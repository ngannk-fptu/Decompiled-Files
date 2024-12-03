/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.RunAutomaton;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Util;

public class BlockTreeTermsReader
extends FieldsProducer {
    private final IndexInput in;
    private final PostingsReaderBase postingsReader;
    private final TreeMap<String, FieldReader> fields = new TreeMap();
    private long dirOffset;
    private long indexDirOffset;
    private String segment;
    private final int version;
    final Outputs<BytesRef> fstOutputs = ByteSequenceOutputs.getSingleton();
    final BytesRef NO_OUTPUT = this.fstOutputs.getNoOutput();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public BlockTreeTermsReader(Directory dir, FieldInfos fieldInfos, SegmentInfo info, PostingsReaderBase postingsReader, IOContext ioContext, String segmentSuffix, int indexDivisor) throws IOException {
        this.postingsReader = postingsReader;
        this.segment = info.name;
        this.in = dir.openInput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "tim"), ioContext);
        boolean success = false;
        IndexInput indexIn = null;
        try {
            int numFields;
            int indexVersion;
            this.version = this.readHeader(this.in);
            if (indexDivisor != -1 && (indexVersion = this.readIndexHeader(indexIn = dir.openInput(IndexFileNames.segmentFileName(this.segment, segmentSuffix, "tip"), ioContext))) != this.version) {
                throw new CorruptIndexException("mixmatched version files: " + this.in + "=" + this.version + "," + indexIn + "=" + indexVersion);
            }
            postingsReader.init(this.in);
            this.seekDir(this.in, this.dirOffset);
            if (indexDivisor != -1) {
                this.seekDir(indexIn, this.indexDirOffset);
            }
            if ((numFields = this.in.readVInt()) < 0) {
                throw new CorruptIndexException("invalid numFields: " + numFields + " (resource=" + this.in + ")");
            }
            for (int i = 0; i < numFields; ++i) {
                int field = this.in.readVInt();
                long numTerms = this.in.readVLong();
                assert (numTerms >= 0L);
                int numBytes = this.in.readVInt();
                BytesRef rootCode = new BytesRef(new byte[numBytes]);
                this.in.readBytes(rootCode.bytes, 0, numBytes);
                rootCode.length = numBytes;
                FieldInfo fieldInfo = fieldInfos.fieldInfo(field);
                assert (fieldInfo != null) : "field=" + field;
                long sumTotalTermFreq = fieldInfo.getIndexOptions() == FieldInfo.IndexOptions.DOCS_ONLY ? -1L : this.in.readVLong();
                long sumDocFreq = this.in.readVLong();
                int docCount = this.in.readVInt();
                if (docCount < 0 || docCount > info.getDocCount()) {
                    throw new CorruptIndexException("invalid docCount: " + docCount + " maxDoc: " + info.getDocCount() + " (resource=" + this.in + ")");
                }
                if (sumDocFreq < (long)docCount) {
                    throw new CorruptIndexException("invalid sumDocFreq: " + sumDocFreq + " docCount: " + docCount + " (resource=" + this.in + ")");
                }
                if (sumTotalTermFreq != -1L && sumTotalTermFreq < sumDocFreq) {
                    throw new CorruptIndexException("invalid sumTotalTermFreq: " + sumTotalTermFreq + " sumDocFreq: " + sumDocFreq + " (resource=" + this.in + ")");
                }
                long indexStartFP = indexDivisor != -1 ? indexIn.readVLong() : 0L;
                FieldReader previous = this.fields.put(fieldInfo.name, new FieldReader(fieldInfo, numTerms, rootCode, sumTotalTermFreq, sumDocFreq, docCount, indexStartFP, indexIn));
                if (previous == null) continue;
                throw new CorruptIndexException("duplicate field: " + fieldInfo.name + " (resource=" + this.in + ")");
            }
            if (indexDivisor != -1) {
                indexIn.close();
            }
            if (success = true) return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(indexIn, this);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(indexIn, this);
    }

    protected int readHeader(IndexInput input) throws IOException {
        int version = CodecUtil.checkHeader(input, "BLOCK_TREE_TERMS_DICT", 0, 1);
        if (version < 1) {
            this.dirOffset = input.readLong();
        }
        return version;
    }

    protected int readIndexHeader(IndexInput input) throws IOException {
        int version = CodecUtil.checkHeader(input, "BLOCK_TREE_TERMS_INDEX", 0, 1);
        if (version < 1) {
            this.indexDirOffset = input.readLong();
        }
        return version;
    }

    protected void seekDir(IndexInput input, long dirOffset) throws IOException {
        if (this.version >= 1) {
            input.seek(input.length() - 8L);
            dirOffset = input.readLong();
        }
        input.seek(dirOffset);
    }

    @Override
    public void close() throws IOException {
        try {
            IOUtils.close(this.in, this.postingsReader);
        }
        finally {
            this.fields.clear();
        }
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet(this.fields.keySet()).iterator();
    }

    @Override
    public Terms terms(String field) throws IOException {
        assert (field != null);
        return this.fields.get(field);
    }

    @Override
    public int size() {
        return this.fields.size();
    }

    String brToString(BytesRef b) {
        if (b == null) {
            return "null";
        }
        try {
            return b.utf8ToString() + " " + b;
        }
        catch (Throwable t) {
            return b.toString();
        }
    }

    public final class FieldReader
    extends Terms {
        final long numTerms;
        final FieldInfo fieldInfo;
        final long sumTotalTermFreq;
        final long sumDocFreq;
        final int docCount;
        final long indexStartFP;
        final long rootBlockFP;
        final BytesRef rootCode;
        private final FST<BytesRef> index;

        FieldReader(FieldInfo fieldInfo, long numTerms, BytesRef rootCode, long sumTotalTermFreq, long sumDocFreq, int docCount, long indexStartFP, IndexInput indexIn) throws IOException {
            assert (numTerms > 0L);
            this.fieldInfo = fieldInfo;
            this.numTerms = numTerms;
            this.sumTotalTermFreq = sumTotalTermFreq;
            this.sumDocFreq = sumDocFreq;
            this.docCount = docCount;
            this.indexStartFP = indexStartFP;
            this.rootCode = rootCode;
            this.rootBlockFP = new ByteArrayDataInput(rootCode.bytes, rootCode.offset, rootCode.length).readVLong() >>> 2;
            if (indexIn != null) {
                IndexInput clone = indexIn.clone();
                clone.seek(indexStartFP);
                this.index = new FST<BytesRef>(clone, ByteSequenceOutputs.getSingleton());
            } else {
                this.index = null;
            }
        }

        public Stats computeStats() throws IOException {
            return new SegmentTermsEnum().computeBlockStats();
        }

        @Override
        public Comparator<BytesRef> getComparator() {
            return BytesRef.getUTF8SortedAsUnicodeComparator();
        }

        @Override
        public boolean hasOffsets() {
            return this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        }

        @Override
        public boolean hasPositions() {
            return this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        }

        @Override
        public boolean hasPayloads() {
            return this.fieldInfo.hasPayloads();
        }

        @Override
        public TermsEnum iterator(TermsEnum reuse) throws IOException {
            return new SegmentTermsEnum();
        }

        @Override
        public long size() {
            return this.numTerms;
        }

        @Override
        public long getSumTotalTermFreq() {
            return this.sumTotalTermFreq;
        }

        @Override
        public long getSumDocFreq() {
            return this.sumDocFreq;
        }

        @Override
        public int getDocCount() {
            return this.docCount;
        }

        @Override
        public TermsEnum intersect(CompiledAutomaton compiled, BytesRef startTerm) throws IOException {
            if (compiled.type != CompiledAutomaton.AUTOMATON_TYPE.NORMAL) {
                throw new IllegalArgumentException("please use CompiledAutomaton.getTermsEnum instead");
            }
            return new IntersectEnum(compiled, startTerm);
        }

        private final class SegmentTermsEnum
        extends TermsEnum {
            private IndexInput in;
            private Frame[] stack;
            private final Frame staticFrame;
            private Frame currentFrame;
            private boolean termExists;
            private int targetBeforeCurrentLength;
            private final ByteArrayDataInput scratchReader = new ByteArrayDataInput();
            private int validIndexPrefix;
            private boolean eof;
            final BytesRef term = new BytesRef();
            private final FST.BytesReader fstReader;
            private FST.Arc<BytesRef>[] arcs = new FST.Arc[1];

            public SegmentTermsEnum() throws IOException {
                this.stack = new Frame[0];
                this.staticFrame = new Frame(-1);
                this.fstReader = FieldReader.this.index == null ? null : FieldReader.this.index.getBytesReader();
                for (int arcIdx = 0; arcIdx < this.arcs.length; ++arcIdx) {
                    this.arcs[arcIdx] = new FST.Arc();
                }
                this.currentFrame = this.staticFrame;
                if (FieldReader.this.index != null) {
                    FST.Arc<BytesRef> arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                    assert (arc.isFinal());
                } else {
                    Object arc = null;
                }
                this.currentFrame = this.staticFrame;
                this.validIndexPrefix = 0;
            }

            void initIndexInput() {
                if (this.in == null) {
                    this.in = BlockTreeTermsReader.this.in.clone();
                }
            }

            public Stats computeBlockStats() throws IOException {
                FST.Arc<BytesRef> arc;
                Stats stats = new Stats(BlockTreeTermsReader.this.segment, FieldReader.this.fieldInfo.name);
                if (FieldReader.this.index != null) {
                    stats.indexNodeCount = FieldReader.this.index.getNodeCount();
                    stats.indexArcCount = FieldReader.this.index.getArcCount();
                    stats.indexNumBytes = FieldReader.this.index.sizeInBytes();
                }
                this.currentFrame = this.staticFrame;
                if (FieldReader.this.index != null) {
                    arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                    assert (arc.isFinal());
                } else {
                    arc = null;
                }
                this.currentFrame = this.pushFrame(arc, FieldReader.this.rootCode, 0);
                this.currentFrame.fpOrig = this.currentFrame.fp;
                this.currentFrame.loadBlock();
                this.validIndexPrefix = 0;
                stats.startBlock(this.currentFrame, !this.currentFrame.isLastInFloor);
                while (true) {
                    if (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                        stats.endBlock(this.currentFrame);
                        if (!this.currentFrame.isLastInFloor) {
                            this.currentFrame.loadNextFloorBlock();
                            stats.startBlock(this.currentFrame, true);
                            continue;
                        }
                        if (this.currentFrame.ord == 0) break;
                        long lastFP = this.currentFrame.fpOrig;
                        this.currentFrame = this.stack[this.currentFrame.ord - 1];
                        assert (lastFP == this.currentFrame.lastSubFP);
                        continue;
                    }
                    while (this.currentFrame.next()) {
                        this.currentFrame = this.pushFrame(null, this.currentFrame.lastSubFP, this.term.length);
                        this.currentFrame.fpOrig = this.currentFrame.fp;
                        this.currentFrame.isFloor = false;
                        this.currentFrame.loadBlock();
                        stats.startBlock(this.currentFrame, !this.currentFrame.isLastInFloor);
                    }
                    stats.term(this.term);
                }
                stats.finish();
                this.currentFrame = this.staticFrame;
                if (FieldReader.this.index != null) {
                    arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                    assert (arc.isFinal());
                } else {
                    arc = null;
                }
                this.currentFrame = this.pushFrame(arc, FieldReader.this.rootCode, 0);
                this.currentFrame.rewind();
                this.currentFrame.loadBlock();
                this.validIndexPrefix = 0;
                this.term.length = 0;
                return stats;
            }

            private Frame getFrame(int ord) throws IOException {
                if (ord >= this.stack.length) {
                    Frame[] next = new Frame[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    System.arraycopy(this.stack, 0, next, 0, this.stack.length);
                    for (int stackOrd = this.stack.length; stackOrd < next.length; ++stackOrd) {
                        next[stackOrd] = new Frame(stackOrd);
                    }
                    this.stack = next;
                }
                assert (this.stack[ord].ord == ord);
                return this.stack[ord];
            }

            private FST.Arc<BytesRef> getArc(int ord) {
                if (ord >= this.arcs.length) {
                    FST.Arc[] next = new FST.Arc[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    System.arraycopy(this.arcs, 0, next, 0, this.arcs.length);
                    for (int arcOrd = this.arcs.length; arcOrd < next.length; ++arcOrd) {
                        next[arcOrd] = new FST.Arc();
                    }
                    this.arcs = next;
                }
                return this.arcs[ord];
            }

            @Override
            public Comparator<BytesRef> getComparator() {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }

            Frame pushFrame(FST.Arc<BytesRef> arc, BytesRef frameData, int length) throws IOException {
                this.scratchReader.reset(frameData.bytes, frameData.offset, frameData.length);
                long code = this.scratchReader.readVLong();
                long fpSeek = code >>> 2;
                Frame f = this.getFrame(1 + this.currentFrame.ord);
                f.hasTermsOrig = f.hasTerms = (code & 2L) != 0L;
                boolean bl = f.isFloor = (code & 1L) != 0L;
                if (f.isFloor) {
                    f.setFloorData(this.scratchReader, frameData);
                }
                this.pushFrame(arc, fpSeek, length);
                return f;
            }

            Frame pushFrame(FST.Arc<BytesRef> arc, long fp, int length) throws IOException {
                Frame f = this.getFrame(1 + this.currentFrame.ord);
                f.arc = arc;
                if (f.fpOrig == fp && f.nextEnt != -1) {
                    if (f.prefix > this.targetBeforeCurrentLength) {
                        f.rewind();
                    }
                    assert (length == f.prefix);
                } else {
                    f.nextEnt = -1;
                    f.prefix = length;
                    f.state.termBlockOrd = 0;
                    f.fpOrig = f.fp = fp;
                    f.lastSubFP = -1L;
                }
                return f;
            }

            private boolean clearEOF() {
                this.eof = false;
                return true;
            }

            private boolean setEOF() {
                this.eof = true;
                return true;
            }

            @Override
            public boolean seekExact(BytesRef target, boolean useCache) throws IOException {
                int targetUpto;
                BytesRef output;
                FST.Arc<BytesRef> arc;
                if (FieldReader.this.index == null) {
                    throw new IllegalStateException("terms index was not loaded");
                }
                if (this.term.bytes.length <= target.length) {
                    this.term.bytes = ArrayUtil.grow(this.term.bytes, 1 + target.length);
                }
                assert (this.clearEOF());
                this.targetBeforeCurrentLength = this.currentFrame.ord;
                if (this.currentFrame != this.staticFrame) {
                    arc = this.arcs[0];
                    assert (arc.isFinal());
                    output = (BytesRef)arc.output;
                    Frame lastFrame = this.stack[0];
                    assert (this.validIndexPrefix <= this.term.length);
                    int targetLimit = Math.min(target.length, this.validIndexPrefix);
                    int cmp = 0;
                    for (targetUpto = 0; targetUpto < targetLimit && (cmp = (this.term.bytes[targetUpto] & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF)) == 0; ++targetUpto) {
                        arc = this.arcs[1 + targetUpto];
                        assert (arc.label == (target.bytes[target.offset + targetUpto] & 0xFF)) : "arc.label=" + (char)arc.label + " targetLabel=" + (char)(target.bytes[target.offset + targetUpto] & 0xFF);
                        if (arc.output != BlockTreeTermsReader.this.NO_OUTPUT) {
                            output = BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.output);
                        }
                        if (!arc.isFinal()) continue;
                        lastFrame = this.stack[1 + lastFrame.ord];
                    }
                    if (cmp == 0) {
                        int targetUptoMid = targetUpto;
                        int targetLimit2 = Math.min(target.length, this.term.length);
                        while (targetUpto < targetLimit2 && (cmp = (this.term.bytes[targetUpto] & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF)) == 0) {
                            ++targetUpto;
                        }
                        if (cmp == 0) {
                            cmp = this.term.length - target.length;
                        }
                        targetUpto = targetUptoMid;
                    }
                    if (cmp < 0) {
                        this.currentFrame = lastFrame;
                    } else if (cmp > 0) {
                        this.targetBeforeCurrentLength = 0;
                        this.currentFrame = lastFrame;
                        this.currentFrame.rewind();
                    } else {
                        assert (this.term.length == target.length);
                        if (this.termExists) {
                            return true;
                        }
                    }
                } else {
                    this.targetBeforeCurrentLength = -1;
                    arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                    assert (arc.isFinal());
                    assert (arc.output != null);
                    output = (BytesRef)arc.output;
                    this.currentFrame = this.staticFrame;
                    targetUpto = 0;
                    this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.nextFinalOutput), 0);
                }
                while (targetUpto < target.length) {
                    int targetLabel = target.bytes[target.offset + targetUpto] & 0xFF;
                    FST.Arc<BytesRef> nextArc = FieldReader.this.index.findTargetArc(targetLabel, arc, this.getArc(1 + targetUpto), this.fstReader);
                    if (nextArc == null) {
                        this.validIndexPrefix = this.currentFrame.prefix;
                        this.currentFrame.scanToFloorFrame(target);
                        if (!this.currentFrame.hasTerms) {
                            this.termExists = false;
                            this.term.bytes[targetUpto] = (byte)targetLabel;
                            this.term.length = 1 + targetUpto;
                            return false;
                        }
                        this.currentFrame.loadBlock();
                        TermsEnum.SeekStatus result = this.currentFrame.scanToTerm(target, true);
                        return result == TermsEnum.SeekStatus.FOUND;
                    }
                    arc = nextArc;
                    this.term.bytes[targetUpto] = (byte)targetLabel;
                    assert (arc.output != null);
                    if (arc.output != BlockTreeTermsReader.this.NO_OUTPUT) {
                        output = BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.output);
                    }
                    ++targetUpto;
                    if (!arc.isFinal()) continue;
                    this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.nextFinalOutput), targetUpto);
                }
                this.validIndexPrefix = this.currentFrame.prefix;
                this.currentFrame.scanToFloorFrame(target);
                if (!this.currentFrame.hasTerms) {
                    this.termExists = false;
                    this.term.length = targetUpto;
                    return false;
                }
                this.currentFrame.loadBlock();
                TermsEnum.SeekStatus result = this.currentFrame.scanToTerm(target, true);
                return result == TermsEnum.SeekStatus.FOUND;
            }

            @Override
            public TermsEnum.SeekStatus seekCeil(BytesRef target, boolean useCache) throws IOException {
                int targetUpto;
                BytesRef output;
                FST.Arc<BytesRef> arc;
                if (FieldReader.this.index == null) {
                    throw new IllegalStateException("terms index was not loaded");
                }
                if (this.term.bytes.length <= target.length) {
                    this.term.bytes = ArrayUtil.grow(this.term.bytes, 1 + target.length);
                }
                assert (this.clearEOF());
                this.targetBeforeCurrentLength = this.currentFrame.ord;
                if (this.currentFrame != this.staticFrame) {
                    arc = this.arcs[0];
                    assert (arc.isFinal());
                    output = (BytesRef)arc.output;
                    Frame lastFrame = this.stack[0];
                    assert (this.validIndexPrefix <= this.term.length);
                    int targetLimit = Math.min(target.length, this.validIndexPrefix);
                    int cmp = 0;
                    for (targetUpto = 0; targetUpto < targetLimit && (cmp = (this.term.bytes[targetUpto] & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF)) == 0; ++targetUpto) {
                        arc = this.arcs[1 + targetUpto];
                        assert (arc.label == (target.bytes[target.offset + targetUpto] & 0xFF)) : "arc.label=" + (char)arc.label + " targetLabel=" + (char)(target.bytes[target.offset + targetUpto] & 0xFF);
                        if (arc.output != BlockTreeTermsReader.this.NO_OUTPUT) {
                            output = BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.output);
                        }
                        if (!arc.isFinal()) continue;
                        lastFrame = this.stack[1 + lastFrame.ord];
                    }
                    if (cmp == 0) {
                        int targetUptoMid = targetUpto;
                        int targetLimit2 = Math.min(target.length, this.term.length);
                        while (targetUpto < targetLimit2 && (cmp = (this.term.bytes[targetUpto] & 0xFF) - (target.bytes[target.offset + targetUpto] & 0xFF)) == 0) {
                            ++targetUpto;
                        }
                        if (cmp == 0) {
                            cmp = this.term.length - target.length;
                        }
                        targetUpto = targetUptoMid;
                    }
                    if (cmp < 0) {
                        this.currentFrame = lastFrame;
                    } else if (cmp > 0) {
                        this.targetBeforeCurrentLength = 0;
                        this.currentFrame = lastFrame;
                        this.currentFrame.rewind();
                    } else {
                        assert (this.term.length == target.length);
                        if (this.termExists) {
                            return TermsEnum.SeekStatus.FOUND;
                        }
                    }
                } else {
                    this.targetBeforeCurrentLength = -1;
                    arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                    assert (arc.isFinal());
                    assert (arc.output != null);
                    output = (BytesRef)arc.output;
                    this.currentFrame = this.staticFrame;
                    targetUpto = 0;
                    this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.nextFinalOutput), 0);
                }
                while (targetUpto < target.length) {
                    int targetLabel = target.bytes[target.offset + targetUpto] & 0xFF;
                    FST.Arc<BytesRef> nextArc = FieldReader.this.index.findTargetArc(targetLabel, arc, this.getArc(1 + targetUpto), this.fstReader);
                    if (nextArc == null) {
                        this.validIndexPrefix = this.currentFrame.prefix;
                        this.currentFrame.scanToFloorFrame(target);
                        this.currentFrame.loadBlock();
                        TermsEnum.SeekStatus result = this.currentFrame.scanToTerm(target, false);
                        if (result == TermsEnum.SeekStatus.END) {
                            this.term.copyBytes(target);
                            this.termExists = false;
                            if (this.next() != null) {
                                return TermsEnum.SeekStatus.NOT_FOUND;
                            }
                            return TermsEnum.SeekStatus.END;
                        }
                        return result;
                    }
                    this.term.bytes[targetUpto] = (byte)targetLabel;
                    arc = nextArc;
                    assert (arc.output != null);
                    if (arc.output != BlockTreeTermsReader.this.NO_OUTPUT) {
                        output = BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.output);
                    }
                    ++targetUpto;
                    if (!arc.isFinal()) continue;
                    this.currentFrame = this.pushFrame(arc, BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.nextFinalOutput), targetUpto);
                }
                this.validIndexPrefix = this.currentFrame.prefix;
                this.currentFrame.scanToFloorFrame(target);
                this.currentFrame.loadBlock();
                TermsEnum.SeekStatus result = this.currentFrame.scanToTerm(target, false);
                if (result == TermsEnum.SeekStatus.END) {
                    this.term.copyBytes(target);
                    this.termExists = false;
                    if (this.next() != null) {
                        return TermsEnum.SeekStatus.NOT_FOUND;
                    }
                    return TermsEnum.SeekStatus.END;
                }
                return result;
            }

            private void printSeekState(PrintStream out) throws IOException {
                if (this.currentFrame == this.staticFrame) {
                    out.println("  no prior seek");
                } else {
                    out.println("  prior seek state:");
                    int ord = 0;
                    boolean isSeekFrame = true;
                    while (true) {
                        Frame f = this.getFrame(ord);
                        assert (f != null);
                        BytesRef prefix = new BytesRef(this.term.bytes, 0, f.prefix);
                        if (f.nextEnt == -1) {
                            out.println("    frame " + (isSeekFrame ? "(seek)" : "(next)") + " ord=" + ord + " fp=" + f.fp + (f.isFloor ? " (fpOrig=" + f.fpOrig + ")" : "") + " prefixLen=" + f.prefix + " prefix=" + prefix + (f.nextEnt == -1 ? "" : " (of " + f.entCount + ")") + " hasTerms=" + f.hasTerms + " isFloor=" + f.isFloor + " code=" + ((f.fp << 2) + (long)(f.hasTerms ? 2 : 0) + (long)(f.isFloor ? 1 : 0)) + " isLastInFloor=" + f.isLastInFloor + " mdUpto=" + f.metaDataUpto + " tbOrd=" + f.getTermBlockOrd());
                        } else {
                            out.println("    frame " + (isSeekFrame ? "(seek, loaded)" : "(next, loaded)") + " ord=" + ord + " fp=" + f.fp + (f.isFloor ? " (fpOrig=" + f.fpOrig + ")" : "") + " prefixLen=" + f.prefix + " prefix=" + prefix + " nextEnt=" + f.nextEnt + (f.nextEnt == -1 ? "" : " (of " + f.entCount + ")") + " hasTerms=" + f.hasTerms + " isFloor=" + f.isFloor + " code=" + ((f.fp << 2) + (long)(f.hasTerms ? 2 : 0) + (long)(f.isFloor ? 1 : 0)) + " lastSubFP=" + f.lastSubFP + " isLastInFloor=" + f.isLastInFloor + " mdUpto=" + f.metaDataUpto + " tbOrd=" + f.getTermBlockOrd());
                        }
                        if (FieldReader.this.index != null) {
                            long code;
                            ByteArrayDataInput reader;
                            long codeOrig;
                            assert (!isSeekFrame || f.arc != null) : "isSeekFrame=" + isSeekFrame + " f.arc=" + f.arc;
                            if (f.prefix > 0 && isSeekFrame && f.arc.label != (this.term.bytes[f.prefix - 1] & 0xFF)) {
                                out.println("      broken seek state: arc.label=" + (char)f.arc.label + " vs term byte=" + (char)(this.term.bytes[f.prefix - 1] & 0xFF));
                                throw new RuntimeException("seek state is broken");
                            }
                            BytesRef output = (BytesRef)Util.get(FieldReader.this.index, prefix);
                            if (output == null) {
                                out.println("      broken seek state: prefix is not final in index");
                                throw new RuntimeException("seek state is broken");
                            }
                            if (isSeekFrame && !f.isFloor && (codeOrig = (reader = new ByteArrayDataInput(output.bytes, output.offset, output.length)).readVLong()) != (code = f.fp << 2 | (long)(f.hasTerms ? 2 : 0) | (long)(f.isFloor ? 1 : 0))) {
                                out.println("      broken seek state: output code=" + codeOrig + " doesn't match frame code=" + code);
                                throw new RuntimeException("seek state is broken");
                            }
                        }
                        if (f == this.currentFrame) break;
                        if (f.prefix == this.validIndexPrefix) {
                            isSeekFrame = false;
                        }
                        ++ord;
                    }
                }
            }

            @Override
            public BytesRef next() throws IOException {
                if (this.in == null) {
                    FST.Arc<BytesRef> arc;
                    if (FieldReader.this.index != null) {
                        arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                        assert (arc.isFinal());
                    } else {
                        arc = null;
                    }
                    this.currentFrame = this.pushFrame(arc, FieldReader.this.rootCode, 0);
                    this.currentFrame.loadBlock();
                }
                this.targetBeforeCurrentLength = this.currentFrame.ord;
                assert (!this.eof);
                if (this.currentFrame == this.staticFrame) {
                    boolean result = this.seekExact(this.term, false);
                    assert (result);
                }
                while (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                    if (!this.currentFrame.isLastInFloor) {
                        this.currentFrame.loadNextFloorBlock();
                        continue;
                    }
                    if (this.currentFrame.ord == 0) {
                        assert (this.setEOF());
                        this.term.length = 0;
                        this.validIndexPrefix = 0;
                        this.currentFrame.rewind();
                        this.termExists = false;
                        return null;
                    }
                    long lastFP = this.currentFrame.fpOrig;
                    this.currentFrame = this.stack[this.currentFrame.ord - 1];
                    if (this.currentFrame.nextEnt == -1 || this.currentFrame.lastSubFP != lastFP) {
                        this.currentFrame.scanToFloorFrame(this.term);
                        this.currentFrame.loadBlock();
                        this.currentFrame.scanToSubBlock(lastFP);
                    }
                    this.validIndexPrefix = Math.min(this.validIndexPrefix, this.currentFrame.prefix);
                }
                while (this.currentFrame.next()) {
                    this.currentFrame = this.pushFrame(null, this.currentFrame.lastSubFP, this.term.length);
                    this.currentFrame.isFloor = false;
                    this.currentFrame.loadBlock();
                }
                return this.term;
            }

            @Override
            public BytesRef term() {
                assert (!this.eof);
                return this.term;
            }

            @Override
            public int docFreq() throws IOException {
                assert (!this.eof);
                this.currentFrame.decodeMetaData();
                return this.currentFrame.state.docFreq;
            }

            @Override
            public long totalTermFreq() throws IOException {
                assert (!this.eof);
                this.currentFrame.decodeMetaData();
                return this.currentFrame.state.totalTermFreq;
            }

            @Override
            public DocsEnum docs(Bits skipDocs, DocsEnum reuse, int flags) throws IOException {
                assert (!this.eof);
                this.currentFrame.decodeMetaData();
                return BlockTreeTermsReader.this.postingsReader.docs(FieldReader.this.fieldInfo, this.currentFrame.state, skipDocs, reuse, flags);
            }

            @Override
            public DocsAndPositionsEnum docsAndPositions(Bits skipDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
                if (FieldReader.this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
                    return null;
                }
                assert (!this.eof);
                this.currentFrame.decodeMetaData();
                return BlockTreeTermsReader.this.postingsReader.docsAndPositions(FieldReader.this.fieldInfo, this.currentFrame.state, skipDocs, reuse, flags);
            }

            @Override
            public void seekExact(BytesRef target, TermState otherState) {
                assert (this.clearEOF());
                if (target.compareTo(this.term) != 0 || !this.termExists) {
                    assert (otherState != null && otherState instanceof BlockTermState);
                    this.currentFrame = this.staticFrame;
                    this.currentFrame.state.copyFrom(otherState);
                    this.term.copyBytes(target);
                    this.currentFrame.metaDataUpto = this.currentFrame.getTermBlockOrd();
                    assert (this.currentFrame.metaDataUpto > 0);
                    this.validIndexPrefix = 0;
                }
            }

            @Override
            public TermState termState() throws IOException {
                assert (!this.eof);
                this.currentFrame.decodeMetaData();
                TermState ts = this.currentFrame.state.clone();
                return ts;
            }

            @Override
            public void seekExact(long ord) {
                throw new UnsupportedOperationException();
            }

            @Override
            public long ord() {
                throw new UnsupportedOperationException();
            }

            private final class Frame {
                final int ord;
                boolean hasTerms;
                boolean hasTermsOrig;
                boolean isFloor;
                FST.Arc<BytesRef> arc;
                long fp;
                long fpOrig;
                long fpEnd;
                byte[] suffixBytes = new byte[128];
                final ByteArrayDataInput suffixesReader = new ByteArrayDataInput();
                byte[] statBytes = new byte[64];
                final ByteArrayDataInput statsReader = new ByteArrayDataInput();
                byte[] floorData = new byte[32];
                final ByteArrayDataInput floorDataReader = new ByteArrayDataInput();
                int prefix;
                int entCount;
                int nextEnt;
                boolean isLastInFloor;
                boolean isLeafBlock;
                long lastSubFP;
                int nextFloorLabel;
                int numFollowFloorBlocks;
                int metaDataUpto;
                final BlockTermState state;
                private int startBytePos;
                private int suffix;
                private long subCode;

                public Frame(int ord) throws IOException {
                    this.ord = ord;
                    this.state = BlockTreeTermsReader.this.postingsReader.newTermState();
                    this.state.totalTermFreq = -1L;
                }

                public void setFloorData(ByteArrayDataInput in, BytesRef source) {
                    int numBytes = source.length - (in.getPosition() - source.offset);
                    if (numBytes > this.floorData.length) {
                        this.floorData = new byte[ArrayUtil.oversize(numBytes, 1)];
                    }
                    System.arraycopy(source.bytes, source.offset + in.getPosition(), this.floorData, 0, numBytes);
                    this.floorDataReader.reset(this.floorData, 0, numBytes);
                    this.numFollowFloorBlocks = this.floorDataReader.readVInt();
                    this.nextFloorLabel = this.floorDataReader.readByte() & 0xFF;
                }

                public int getTermBlockOrd() {
                    return this.isLeafBlock ? this.nextEnt : this.state.termBlockOrd;
                }

                void loadNextFloorBlock() throws IOException {
                    assert (this.arc == null || this.isFloor) : "arc=" + this.arc + " isFloor=" + this.isFloor;
                    this.fp = this.fpEnd;
                    this.nextEnt = -1;
                    this.loadBlock();
                }

                void loadBlock() throws IOException {
                    SegmentTermsEnum.this.initIndexInput();
                    if (this.nextEnt != -1) {
                        return;
                    }
                    SegmentTermsEnum.this.in.seek(this.fp);
                    int code = SegmentTermsEnum.this.in.readVInt();
                    this.entCount = code >>> 1;
                    assert (this.entCount > 0);
                    boolean bl = this.isLastInFloor = (code & 1) != 0;
                    assert (this.arc == null || this.isLastInFloor || this.isFloor);
                    code = SegmentTermsEnum.this.in.readVInt();
                    this.isLeafBlock = (code & 1) != 0;
                    int numBytes = code >>> 1;
                    if (this.suffixBytes.length < numBytes) {
                        this.suffixBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
                    }
                    SegmentTermsEnum.this.in.readBytes(this.suffixBytes, 0, numBytes);
                    this.suffixesReader.reset(this.suffixBytes, 0, numBytes);
                    numBytes = SegmentTermsEnum.this.in.readVInt();
                    if (this.statBytes.length < numBytes) {
                        this.statBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
                    }
                    SegmentTermsEnum.this.in.readBytes(this.statBytes, 0, numBytes);
                    this.statsReader.reset(this.statBytes, 0, numBytes);
                    this.metaDataUpto = 0;
                    this.state.termBlockOrd = 0;
                    this.nextEnt = 0;
                    this.lastSubFP = -1L;
                    BlockTreeTermsReader.this.postingsReader.readTermsBlock(SegmentTermsEnum.this.in, FieldReader.this.fieldInfo, this.state);
                    this.fpEnd = SegmentTermsEnum.this.in.getFilePointer();
                }

                void rewind() {
                    this.fp = this.fpOrig;
                    this.nextEnt = -1;
                    this.hasTerms = this.hasTermsOrig;
                    if (this.isFloor) {
                        this.floorDataReader.rewind();
                        this.numFollowFloorBlocks = this.floorDataReader.readVInt();
                        this.nextFloorLabel = this.floorDataReader.readByte() & 0xFF;
                    }
                }

                public boolean next() {
                    return this.isLeafBlock ? this.nextLeaf() : this.nextNonLeaf();
                }

                public boolean nextLeaf() {
                    assert (this.nextEnt != -1 && this.nextEnt < this.entCount) : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
                    ++this.nextEnt;
                    this.suffix = this.suffixesReader.readVInt();
                    this.startBytePos = this.suffixesReader.getPosition();
                    SegmentTermsEnum.this.term.length = this.prefix + this.suffix;
                    if (SegmentTermsEnum.this.term.bytes.length < SegmentTermsEnum.this.term.length) {
                        SegmentTermsEnum.this.term.grow(SegmentTermsEnum.this.term.length);
                    }
                    this.suffixesReader.readBytes(SegmentTermsEnum.this.term.bytes, this.prefix, this.suffix);
                    SegmentTermsEnum.this.termExists = true;
                    return false;
                }

                public boolean nextNonLeaf() {
                    assert (this.nextEnt != -1 && this.nextEnt < this.entCount) : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
                    ++this.nextEnt;
                    int code = this.suffixesReader.readVInt();
                    this.suffix = code >>> 1;
                    this.startBytePos = this.suffixesReader.getPosition();
                    SegmentTermsEnum.this.term.length = this.prefix + this.suffix;
                    if (SegmentTermsEnum.this.term.bytes.length < SegmentTermsEnum.this.term.length) {
                        SegmentTermsEnum.this.term.grow(SegmentTermsEnum.this.term.length);
                    }
                    this.suffixesReader.readBytes(SegmentTermsEnum.this.term.bytes, this.prefix, this.suffix);
                    if ((code & 1) == 0) {
                        SegmentTermsEnum.this.termExists = true;
                        this.subCode = 0L;
                        ++this.state.termBlockOrd;
                        return false;
                    }
                    SegmentTermsEnum.this.termExists = false;
                    this.subCode = this.suffixesReader.readVLong();
                    this.lastSubFP = this.fp - this.subCode;
                    return true;
                }

                public void scanToFloorFrame(BytesRef target) {
                    if (!this.isFloor || target.length <= this.prefix) {
                        return;
                    }
                    int targetLabel = target.bytes[target.offset + this.prefix] & 0xFF;
                    if (targetLabel < this.nextFloorLabel) {
                        return;
                    }
                    assert (this.numFollowFloorBlocks != 0);
                    long newFP = this.fpOrig;
                    do {
                        long code = this.floorDataReader.readVLong();
                        newFP = this.fpOrig + (code >>> 1);
                        this.hasTerms = (code & 1L) != 0L;
                        this.isLastInFloor = this.numFollowFloorBlocks == 1;
                        --this.numFollowFloorBlocks;
                        if (this.isLastInFloor) {
                            this.nextFloorLabel = 256;
                            break;
                        }
                        this.nextFloorLabel = this.floorDataReader.readByte() & 0xFF;
                    } while (targetLabel >= this.nextFloorLabel);
                    if (newFP != this.fp) {
                        this.nextEnt = -1;
                        this.fp = newFP;
                    }
                }

                public void decodeMetaData() throws IOException {
                    int limit = this.getTermBlockOrd();
                    assert (limit > 0);
                    this.state.termBlockOrd = this.metaDataUpto;
                    while (this.metaDataUpto < limit) {
                        this.state.docFreq = this.statsReader.readVInt();
                        if (FieldReader.this.fieldInfo.getIndexOptions() != FieldInfo.IndexOptions.DOCS_ONLY) {
                            this.state.totalTermFreq = (long)this.state.docFreq + this.statsReader.readVLong();
                        }
                        BlockTreeTermsReader.this.postingsReader.nextTerm(FieldReader.this.fieldInfo, this.state);
                        ++this.metaDataUpto;
                        ++this.state.termBlockOrd;
                    }
                }

                private boolean prefixMatches(BytesRef target) {
                    for (int bytePos = 0; bytePos < this.prefix; ++bytePos) {
                        if (target.bytes[target.offset + bytePos] == SegmentTermsEnum.this.term.bytes[bytePos]) continue;
                        return false;
                    }
                    return true;
                }

                public void scanToSubBlock(long subFP) {
                    assert (!this.isLeafBlock);
                    if (this.lastSubFP == subFP) {
                        return;
                    }
                    assert (subFP < this.fp) : "fp=" + this.fp + " subFP=" + subFP;
                    long targetSubCode = this.fp - subFP;
                    while (true) {
                        assert (this.nextEnt < this.entCount);
                        ++this.nextEnt;
                        int code = this.suffixesReader.readVInt();
                        this.suffixesReader.skipBytes(this.isLeafBlock ? code : code >>> 1);
                        if ((code & 1) != 0) {
                            long subCode = this.suffixesReader.readVLong();
                            if (targetSubCode != subCode) continue;
                            this.lastSubFP = subFP;
                            return;
                        }
                        ++this.state.termBlockOrd;
                    }
                }

                public TermsEnum.SeekStatus scanToTerm(BytesRef target, boolean exactOnly) throws IOException {
                    return this.isLeafBlock ? this.scanToTermLeaf(target, exactOnly) : this.scanToTermNonLeaf(target, exactOnly);
                }

                public TermsEnum.SeekStatus scanToTermLeaf(BytesRef target, boolean exactOnly) throws IOException {
                    block15: {
                        assert (this.nextEnt != -1);
                        SegmentTermsEnum.this.termExists = true;
                        this.subCode = 0L;
                        if (this.nextEnt == this.entCount) {
                            if (exactOnly) {
                                this.fillTerm();
                            }
                            return TermsEnum.SeekStatus.END;
                        }
                        assert (this.prefixMatches(target));
                        block0: while (true) {
                            boolean stop;
                            ++this.nextEnt;
                            this.suffix = this.suffixesReader.readVInt();
                            int termLen = this.prefix + this.suffix;
                            this.startBytePos = this.suffixesReader.getPosition();
                            this.suffixesReader.skipBytes(this.suffix);
                            int targetLimit = target.offset + (target.length < termLen ? target.length : termLen);
                            int targetPos = target.offset + this.prefix;
                            int bytePos = this.startBytePos;
                            do {
                                int cmp;
                                if (targetPos < targetLimit) {
                                    cmp = (this.suffixBytes[bytePos++] & 0xFF) - (target.bytes[targetPos++] & 0xFF);
                                    stop = false;
                                } else {
                                    assert (targetPos == targetLimit);
                                    cmp = termLen - target.length;
                                    stop = true;
                                }
                                if (cmp < 0) {
                                    if (this.nextEnt != this.entCount) continue block0;
                                    if (exactOnly) {
                                        this.fillTerm();
                                    }
                                    break block15;
                                }
                                if (cmp <= 0) continue;
                                this.fillTerm();
                                if (!exactOnly && !SegmentTermsEnum.this.termExists) {
                                    SegmentTermsEnum.this.currentFrame = SegmentTermsEnum.this.pushFrame(null, ((SegmentTermsEnum)SegmentTermsEnum.this).currentFrame.lastSubFP, termLen);
                                    SegmentTermsEnum.this.currentFrame.loadBlock();
                                    while (SegmentTermsEnum.this.currentFrame.next()) {
                                        SegmentTermsEnum.this.currentFrame = SegmentTermsEnum.this.pushFrame(null, ((SegmentTermsEnum)SegmentTermsEnum.this).currentFrame.lastSubFP, SegmentTermsEnum.this.term.length);
                                        SegmentTermsEnum.this.currentFrame.loadBlock();
                                    }
                                }
                                return TermsEnum.SeekStatus.NOT_FOUND;
                            } while (!stop);
                            break;
                        }
                        assert (SegmentTermsEnum.this.termExists);
                        this.fillTerm();
                        return TermsEnum.SeekStatus.FOUND;
                    }
                    if (exactOnly) {
                        this.fillTerm();
                    }
                    return TermsEnum.SeekStatus.END;
                }

                public TermsEnum.SeekStatus scanToTermNonLeaf(BytesRef target, boolean exactOnly) throws IOException {
                    block17: {
                        assert (this.nextEnt != -1);
                        if (this.nextEnt == this.entCount) {
                            if (exactOnly) {
                                this.fillTerm();
                                SegmentTermsEnum.this.termExists = this.subCode == 0L;
                            }
                            return TermsEnum.SeekStatus.END;
                        }
                        assert (this.prefixMatches(target));
                        block0: while (true) {
                            boolean stop;
                            ++this.nextEnt;
                            int code = this.suffixesReader.readVInt();
                            this.suffix = code >>> 1;
                            SegmentTermsEnum.this.termExists = (code & 1) == 0;
                            int termLen = this.prefix + this.suffix;
                            this.startBytePos = this.suffixesReader.getPosition();
                            this.suffixesReader.skipBytes(this.suffix);
                            if (SegmentTermsEnum.this.termExists) {
                                ++this.state.termBlockOrd;
                                this.subCode = 0L;
                            } else {
                                this.subCode = this.suffixesReader.readVLong();
                                this.lastSubFP = this.fp - this.subCode;
                            }
                            int targetLimit = target.offset + (target.length < termLen ? target.length : termLen);
                            int targetPos = target.offset + this.prefix;
                            int bytePos = this.startBytePos;
                            do {
                                int cmp;
                                if (targetPos < targetLimit) {
                                    cmp = (this.suffixBytes[bytePos++] & 0xFF) - (target.bytes[targetPos++] & 0xFF);
                                    stop = false;
                                } else {
                                    assert (targetPos == targetLimit);
                                    cmp = termLen - target.length;
                                    stop = true;
                                }
                                if (cmp < 0) {
                                    if (this.nextEnt != this.entCount) continue block0;
                                    if (exactOnly) {
                                        this.fillTerm();
                                    }
                                    break block17;
                                }
                                if (cmp <= 0) continue;
                                this.fillTerm();
                                if (!exactOnly && !SegmentTermsEnum.this.termExists) {
                                    SegmentTermsEnum.this.currentFrame = SegmentTermsEnum.this.pushFrame(null, ((SegmentTermsEnum)SegmentTermsEnum.this).currentFrame.lastSubFP, termLen);
                                    SegmentTermsEnum.this.currentFrame.loadBlock();
                                    while (SegmentTermsEnum.this.currentFrame.next()) {
                                        SegmentTermsEnum.this.currentFrame = SegmentTermsEnum.this.pushFrame(null, ((SegmentTermsEnum)SegmentTermsEnum.this).currentFrame.lastSubFP, SegmentTermsEnum.this.term.length);
                                        SegmentTermsEnum.this.currentFrame.loadBlock();
                                    }
                                }
                                return TermsEnum.SeekStatus.NOT_FOUND;
                            } while (!stop);
                            break;
                        }
                        assert (SegmentTermsEnum.this.termExists);
                        this.fillTerm();
                        return TermsEnum.SeekStatus.FOUND;
                    }
                    if (exactOnly) {
                        this.fillTerm();
                    }
                    return TermsEnum.SeekStatus.END;
                }

                private void fillTerm() {
                    int termLength = this.prefix + this.suffix;
                    SegmentTermsEnum.this.term.length = this.prefix + this.suffix;
                    if (SegmentTermsEnum.this.term.bytes.length < termLength) {
                        SegmentTermsEnum.this.term.grow(termLength);
                    }
                    System.arraycopy(this.suffixBytes, this.startBytePos, SegmentTermsEnum.this.term.bytes, this.prefix, this.suffix);
                }
            }
        }

        private final class IntersectEnum
        extends TermsEnum {
            private final IndexInput in;
            private Frame[] stack;
            private FST.Arc<BytesRef>[] arcs = new FST.Arc[5];
            private final RunAutomaton runAutomaton;
            private final CompiledAutomaton compiledAutomaton;
            private Frame currentFrame;
            private final BytesRef term = new BytesRef();
            private final FST.BytesReader fstReader;
            private BytesRef savedStartTerm;

            public IntersectEnum(CompiledAutomaton compiled, BytesRef startTerm) throws IOException {
                this.runAutomaton = compiled.runAutomaton;
                this.compiledAutomaton = compiled;
                this.in = BlockTreeTermsReader.this.in.clone();
                this.stack = new Frame[5];
                for (int idx = 0; idx < this.stack.length; ++idx) {
                    this.stack[idx] = new Frame(idx);
                }
                for (int arcIdx = 0; arcIdx < this.arcs.length; ++arcIdx) {
                    this.arcs[arcIdx] = new FST.Arc();
                }
                this.fstReader = FieldReader.this.index == null ? null : FieldReader.this.index.getBytesReader();
                FST.Arc<BytesRef> arc = FieldReader.this.index.getFirstArc(this.arcs[0]);
                assert (arc.isFinal());
                Frame f = this.stack[0];
                f.fp = f.fpOrig = FieldReader.this.rootBlockFP;
                f.prefix = 0;
                f.setState(this.runAutomaton.getInitialState());
                f.arc = arc;
                f.outputPrefix = (BytesRef)arc.output;
                f.load(FieldReader.this.rootCode);
                assert (this.setSavedStartTerm(startTerm));
                this.currentFrame = f;
                if (startTerm != null) {
                    this.seekToStartTerm(startTerm);
                }
            }

            private boolean setSavedStartTerm(BytesRef startTerm) {
                this.savedStartTerm = startTerm == null ? null : BytesRef.deepCopyOf(startTerm);
                return true;
            }

            @Override
            public TermState termState() throws IOException {
                this.currentFrame.decodeMetaData();
                return this.currentFrame.termState.clone();
            }

            private Frame getFrame(int ord) throws IOException {
                if (ord >= this.stack.length) {
                    Frame[] next = new Frame[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    System.arraycopy(this.stack, 0, next, 0, this.stack.length);
                    for (int stackOrd = this.stack.length; stackOrd < next.length; ++stackOrd) {
                        next[stackOrd] = new Frame(stackOrd);
                    }
                    this.stack = next;
                }
                assert (this.stack[ord].ord == ord);
                return this.stack[ord];
            }

            private FST.Arc<BytesRef> getArc(int ord) {
                if (ord >= this.arcs.length) {
                    FST.Arc[] next = new FST.Arc[ArrayUtil.oversize(1 + ord, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    System.arraycopy(this.arcs, 0, next, 0, this.arcs.length);
                    for (int arcOrd = this.arcs.length; arcOrd < next.length; ++arcOrd) {
                        next[arcOrd] = new FST.Arc();
                    }
                    this.arcs = next;
                }
                return this.arcs[ord];
            }

            private Frame pushFrame(int state) throws IOException {
                Frame f = this.getFrame(this.currentFrame == null ? 0 : 1 + this.currentFrame.ord);
                f.fp = f.fpOrig = this.currentFrame.lastSubFP;
                f.prefix = this.currentFrame.prefix + this.currentFrame.suffix;
                f.setState(state);
                FST.Arc<BytesRef> arc = this.currentFrame.arc;
                assert (this.currentFrame.suffix > 0);
                BytesRef output = this.currentFrame.outputPrefix;
                for (int idx = this.currentFrame.prefix; idx < f.prefix; ++idx) {
                    int target = this.term.bytes[idx] & 0xFF;
                    arc = FieldReader.this.index.findTargetArc(target, arc, this.getArc(1 + idx), this.fstReader);
                    assert (arc != null);
                    output = BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.output);
                }
                f.arc = arc;
                f.outputPrefix = output;
                assert (arc.isFinal());
                f.load(BlockTreeTermsReader.this.fstOutputs.add(output, (BytesRef)arc.nextFinalOutput));
                return f;
            }

            @Override
            public BytesRef term() {
                return this.term;
            }

            @Override
            public int docFreq() throws IOException {
                this.currentFrame.decodeMetaData();
                return this.currentFrame.termState.docFreq;
            }

            @Override
            public long totalTermFreq() throws IOException {
                this.currentFrame.decodeMetaData();
                return this.currentFrame.termState.totalTermFreq;
            }

            @Override
            public DocsEnum docs(Bits skipDocs, DocsEnum reuse, int flags) throws IOException {
                this.currentFrame.decodeMetaData();
                return BlockTreeTermsReader.this.postingsReader.docs(FieldReader.this.fieldInfo, this.currentFrame.termState, skipDocs, reuse, flags);
            }

            @Override
            public DocsAndPositionsEnum docsAndPositions(Bits skipDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
                if (FieldReader.this.fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
                    return null;
                }
                this.currentFrame.decodeMetaData();
                return BlockTreeTermsReader.this.postingsReader.docsAndPositions(FieldReader.this.fieldInfo, this.currentFrame.termState, skipDocs, reuse, flags);
            }

            private int getState() {
                int state = this.currentFrame.state;
                for (int idx = 0; idx < this.currentFrame.suffix; ++idx) {
                    state = this.runAutomaton.step(state, this.currentFrame.suffixBytes[this.currentFrame.startBytePos + idx] & 0xFF);
                    assert (state != -1);
                }
                return state;
            }

            private void seekToStartTerm(BytesRef target) throws IOException {
                assert (this.currentFrame.ord == 0);
                if (this.term.length < target.length) {
                    this.term.bytes = ArrayUtil.grow(this.term.bytes, target.length);
                }
                FST.Arc<BytesRef> arc = this.arcs[0];
                assert (arc == this.currentFrame.arc);
                for (int idx = 0; idx <= target.length; ++idx) {
                    block8: {
                        int cmp;
                        int saveTermBlockOrd;
                        long saveLastSubFP;
                        int saveSuffix;
                        int saveStartBytePos;
                        int savePos;
                        block9: {
                            while (true) {
                                savePos = this.currentFrame.suffixesReader.getPosition();
                                saveStartBytePos = this.currentFrame.startBytePos;
                                saveSuffix = this.currentFrame.suffix;
                                saveLastSubFP = this.currentFrame.lastSubFP;
                                saveTermBlockOrd = this.currentFrame.termState.termBlockOrd;
                                boolean isSubBlock = this.currentFrame.next();
                                this.term.length = this.currentFrame.prefix + this.currentFrame.suffix;
                                if (this.term.bytes.length < this.term.length) {
                                    this.term.bytes = ArrayUtil.grow(this.term.bytes, this.term.length);
                                }
                                System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
                                if (isSubBlock && StringHelper.startsWith(target, this.term)) break block8;
                                cmp = this.term.compareTo(target);
                                if (cmp >= 0) break block9;
                                if (this.currentFrame.nextEnt != this.currentFrame.entCount) continue;
                                if (this.currentFrame.isLastInFloor) break;
                                this.currentFrame.loadNextFloorBlock();
                            }
                            return;
                        }
                        if (cmp == 0) {
                            return;
                        }
                        --this.currentFrame.nextEnt;
                        this.currentFrame.lastSubFP = saveLastSubFP;
                        this.currentFrame.startBytePos = saveStartBytePos;
                        this.currentFrame.suffix = saveSuffix;
                        this.currentFrame.suffixesReader.setPosition(savePos);
                        this.currentFrame.termState.termBlockOrd = saveTermBlockOrd;
                        System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
                        this.term.length = this.currentFrame.prefix + this.currentFrame.suffix;
                        return;
                    }
                    this.currentFrame = this.pushFrame(this.getState());
                }
                assert (false);
            }

            @Override
            public BytesRef next() throws IOException {
                block0: while (true) {
                    if (this.currentFrame.nextEnt == this.currentFrame.entCount) {
                        if (!this.currentFrame.isLastInFloor) {
                            this.currentFrame.loadNextFloorBlock();
                            continue;
                        }
                        if (this.currentFrame.ord == 0) {
                            return null;
                        }
                        long lastFP = this.currentFrame.fpOrig;
                        this.currentFrame = this.stack[this.currentFrame.ord - 1];
                        assert (this.currentFrame.lastSubFP == lastFP);
                        continue;
                    }
                    boolean isSubBlock = this.currentFrame.next();
                    if (this.currentFrame.suffix != 0) {
                        int label = this.currentFrame.suffixBytes[this.currentFrame.startBytePos] & 0xFF;
                        while (label > this.currentFrame.curTransitionMax) {
                            if (this.currentFrame.transitionIndex >= this.currentFrame.transitions.length - 1) {
                                this.currentFrame.isLastInFloor = true;
                                this.currentFrame.nextEnt = this.currentFrame.entCount;
                                continue block0;
                            }
                            ++this.currentFrame.transitionIndex;
                            this.currentFrame.curTransitionMax = this.currentFrame.transitions[this.currentFrame.transitionIndex].getMax();
                        }
                    }
                    if (this.compiledAutomaton.commonSuffixRef != null && !isSubBlock) {
                        int suffixBytesPos;
                        int termLen = this.currentFrame.prefix + this.currentFrame.suffix;
                        if (termLen < this.compiledAutomaton.commonSuffixRef.length) continue;
                        byte[] suffixBytes = this.currentFrame.suffixBytes;
                        byte[] commonSuffixBytes = this.compiledAutomaton.commonSuffixRef.bytes;
                        int lenInPrefix = this.compiledAutomaton.commonSuffixRef.length - this.currentFrame.suffix;
                        assert (this.compiledAutomaton.commonSuffixRef.offset == 0);
                        int commonSuffixBytesPos = 0;
                        if (lenInPrefix > 0) {
                            byte[] termBytes = this.term.bytes;
                            int termBytesPos = this.currentFrame.prefix - lenInPrefix;
                            assert (termBytesPos >= 0);
                            int termBytesPosEnd = this.currentFrame.prefix;
                            while (termBytesPos < termBytesPosEnd) {
                                if (termBytes[termBytesPos++] == commonSuffixBytes[commonSuffixBytesPos++]) continue;
                                continue block0;
                            }
                            suffixBytesPos = this.currentFrame.startBytePos;
                        } else {
                            suffixBytesPos = this.currentFrame.startBytePos + this.currentFrame.suffix - this.compiledAutomaton.commonSuffixRef.length;
                        }
                        int commonSuffixBytesPosEnd = this.compiledAutomaton.commonSuffixRef.length;
                        while (commonSuffixBytesPos < commonSuffixBytesPosEnd) {
                            if (suffixBytes[suffixBytesPos++] == commonSuffixBytes[commonSuffixBytesPos++]) continue;
                            continue block0;
                        }
                    }
                    int state = this.currentFrame.state;
                    for (int idx = 0; idx < this.currentFrame.suffix; ++idx) {
                        if ((state = this.runAutomaton.step(state, this.currentFrame.suffixBytes[this.currentFrame.startBytePos + idx] & 0xFF)) == -1) continue block0;
                    }
                    if (isSubBlock) {
                        this.copyTerm();
                        this.currentFrame = this.pushFrame(state);
                        continue;
                    }
                    if (this.runAutomaton.isAccept(state)) break;
                }
                this.copyTerm();
                assert (this.savedStartTerm == null || this.term.compareTo(this.savedStartTerm) > 0) : "saveStartTerm=" + this.savedStartTerm.utf8ToString() + " term=" + this.term.utf8ToString();
                return this.term;
            }

            private void copyTerm() {
                int len = this.currentFrame.prefix + this.currentFrame.suffix;
                if (this.term.bytes.length < len) {
                    this.term.bytes = ArrayUtil.grow(this.term.bytes, len);
                }
                System.arraycopy(this.currentFrame.suffixBytes, this.currentFrame.startBytePos, this.term.bytes, this.currentFrame.prefix, this.currentFrame.suffix);
                this.term.length = len;
            }

            @Override
            public Comparator<BytesRef> getComparator() {
                return BytesRef.getUTF8SortedAsUnicodeComparator();
            }

            @Override
            public boolean seekExact(BytesRef text, boolean useCache) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void seekExact(long ord) {
                throw new UnsupportedOperationException();
            }

            @Override
            public long ord() {
                throw new UnsupportedOperationException();
            }

            @Override
            public TermsEnum.SeekStatus seekCeil(BytesRef text, boolean useCache) {
                throw new UnsupportedOperationException();
            }

            private final class Frame {
                final int ord;
                long fp;
                long fpOrig;
                long fpEnd;
                long lastSubFP;
                int state;
                int metaDataUpto;
                byte[] suffixBytes = new byte[128];
                final ByteArrayDataInput suffixesReader = new ByteArrayDataInput();
                byte[] statBytes = new byte[64];
                final ByteArrayDataInput statsReader = new ByteArrayDataInput();
                byte[] floorData = new byte[32];
                final ByteArrayDataInput floorDataReader = new ByteArrayDataInput();
                int prefix;
                int entCount;
                int nextEnt;
                boolean isLastInFloor;
                boolean isLeafBlock;
                int numFollowFloorBlocks;
                int nextFloorLabel;
                Transition[] transitions;
                int curTransitionMax;
                int transitionIndex;
                FST.Arc<BytesRef> arc;
                final BlockTermState termState;
                BytesRef outputPrefix;
                private int startBytePos;
                private int suffix;

                public Frame(int ord) throws IOException {
                    this.ord = ord;
                    this.termState = BlockTreeTermsReader.this.postingsReader.newTermState();
                    this.termState.totalTermFreq = -1L;
                }

                void loadNextFloorBlock() throws IOException {
                    assert (this.numFollowFloorBlocks > 0);
                    do {
                        this.fp = this.fpOrig + (this.floorDataReader.readVLong() >>> 1);
                        --this.numFollowFloorBlocks;
                        this.nextFloorLabel = this.numFollowFloorBlocks != 0 ? this.floorDataReader.readByte() & 0xFF : 256;
                    } while (this.numFollowFloorBlocks != 0 && this.nextFloorLabel <= this.transitions[this.transitionIndex].getMin());
                    this.load(null);
                }

                public void setState(int state) {
                    this.state = state;
                    this.transitionIndex = 0;
                    this.transitions = ((IntersectEnum)IntersectEnum.this).compiledAutomaton.sortedTransitions[state];
                    this.curTransitionMax = this.transitions.length != 0 ? this.transitions[0].getMax() : -1;
                }

                void load(BytesRef frameIndexData) throws IOException {
                    if (frameIndexData != null && this.transitions.length != 0) {
                        if (this.floorData.length < frameIndexData.length) {
                            this.floorData = new byte[ArrayUtil.oversize(frameIndexData.length, 1)];
                        }
                        System.arraycopy(frameIndexData.bytes, frameIndexData.offset, this.floorData, 0, frameIndexData.length);
                        this.floorDataReader.reset(this.floorData, 0, frameIndexData.length);
                        long code = this.floorDataReader.readVLong();
                        if ((code & 1L) != 0L) {
                            this.numFollowFloorBlocks = this.floorDataReader.readVInt();
                            this.nextFloorLabel = this.floorDataReader.readByte() & 0xFF;
                            if (!IntersectEnum.this.runAutomaton.isAccept(this.state)) {
                                while (this.numFollowFloorBlocks != 0 && this.nextFloorLabel <= this.transitions[0].getMin()) {
                                    this.fp = this.fpOrig + (this.floorDataReader.readVLong() >>> 1);
                                    --this.numFollowFloorBlocks;
                                    if (this.numFollowFloorBlocks != 0) {
                                        this.nextFloorLabel = this.floorDataReader.readByte() & 0xFF;
                                        continue;
                                    }
                                    this.nextFloorLabel = 256;
                                }
                            }
                        }
                    }
                    IntersectEnum.this.in.seek(this.fp);
                    int code = IntersectEnum.this.in.readVInt();
                    this.entCount = code >>> 1;
                    assert (this.entCount > 0);
                    this.isLastInFloor = (code & 1) != 0;
                    code = IntersectEnum.this.in.readVInt();
                    this.isLeafBlock = (code & 1) != 0;
                    int numBytes = code >>> 1;
                    if (this.suffixBytes.length < numBytes) {
                        this.suffixBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
                    }
                    IntersectEnum.this.in.readBytes(this.suffixBytes, 0, numBytes);
                    this.suffixesReader.reset(this.suffixBytes, 0, numBytes);
                    numBytes = IntersectEnum.this.in.readVInt();
                    if (this.statBytes.length < numBytes) {
                        this.statBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
                    }
                    IntersectEnum.this.in.readBytes(this.statBytes, 0, numBytes);
                    this.statsReader.reset(this.statBytes, 0, numBytes);
                    this.metaDataUpto = 0;
                    this.termState.termBlockOrd = 0;
                    this.nextEnt = 0;
                    BlockTreeTermsReader.this.postingsReader.readTermsBlock(IntersectEnum.this.in, FieldReader.this.fieldInfo, this.termState);
                    if (!this.isLastInFloor) {
                        this.fpEnd = IntersectEnum.this.in.getFilePointer();
                    }
                }

                public boolean next() {
                    return this.isLeafBlock ? this.nextLeaf() : this.nextNonLeaf();
                }

                public boolean nextLeaf() {
                    assert (this.nextEnt != -1 && this.nextEnt < this.entCount) : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
                    ++this.nextEnt;
                    this.suffix = this.suffixesReader.readVInt();
                    this.startBytePos = this.suffixesReader.getPosition();
                    this.suffixesReader.skipBytes(this.suffix);
                    return false;
                }

                public boolean nextNonLeaf() {
                    assert (this.nextEnt != -1 && this.nextEnt < this.entCount) : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
                    ++this.nextEnt;
                    int code = this.suffixesReader.readVInt();
                    this.suffix = code >>> 1;
                    this.startBytePos = this.suffixesReader.getPosition();
                    this.suffixesReader.skipBytes(this.suffix);
                    if ((code & 1) == 0) {
                        ++this.termState.termBlockOrd;
                        return false;
                    }
                    this.lastSubFP = this.fp - this.suffixesReader.readVLong();
                    return true;
                }

                public int getTermBlockOrd() {
                    return this.isLeafBlock ? this.nextEnt : this.termState.termBlockOrd;
                }

                public void decodeMetaData() throws IOException {
                    int limit = this.getTermBlockOrd();
                    assert (limit > 0);
                    this.termState.termBlockOrd = this.metaDataUpto;
                    while (this.metaDataUpto < limit) {
                        this.termState.docFreq = this.statsReader.readVInt();
                        if (FieldReader.this.fieldInfo.getIndexOptions() != FieldInfo.IndexOptions.DOCS_ONLY) {
                            this.termState.totalTermFreq = (long)this.termState.docFreq + this.statsReader.readVLong();
                        }
                        BlockTreeTermsReader.this.postingsReader.nextTerm(FieldReader.this.fieldInfo, this.termState);
                        ++this.metaDataUpto;
                        ++this.termState.termBlockOrd;
                    }
                }
            }
        }
    }

    public static class Stats {
        public long indexNodeCount;
        public long indexArcCount;
        public long indexNumBytes;
        public long totalTermCount;
        public long totalTermBytes;
        public int nonFloorBlockCount;
        public int floorBlockCount;
        public int floorSubBlockCount;
        public int mixedBlockCount;
        public int termsOnlyBlockCount;
        public int subBlocksOnlyBlockCount;
        public int totalBlockCount;
        public int[] blockCountByPrefixLen = new int[10];
        private int startBlockCount;
        private int endBlockCount;
        public long totalBlockSuffixBytes;
        public long totalBlockStatsBytes;
        public long totalBlockOtherBytes;
        public final String segment;
        public final String field;

        Stats(String segment, String field) {
            this.segment = segment;
            this.field = field;
        }

        void startBlock(FieldReader.SegmentTermsEnum.Frame frame, boolean isFloor) {
            ++this.totalBlockCount;
            if (isFloor) {
                if (frame.fp == frame.fpOrig) {
                    ++this.floorBlockCount;
                }
                ++this.floorSubBlockCount;
            } else {
                ++this.nonFloorBlockCount;
            }
            if (this.blockCountByPrefixLen.length <= frame.prefix) {
                this.blockCountByPrefixLen = ArrayUtil.grow(this.blockCountByPrefixLen, 1 + frame.prefix);
            }
            int n = frame.prefix;
            this.blockCountByPrefixLen[n] = this.blockCountByPrefixLen[n] + 1;
            ++this.startBlockCount;
            this.totalBlockSuffixBytes += (long)frame.suffixesReader.length();
            this.totalBlockStatsBytes += (long)frame.statsReader.length();
        }

        void endBlock(FieldReader.SegmentTermsEnum.Frame frame) {
            int termCount = frame.isLeafBlock ? frame.entCount : frame.state.termBlockOrd;
            int subBlockCount = frame.entCount - termCount;
            this.totalTermCount += (long)termCount;
            if (termCount != 0 && subBlockCount != 0) {
                ++this.mixedBlockCount;
            } else if (termCount != 0) {
                ++this.termsOnlyBlockCount;
            } else if (subBlockCount != 0) {
                ++this.subBlocksOnlyBlockCount;
            } else {
                throw new IllegalStateException();
            }
            ++this.endBlockCount;
            long otherBytes = frame.fpEnd - frame.fp - (long)frame.suffixesReader.length() - (long)frame.statsReader.length();
            assert (otherBytes > 0L) : "otherBytes=" + otherBytes + " frame.fp=" + frame.fp + " frame.fpEnd=" + frame.fpEnd;
            this.totalBlockOtherBytes += otherBytes;
        }

        void term(BytesRef term) {
            this.totalTermBytes += (long)term.length;
        }

        void finish() {
            assert (this.startBlockCount == this.endBlockCount) : "startBlockCount=" + this.startBlockCount + " endBlockCount=" + this.endBlockCount;
            assert (this.totalBlockCount == this.floorSubBlockCount + this.nonFloorBlockCount) : "floorSubBlockCount=" + this.floorSubBlockCount + " nonFloorBlockCount=" + this.nonFloorBlockCount + " totalBlockCount=" + this.totalBlockCount;
            assert (this.totalBlockCount == this.mixedBlockCount + this.termsOnlyBlockCount + this.subBlocksOnlyBlockCount) : "totalBlockCount=" + this.totalBlockCount + " mixedBlockCount=" + this.mixedBlockCount + " subBlocksOnlyBlockCount=" + this.subBlocksOnlyBlockCount + " termsOnlyBlockCount=" + this.termsOnlyBlockCount;
        }

        public String toString() {
            PrintStream out;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            try {
                out = new PrintStream((OutputStream)bos, false, "UTF-8");
            }
            catch (UnsupportedEncodingException bogus) {
                throw new RuntimeException(bogus);
            }
            out.println("  index FST:");
            out.println("    " + this.indexNodeCount + " nodes");
            out.println("    " + this.indexArcCount + " arcs");
            out.println("    " + this.indexNumBytes + " bytes");
            out.println("  terms:");
            out.println("    " + this.totalTermCount + " terms");
            out.println("    " + this.totalTermBytes + " bytes" + (this.totalTermCount != 0L ? " (" + String.format(Locale.ROOT, "%.1f", (double)this.totalTermBytes / (double)this.totalTermCount) + " bytes/term)" : ""));
            out.println("  blocks:");
            out.println("    " + this.totalBlockCount + " blocks");
            out.println("    " + this.termsOnlyBlockCount + " terms-only blocks");
            out.println("    " + this.subBlocksOnlyBlockCount + " sub-block-only blocks");
            out.println("    " + this.mixedBlockCount + " mixed blocks");
            out.println("    " + this.floorBlockCount + " floor blocks");
            out.println("    " + (this.totalBlockCount - this.floorSubBlockCount) + " non-floor blocks");
            out.println("    " + this.floorSubBlockCount + " floor sub-blocks");
            out.println("    " + this.totalBlockSuffixBytes + " term suffix bytes" + (this.totalBlockCount != 0 ? " (" + String.format(Locale.ROOT, "%.1f", (double)this.totalBlockSuffixBytes / (double)this.totalBlockCount) + " suffix-bytes/block)" : ""));
            out.println("    " + this.totalBlockStatsBytes + " term stats bytes" + (this.totalBlockCount != 0 ? " (" + String.format(Locale.ROOT, "%.1f", (double)this.totalBlockStatsBytes / (double)this.totalBlockCount) + " stats-bytes/block)" : ""));
            out.println("    " + this.totalBlockOtherBytes + " other bytes" + (this.totalBlockCount != 0 ? " (" + String.format(Locale.ROOT, "%.1f", (double)this.totalBlockOtherBytes / (double)this.totalBlockCount) + " other-bytes/block)" : ""));
            if (this.totalBlockCount != 0) {
                out.println("    by prefix length:");
                int total = 0;
                for (int prefix = 0; prefix < this.blockCountByPrefixLen.length; ++prefix) {
                    int blockCount = this.blockCountByPrefixLen[prefix];
                    total += blockCount;
                    if (blockCount == 0) continue;
                    out.println("      " + String.format(Locale.ROOT, "%2d", prefix) + ": " + blockCount);
                }
                assert (this.totalBlockCount == total);
            }
            try {
                return bos.toString("UTF-8");
            }
            catch (UnsupportedEncodingException bogus) {
                throw new RuntimeException(bogus);
            }
        }
    }
}

