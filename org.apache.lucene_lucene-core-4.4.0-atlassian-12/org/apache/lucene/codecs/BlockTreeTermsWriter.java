/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.FieldsConsumer;
import org.apache.lucene.codecs.PostingsConsumer;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.codecs.TermsConsumer;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.BytesRefFSTEnum;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.NoOutputs;
import org.apache.lucene.util.fst.Util;

public class BlockTreeTermsWriter
extends FieldsConsumer {
    public static final int DEFAULT_MIN_BLOCK_SIZE = 25;
    public static final int DEFAULT_MAX_BLOCK_SIZE = 48;
    static final int OUTPUT_FLAGS_NUM_BITS = 2;
    static final int OUTPUT_FLAGS_MASK = 3;
    static final int OUTPUT_FLAG_IS_FLOOR = 1;
    static final int OUTPUT_FLAG_HAS_TERMS = 2;
    static final String TERMS_EXTENSION = "tim";
    static final String TERMS_CODEC_NAME = "BLOCK_TREE_TERMS_DICT";
    public static final int TERMS_VERSION_START = 0;
    public static final int TERMS_VERSION_APPEND_ONLY = 1;
    public static final int TERMS_VERSION_CURRENT = 1;
    static final String TERMS_INDEX_EXTENSION = "tip";
    static final String TERMS_INDEX_CODEC_NAME = "BLOCK_TREE_TERMS_INDEX";
    public static final int TERMS_INDEX_VERSION_START = 0;
    public static final int TERMS_INDEX_VERSION_APPEND_ONLY = 1;
    public static final int TERMS_INDEX_VERSION_CURRENT = 1;
    private final IndexOutput out;
    private final IndexOutput indexOut;
    final int minItemsInBlock;
    final int maxItemsInBlock;
    final PostingsWriterBase postingsWriter;
    final FieldInfos fieldInfos;
    FieldInfo currentField;
    private final List<FieldMetaData> fields;
    final RAMOutputStream scratchBytes;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public BlockTreeTermsWriter(SegmentWriteState state, PostingsWriterBase postingsWriter, int minItemsInBlock, int maxItemsInBlock) throws IOException {
        IndexOutput indexOut;
        block7: {
            this.fields = new ArrayList<FieldMetaData>();
            this.scratchBytes = new RAMOutputStream();
            if (minItemsInBlock <= 1) {
                throw new IllegalArgumentException("minItemsInBlock must be >= 2; got " + minItemsInBlock);
            }
            if (maxItemsInBlock <= 0) {
                throw new IllegalArgumentException("maxItemsInBlock must be >= 1; got " + maxItemsInBlock);
            }
            if (minItemsInBlock > maxItemsInBlock) {
                throw new IllegalArgumentException("maxItemsInBlock must be >= minItemsInBlock; got maxItemsInBlock=" + maxItemsInBlock + " minItemsInBlock=" + minItemsInBlock);
            }
            if (2 * (minItemsInBlock - 1) > maxItemsInBlock) {
                throw new IllegalArgumentException("maxItemsInBlock must be at least 2*(minItemsInBlock-1); got maxItemsInBlock=" + maxItemsInBlock + " minItemsInBlock=" + minItemsInBlock);
            }
            String termsFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, TERMS_EXTENSION);
            this.out = state.directory.createOutput(termsFileName, state.context);
            boolean success = false;
            indexOut = null;
            try {
                this.fieldInfos = state.fieldInfos;
                this.minItemsInBlock = minItemsInBlock;
                this.maxItemsInBlock = maxItemsInBlock;
                this.writeHeader(this.out);
                String termsIndexFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, TERMS_INDEX_EXTENSION);
                indexOut = state.directory.createOutput(termsIndexFileName, state.context);
                this.writeIndexHeader(indexOut);
                this.currentField = null;
                this.postingsWriter = postingsWriter;
                postingsWriter.start(this.out);
                success = true;
                if (success) break block7;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.out, indexOut);
                }
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(this.out, indexOut);
        }
        this.indexOut = indexOut;
    }

    protected void writeHeader(IndexOutput out) throws IOException {
        CodecUtil.writeHeader(out, TERMS_CODEC_NAME, 1);
    }

    protected void writeIndexHeader(IndexOutput out) throws IOException {
        CodecUtil.writeHeader(out, TERMS_INDEX_CODEC_NAME, 1);
    }

    protected void writeTrailer(IndexOutput out, long dirStart) throws IOException {
        out.writeLong(dirStart);
    }

    protected void writeIndexTrailer(IndexOutput indexOut, long dirStart) throws IOException {
        indexOut.writeLong(dirStart);
    }

    @Override
    public TermsConsumer addField(FieldInfo field) throws IOException {
        assert (this.currentField == null || this.currentField.name.compareTo(field.name) < 0);
        this.currentField = field;
        return new TermsWriter(field);
    }

    static long encodeOutput(long fp, boolean hasTerms, boolean isFloor) {
        assert (fp < 0x4000000000000000L);
        return fp << 2 | (long)(hasTerms ? 2 : 0) | (long)(isFloor ? 1 : 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        IOException ioe = null;
        try {
            long dirStart = this.out.getFilePointer();
            long indexDirStart = this.indexOut.getFilePointer();
            this.out.writeVInt(this.fields.size());
            for (FieldMetaData field : this.fields) {
                this.out.writeVInt(field.fieldInfo.number);
                this.out.writeVLong(field.numTerms);
                this.out.writeVInt(field.rootCode.length);
                this.out.writeBytes(field.rootCode.bytes, field.rootCode.offset, field.rootCode.length);
                if (field.fieldInfo.getIndexOptions() != FieldInfo.IndexOptions.DOCS_ONLY) {
                    this.out.writeVLong(field.sumTotalTermFreq);
                }
                this.out.writeVLong(field.sumDocFreq);
                this.out.writeVInt(field.docCount);
                this.indexOut.writeVLong(field.indexStartFP);
            }
            this.writeTrailer(this.out, dirStart);
            this.writeIndexTrailer(this.indexOut, indexDirStart);
        }
        catch (IOException ioe2) {
            try {
                ioe = ioe2;
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException(ioe, this.out, this.indexOut, this.postingsWriter);
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(ioe, this.out, this.indexOut, this.postingsWriter);
        }
        IOUtils.closeWhileHandlingException(ioe, this.out, this.indexOut, this.postingsWriter);
    }

    class TermsWriter
    extends TermsConsumer {
        private final FieldInfo fieldInfo;
        private long numTerms;
        long sumTotalTermFreq;
        long sumDocFreq;
        int docCount;
        long indexStartFP;
        private final NoOutputs noOutputs;
        private final Builder<Object> blockBuilder;
        private final List<PendingEntry> pending = new ArrayList<PendingEntry>();
        private int lastBlockIndex = -1;
        private int[] subBytes = new int[10];
        private int[] subTermCounts = new int[10];
        private int[] subTermCountSums = new int[10];
        private int[] subSubCounts = new int[10];
        private final IntsRef scratchIntsRef = new IntsRef();
        private final RAMOutputStream bytesWriter = new RAMOutputStream();
        private final RAMOutputStream bytesWriter2 = new RAMOutputStream();

        void writeBlocks(IntsRef prevTerm, int prefixLength, int count) throws IOException {
            if (prefixLength == 0 || count <= BlockTreeTermsWriter.this.maxItemsInBlock) {
                PendingBlock nonFloorBlock = this.writeBlock(prevTerm, prefixLength, prefixLength, count, count, 0, false, -1, true);
                nonFloorBlock.compileIndex(null, BlockTreeTermsWriter.this.scratchBytes);
                this.pending.add(nonFloorBlock);
            } else {
                int savLabel = prevTerm.ints[prevTerm.offset + prefixLength];
                List<PendingEntry> slice = this.pending.subList(this.pending.size() - count, this.pending.size());
                int lastSuffixLeadLabel = -1;
                int termCount = 0;
                int subCount = 0;
                int numSubs = 0;
                for (PendingEntry ent : slice) {
                    int suffixLeadLabel;
                    if (ent.isTerm) {
                        PendingTerm term = (PendingTerm)ent;
                        if (term.term.length == prefixLength) {
                            assert (lastSuffixLeadLabel == -1);
                            assert (numSubs == 0);
                            suffixLeadLabel = -1;
                        } else {
                            suffixLeadLabel = term.term.bytes[term.term.offset + prefixLength] & 0xFF;
                        }
                    } else {
                        PendingBlock block = (PendingBlock)ent;
                        assert (block.prefix.length > prefixLength);
                        suffixLeadLabel = block.prefix.bytes[block.prefix.offset + prefixLength] & 0xFF;
                    }
                    if (suffixLeadLabel != lastSuffixLeadLabel && termCount + subCount != 0) {
                        if (this.subBytes.length == numSubs) {
                            this.subBytes = ArrayUtil.grow(this.subBytes);
                            this.subTermCounts = ArrayUtil.grow(this.subTermCounts);
                            this.subSubCounts = ArrayUtil.grow(this.subSubCounts);
                        }
                        this.subBytes[numSubs] = lastSuffixLeadLabel;
                        lastSuffixLeadLabel = suffixLeadLabel;
                        this.subTermCounts[numSubs] = termCount;
                        this.subSubCounts[numSubs] = subCount;
                        subCount = 0;
                        termCount = 0;
                        ++numSubs;
                    }
                    if (ent.isTerm) {
                        ++termCount;
                        continue;
                    }
                    ++subCount;
                }
                if (this.subBytes.length == numSubs) {
                    this.subBytes = ArrayUtil.grow(this.subBytes);
                    this.subTermCounts = ArrayUtil.grow(this.subTermCounts);
                    this.subSubCounts = ArrayUtil.grow(this.subSubCounts);
                }
                this.subBytes[numSubs] = lastSuffixLeadLabel;
                this.subTermCounts[numSubs] = termCount;
                this.subSubCounts[numSubs] = subCount;
                if (this.subTermCountSums.length < ++numSubs) {
                    this.subTermCountSums = ArrayUtil.grow(this.subTermCountSums, numSubs);
                }
                int sum = 0;
                for (int idx = numSubs - 1; idx >= 0; --idx) {
                    this.subTermCountSums[idx] = sum += this.subTermCounts[idx];
                }
                int pendingCount = 0;
                int startLabel = this.subBytes[0];
                int curStart = count;
                subCount = 0;
                ArrayList<PendingBlock> floorBlocks = new ArrayList<PendingBlock>();
                PendingBlock firstBlock = null;
                for (int sub = 0; sub < numSubs; ++sub) {
                    int curPrefixLength;
                    ++subCount;
                    if ((pendingCount += this.subTermCounts[sub] + this.subSubCounts[sub]) < BlockTreeTermsWriter.this.minItemsInBlock) continue;
                    if (startLabel == -1) {
                        curPrefixLength = prefixLength;
                    } else {
                        curPrefixLength = 1 + prefixLength;
                        prevTerm.ints[prevTerm.offset + prefixLength] = startLabel;
                    }
                    PendingBlock floorBlock = this.writeBlock(prevTerm, prefixLength, curPrefixLength, curStart, pendingCount, this.subTermCountSums[1 + sub], true, startLabel, curStart == pendingCount);
                    if (firstBlock == null) {
                        firstBlock = floorBlock;
                    } else {
                        floorBlocks.add(floorBlock);
                    }
                    curStart -= pendingCount;
                    pendingCount = 0;
                    assert (BlockTreeTermsWriter.this.minItemsInBlock == 1 || subCount > 1) : "minItemsInBlock=" + BlockTreeTermsWriter.this.minItemsInBlock + " subCount=" + subCount + " sub=" + sub + " of " + numSubs + " subTermCount=" + this.subTermCountSums[sub] + " subSubCount=" + this.subSubCounts[sub] + " depth=" + prefixLength;
                    subCount = 0;
                    startLabel = this.subBytes[sub + 1];
                    if (curStart == 0) break;
                    if (curStart > BlockTreeTermsWriter.this.maxItemsInBlock) continue;
                    assert (startLabel != -1);
                    assert (firstBlock != null);
                    prevTerm.ints[prevTerm.offset + prefixLength] = startLabel;
                    floorBlocks.add(this.writeBlock(prevTerm, prefixLength, prefixLength + 1, curStart, curStart, 0, true, startLabel, true));
                    break;
                }
                prevTerm.ints[prevTerm.offset + prefixLength] = savLabel;
                assert (firstBlock != null);
                firstBlock.compileIndex(floorBlocks, BlockTreeTermsWriter.this.scratchBytes);
                this.pending.add(firstBlock);
            }
            this.lastBlockIndex = this.pending.size() - 1;
        }

        private String toString(BytesRef b) {
            try {
                return b.utf8ToString() + " " + b;
            }
            catch (Throwable t) {
                return b.toString();
            }
        }

        private PendingBlock writeBlock(IntsRef prevTerm, int prefixLength, int indexPrefixLength, int startBackwards, int length, int futureTermCount, boolean isFloor, int floorLeadByte, boolean isLastInFloor) throws IOException {
            int termCount;
            ArrayList<FST<BytesRef>> subIndices;
            boolean isLeafBlock;
            assert (length > 0);
            int start = this.pending.size() - startBackwards;
            assert (start >= 0) : "pending.size()=" + this.pending.size() + " startBackwards=" + startBackwards + " length=" + length;
            List<PendingEntry> slice = this.pending.subList(start, start + length);
            long startFP = BlockTreeTermsWriter.this.out.getFilePointer();
            BytesRef prefix = new BytesRef(indexPrefixLength);
            for (int m = 0; m < indexPrefixLength; ++m) {
                prefix.bytes[m] = (byte)prevTerm.ints[m];
            }
            prefix.length = indexPrefixLength;
            BlockTreeTermsWriter.this.out.writeVInt(length << 1 | (isLastInFloor ? 1 : 0));
            if (this.lastBlockIndex < start) {
                isLeafBlock = true;
            } else if (!isFloor) {
                isLeafBlock = false;
            } else {
                boolean v = true;
                for (PendingEntry ent : slice) {
                    if (ent.isTerm) continue;
                    v = false;
                    break;
                }
                isLeafBlock = v;
            }
            if (isLeafBlock) {
                subIndices = null;
                for (PendingEntry ent : slice) {
                    assert (ent.isTerm);
                    PendingTerm term = (PendingTerm)ent;
                    int suffix = term.term.length - prefixLength;
                    this.bytesWriter.writeVInt(suffix);
                    this.bytesWriter.writeBytes(term.term.bytes, prefixLength, suffix);
                    this.bytesWriter2.writeVInt(term.stats.docFreq);
                    if (this.fieldInfo.getIndexOptions() == FieldInfo.IndexOptions.DOCS_ONLY) continue;
                    assert (term.stats.totalTermFreq >= (long)term.stats.docFreq) : term.stats.totalTermFreq + " vs " + term.stats.docFreq;
                    this.bytesWriter2.writeVLong(term.stats.totalTermFreq - (long)term.stats.docFreq);
                }
                termCount = length;
            } else {
                subIndices = new ArrayList<FST<BytesRef>>();
                termCount = 0;
                for (PendingEntry ent : slice) {
                    int suffix;
                    if (ent.isTerm) {
                        PendingTerm term = (PendingTerm)ent;
                        suffix = term.term.length - prefixLength;
                        this.bytesWriter.writeVInt(suffix << 1);
                        this.bytesWriter.writeBytes(term.term.bytes, prefixLength, suffix);
                        this.bytesWriter2.writeVInt(term.stats.docFreq);
                        if (this.fieldInfo.getIndexOptions() != FieldInfo.IndexOptions.DOCS_ONLY) {
                            assert (term.stats.totalTermFreq >= (long)term.stats.docFreq);
                            this.bytesWriter2.writeVLong(term.stats.totalTermFreq - (long)term.stats.docFreq);
                        }
                        ++termCount;
                        continue;
                    }
                    PendingBlock block = (PendingBlock)ent;
                    suffix = block.prefix.length - prefixLength;
                    assert (suffix > 0);
                    this.bytesWriter.writeVInt(suffix << 1 | 1);
                    this.bytesWriter.writeBytes(block.prefix.bytes, prefixLength, suffix);
                    assert (block.fp < startFP);
                    this.bytesWriter.writeVLong(startFP - block.fp);
                    subIndices.add(block.index);
                }
                assert (subIndices.size() != 0);
            }
            BlockTreeTermsWriter.this.out.writeVInt((int)(this.bytesWriter.getFilePointer() << 1) | (isLeafBlock ? 1 : 0));
            this.bytesWriter.writeTo(BlockTreeTermsWriter.this.out);
            this.bytesWriter.reset();
            BlockTreeTermsWriter.this.out.writeVInt((int)this.bytesWriter2.getFilePointer());
            this.bytesWriter2.writeTo(BlockTreeTermsWriter.this.out);
            this.bytesWriter2.reset();
            BlockTreeTermsWriter.this.postingsWriter.flushTermsBlock(futureTermCount + termCount, termCount);
            slice.clear();
            if (this.lastBlockIndex >= start) {
                this.lastBlockIndex = this.lastBlockIndex < start + length ? start : (this.lastBlockIndex -= length);
            }
            return new PendingBlock(prefix, startFP, termCount != 0, isFloor, floorLeadByte, subIndices);
        }

        TermsWriter(FieldInfo fieldInfo) {
            this.fieldInfo = fieldInfo;
            this.noOutputs = NoOutputs.getSingleton();
            this.blockBuilder = new Builder<Object>(FST.INPUT_TYPE.BYTE1, 0, 0, true, true, Integer.MAX_VALUE, this.noOutputs, new FindBlocks(), false, 0.0f, true, 15);
            BlockTreeTermsWriter.this.postingsWriter.setField(fieldInfo);
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public PostingsConsumer startTerm(BytesRef text) throws IOException {
            BlockTreeTermsWriter.this.postingsWriter.startTerm();
            return BlockTreeTermsWriter.this.postingsWriter;
        }

        @Override
        public void finishTerm(BytesRef text, TermStats stats) throws IOException {
            assert (stats.docFreq > 0);
            this.blockBuilder.add(Util.toIntsRef(text, this.scratchIntsRef), this.noOutputs.getNoOutput());
            this.pending.add(new PendingTerm(BytesRef.deepCopyOf(text), stats));
            BlockTreeTermsWriter.this.postingsWriter.finishTerm(stats);
            ++this.numTerms;
        }

        @Override
        public void finish(long sumTotalTermFreq, long sumDocFreq, int docCount) throws IOException {
            if (this.numTerms > 0L) {
                this.blockBuilder.finish();
                assert (this.pending.size() == 1 && !this.pending.get((int)0).isTerm) : "pending.size()=" + this.pending.size() + " pending=" + this.pending;
                PendingBlock root = (PendingBlock)this.pending.get(0);
                assert (root.prefix.length == 0);
                assert (root.index.getEmptyOutput() != null);
                this.sumTotalTermFreq = sumTotalTermFreq;
                this.sumDocFreq = sumDocFreq;
                this.docCount = docCount;
                this.indexStartFP = BlockTreeTermsWriter.this.indexOut.getFilePointer();
                root.index.save(BlockTreeTermsWriter.this.indexOut);
                BlockTreeTermsWriter.this.fields.add(new FieldMetaData(this.fieldInfo, ((PendingBlock)this.pending.get((int)0)).index.getEmptyOutput(), this.numTerms, this.indexStartFP, sumTotalTermFreq, sumDocFreq, docCount));
            } else {
                assert (sumTotalTermFreq == 0L || this.fieldInfo.getIndexOptions() == FieldInfo.IndexOptions.DOCS_ONLY && sumTotalTermFreq == -1L);
                assert (sumDocFreq == 0L);
                assert (docCount == 0);
            }
        }

        private class FindBlocks
        extends Builder.FreezeTail<Object> {
            private FindBlocks() {
            }

            @Override
            public void freeze(Builder.UnCompiledNode<Object>[] frontier, int prefixLenPlus1, IntsRef lastInput) throws IOException {
                for (int idx = lastInput.length; idx >= prefixLenPlus1; --idx) {
                    Builder.UnCompiledNode<Object> node = frontier[idx];
                    long totCount = 0L;
                    if (node.isFinal) {
                        ++totCount;
                    }
                    for (int arcIdx = 0; arcIdx < node.numArcs; ++arcIdx) {
                        Builder.UnCompiledNode target = (Builder.UnCompiledNode)node.arcs[arcIdx].target;
                        totCount += target.inputCount;
                        target.clear();
                        node.arcs[arcIdx].target = null;
                    }
                    node.numArcs = 0;
                    if (totCount >= (long)BlockTreeTermsWriter.this.minItemsInBlock || idx == 0) {
                        TermsWriter.this.writeBlocks(lastInput, idx, (int)totCount);
                        node.inputCount = 1L;
                    } else {
                        node.inputCount = totCount;
                    }
                    frontier[idx] = new Builder.UnCompiledNode(TermsWriter.this.blockBuilder, idx);
                }
            }
        }
    }

    private static final class PendingBlock
    extends PendingEntry {
        public final BytesRef prefix;
        public final long fp;
        public FST<BytesRef> index;
        public List<FST<BytesRef>> subIndices;
        public final boolean hasTerms;
        public final boolean isFloor;
        public final int floorLeadByte;
        private final IntsRef scratchIntsRef = new IntsRef();

        public PendingBlock(BytesRef prefix, long fp, boolean hasTerms, boolean isFloor, int floorLeadByte, List<FST<BytesRef>> subIndices) {
            super(false);
            this.prefix = prefix;
            this.fp = fp;
            this.hasTerms = hasTerms;
            this.isFloor = isFloor;
            this.floorLeadByte = floorLeadByte;
            this.subIndices = subIndices;
        }

        public String toString() {
            return "BLOCK: " + this.prefix.utf8ToString();
        }

        public void compileIndex(List<PendingBlock> floorBlocks, RAMOutputStream scratchBytes) throws IOException {
            assert (this.isFloor && floorBlocks != null && floorBlocks.size() != 0 || !this.isFloor && floorBlocks == null) : "isFloor=" + this.isFloor + " floorBlocks=" + floorBlocks;
            assert (scratchBytes.getFilePointer() == 0L);
            scratchBytes.writeVLong(BlockTreeTermsWriter.encodeOutput(this.fp, this.hasTerms, this.isFloor));
            if (this.isFloor) {
                scratchBytes.writeVInt(floorBlocks.size());
                for (PendingBlock sub : floorBlocks) {
                    assert (sub.floorLeadByte != -1);
                    scratchBytes.writeByte((byte)sub.floorLeadByte);
                    assert (sub.fp > this.fp);
                    scratchBytes.writeVLong(sub.fp - this.fp << 1 | (long)(sub.hasTerms ? 1 : 0));
                }
            }
            ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            Builder<BytesRef> indexBuilder = new Builder<BytesRef>(FST.INPUT_TYPE.BYTE1, 0, 0, true, false, Integer.MAX_VALUE, outputs, null, false, 0.0f, true, 15);
            byte[] bytes = new byte[(int)scratchBytes.getFilePointer()];
            assert (bytes.length > 0);
            scratchBytes.writeTo(bytes, 0);
            indexBuilder.add(Util.toIntsRef(this.prefix, this.scratchIntsRef), new BytesRef(bytes, 0, bytes.length));
            scratchBytes.reset();
            if (this.subIndices != null) {
                for (FST fST : this.subIndices) {
                    this.append(indexBuilder, fST);
                }
            }
            if (floorBlocks != null) {
                for (PendingBlock pendingBlock : floorBlocks) {
                    if (pendingBlock.subIndices != null) {
                        for (FST<BytesRef> subIndex : pendingBlock.subIndices) {
                            this.append(indexBuilder, subIndex);
                        }
                    }
                    pendingBlock.subIndices = null;
                }
            }
            this.index = indexBuilder.finish();
            this.subIndices = null;
        }

        private void append(Builder<BytesRef> builder, FST<BytesRef> subIndex) throws IOException {
            BytesRefFSTEnum.InputOutput<BytesRef> indexEnt;
            BytesRefFSTEnum<BytesRef> subIndexEnum = new BytesRefFSTEnum<BytesRef>(subIndex);
            while ((indexEnt = subIndexEnum.next()) != null) {
                builder.add(Util.toIntsRef(indexEnt.input, this.scratchIntsRef), (BytesRef)indexEnt.output);
            }
        }
    }

    private static final class PendingTerm
    extends PendingEntry {
        public final BytesRef term;
        public final TermStats stats;

        public PendingTerm(BytesRef term, TermStats stats) {
            super(true);
            this.term = term;
            this.stats = stats;
        }

        public String toString() {
            return this.term.utf8ToString();
        }
    }

    private static class PendingEntry {
        public final boolean isTerm;

        protected PendingEntry(boolean isTerm) {
            this.isTerm = isTerm;
        }
    }

    private static class FieldMetaData {
        public final FieldInfo fieldInfo;
        public final BytesRef rootCode;
        public final long numTerms;
        public final long indexStartFP;
        public final long sumTotalTermFreq;
        public final long sumDocFreq;
        public final int docCount;

        public FieldMetaData(FieldInfo fieldInfo, BytesRef rootCode, long numTerms, long indexStartFP, long sumTotalTermFreq, long sumDocFreq, int docCount) {
            assert (numTerms > 0L);
            this.fieldInfo = fieldInfo;
            assert (rootCode != null) : "field=" + fieldInfo.name + " numTerms=" + numTerms;
            this.rootCode = rootCode;
            this.indexStartFP = indexStartFP;
            this.numTerms = numTerms;
            this.sumTotalTermFreq = sumTotalTermFreq;
            this.sumDocFreq = sumDocFreq;
            this.docCount = docCount;
        }
    }
}

