/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.PostingsReaderBase;
import org.apache.lucene.codecs.lucene41.ForUtil;
import org.apache.lucene.codecs.lucene41.Lucene41SkipReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.TermState;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public final class Lucene41PostingsReader
extends PostingsReaderBase {
    private final IndexInput docIn;
    private final IndexInput posIn;
    private final IndexInput payIn;
    private final ForUtil forUtil;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Lucene41PostingsReader(Directory dir, FieldInfos fieldInfos, SegmentInfo segmentInfo, IOContext ioContext, String segmentSuffix) throws IOException {
        boolean success = false;
        IndexInput docIn = null;
        IndexInput posIn = null;
        IndexInput payIn = null;
        try {
            docIn = dir.openInput(IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "doc"), ioContext);
            CodecUtil.checkHeader(docIn, "Lucene41PostingsWriterDoc", 0, 0);
            this.forUtil = new ForUtil(docIn);
            if (fieldInfos.hasProx()) {
                posIn = dir.openInput(IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "pos"), ioContext);
                CodecUtil.checkHeader(posIn, "Lucene41PostingsWriterPos", 0, 0);
                if (fieldInfos.hasPayloads() || fieldInfos.hasOffsets()) {
                    payIn = dir.openInput(IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "pay"), ioContext);
                    CodecUtil.checkHeader(payIn, "Lucene41PostingsWriterPay", 0, 0);
                }
            }
            this.docIn = docIn;
            this.posIn = posIn;
            this.payIn = payIn;
            success = true;
            if (success) return;
        }
        catch (Throwable throwable) {
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(docIn, posIn, payIn);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(docIn, posIn, payIn);
    }

    @Override
    public void init(IndexInput termsIn) throws IOException {
        CodecUtil.checkHeader(termsIn, "Lucene41PostingsWriterTerms", 0, 0);
        int indexBlockSize = termsIn.readVInt();
        if (indexBlockSize != 128) {
            throw new IllegalStateException("index-time BLOCK_SIZE (" + indexBlockSize + ") != read-time BLOCK_SIZE (" + 128 + ")");
        }
    }

    static void readVIntBlock(IndexInput docIn, int[] docBuffer, int[] freqBuffer, int num, boolean indexHasFreq) throws IOException {
        if (indexHasFreq) {
            for (int i = 0; i < num; ++i) {
                int code = docIn.readVInt();
                docBuffer[i] = code >>> 1;
                freqBuffer[i] = (code & 1) != 0 ? 1 : docIn.readVInt();
            }
        } else {
            for (int i = 0; i < num; ++i) {
                docBuffer[i] = docIn.readVInt();
            }
        }
    }

    @Override
    public IntBlockTermState newTermState() {
        return new IntBlockTermState();
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.docIn, this.posIn, this.payIn);
    }

    @Override
    public void readTermsBlock(IndexInput termsIn, FieldInfo fieldInfo, BlockTermState _termState) throws IOException {
        IntBlockTermState termState = (IntBlockTermState)_termState;
        int numBytes = termsIn.readVInt();
        if (termState.bytes == null) {
            termState.bytes = new byte[ArrayUtil.oversize(numBytes, 1)];
            termState.bytesReader = new ByteArrayDataInput();
        } else if (termState.bytes.length < numBytes) {
            termState.bytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        termsIn.readBytes(termState.bytes, 0, numBytes);
        termState.bytesReader.reset(termState.bytes, 0, numBytes);
    }

    @Override
    public void nextTerm(FieldInfo fieldInfo, BlockTermState _termState) throws IOException {
        IntBlockTermState termState = (IntBlockTermState)_termState;
        boolean isFirstTerm = termState.termBlockOrd == 0;
        boolean fieldHasPositions = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        boolean fieldHasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        boolean fieldHasPayloads = fieldInfo.hasPayloads();
        ByteArrayDataInput in = termState.bytesReader;
        if (isFirstTerm) {
            if (termState.docFreq == 1) {
                termState.singletonDocID = ((DataInput)in).readVInt();
                termState.docStartFP = 0L;
            } else {
                termState.singletonDocID = -1;
                termState.docStartFP = ((DataInput)in).readVLong();
            }
            if (fieldHasPositions) {
                termState.posStartFP = ((DataInput)in).readVLong();
                termState.lastPosBlockOffset = termState.totalTermFreq > 128L ? ((DataInput)in).readVLong() : -1L;
                termState.payStartFP = (fieldHasPayloads || fieldHasOffsets) && termState.totalTermFreq >= 128L ? ((DataInput)in).readVLong() : -1L;
            }
        } else {
            if (termState.docFreq == 1) {
                termState.singletonDocID = ((DataInput)in).readVInt();
            } else {
                termState.singletonDocID = -1;
                termState.docStartFP += ((DataInput)in).readVLong();
            }
            if (fieldHasPositions) {
                termState.posStartFP += ((DataInput)in).readVLong();
                termState.lastPosBlockOffset = termState.totalTermFreq > 128L ? ((DataInput)in).readVLong() : -1L;
                if ((fieldHasPayloads || fieldHasOffsets) && termState.totalTermFreq >= 128L) {
                    long delta = ((DataInput)in).readVLong();
                    termState.payStartFP = termState.payStartFP == -1L ? delta : (termState.payStartFP += delta);
                }
            }
        }
        termState.skipOffset = termState.docFreq > 128 ? ((DataInput)in).readVLong() : -1L;
    }

    @Override
    public DocsEnum docs(FieldInfo fieldInfo, BlockTermState termState, Bits liveDocs, DocsEnum reuse, int flags) throws IOException {
        BlockDocsEnum docsEnum;
        if (reuse instanceof BlockDocsEnum) {
            docsEnum = (BlockDocsEnum)reuse;
            if (!docsEnum.canReuse(this.docIn, fieldInfo)) {
                docsEnum = new BlockDocsEnum(fieldInfo);
            }
        } else {
            docsEnum = new BlockDocsEnum(fieldInfo);
        }
        return docsEnum.reset(liveDocs, (IntBlockTermState)termState, flags);
    }

    @Override
    public DocsAndPositionsEnum docsAndPositions(FieldInfo fieldInfo, BlockTermState termState, Bits liveDocs, DocsAndPositionsEnum reuse, int flags) throws IOException {
        EverythingEnum everythingEnum;
        boolean indexHasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        boolean indexHasPayloads = fieldInfo.hasPayloads();
        if (!(indexHasOffsets && (flags & 1) != 0 || indexHasPayloads && (flags & 2) != 0)) {
            BlockDocsAndPositionsEnum docsAndPositionsEnum;
            if (reuse instanceof BlockDocsAndPositionsEnum) {
                docsAndPositionsEnum = (BlockDocsAndPositionsEnum)reuse;
                if (!docsAndPositionsEnum.canReuse(this.docIn, fieldInfo)) {
                    docsAndPositionsEnum = new BlockDocsAndPositionsEnum(fieldInfo);
                }
            } else {
                docsAndPositionsEnum = new BlockDocsAndPositionsEnum(fieldInfo);
            }
            return docsAndPositionsEnum.reset(liveDocs, (IntBlockTermState)termState);
        }
        if (reuse instanceof EverythingEnum) {
            everythingEnum = (EverythingEnum)reuse;
            if (!everythingEnum.canReuse(this.docIn, fieldInfo)) {
                everythingEnum = new EverythingEnum(fieldInfo);
            }
        } else {
            everythingEnum = new EverythingEnum(fieldInfo);
        }
        return everythingEnum.reset(liveDocs, (IntBlockTermState)termState, flags);
    }

    final class EverythingEnum
    extends DocsAndPositionsEnum {
        private final byte[] encoded;
        private final int[] docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] payloadLengthBuffer;
        private final int[] offsetStartDeltaBuffer;
        private final int[] offsetLengthBuffer;
        private byte[] payloadBytes;
        private int payloadByteUpto;
        private int payloadLength;
        private int lastStartOffset;
        private int startOffset;
        private int endOffset;
        private int docBufferUpto;
        private int posBufferUpto;
        private Lucene41SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final IndexInput posIn;
        final IndexInput payIn;
        final BytesRef payload;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private int position;
        private int posPendingCount;
        private long posPendingFP;
        private long payPendingFP;
        private long docTermStartFP;
        private long posTermStartFP;
        private long payTermStartFP;
        private long lastPosBlockFP;
        private long skipOffset;
        private int nextSkipDoc;
        private Bits liveDocs;
        private boolean needsOffsets;
        private boolean needsPayloads;
        private int singletonDocID;

        public EverythingEnum(FieldInfo fieldInfo) throws IOException {
            this.startDocIn = Lucene41PostingsReader.this.docIn;
            this.docIn = null;
            this.posIn = Lucene41PostingsReader.this.posIn.clone();
            this.payIn = Lucene41PostingsReader.this.payIn.clone();
            this.encoded = new byte[512];
            boolean bl = this.indexHasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            if (this.indexHasOffsets) {
                this.offsetStartDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                this.offsetLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
            } else {
                this.offsetStartDeltaBuffer = null;
                this.offsetLengthBuffer = null;
                this.startOffset = -1;
                this.endOffset = -1;
            }
            this.indexHasPayloads = fieldInfo.hasPayloads();
            if (this.indexHasPayloads) {
                this.payloadLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                this.payloadBytes = new byte[128];
                this.payload = new BytesRef();
            } else {
                this.payloadLengthBuffer = null;
                this.payloadBytes = null;
                this.payload = null;
            }
        }

        public boolean canReuse(IndexInput docIn, FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasOffsets == fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }

        public EverythingEnum reset(Bits liveDocs, IntBlockTermState termState, int flags) throws IOException {
            this.liveDocs = liveDocs;
            this.docFreq = termState.docFreq;
            this.docTermStartFP = termState.docStartFP;
            this.posTermStartFP = termState.posStartFP;
            this.payTermStartFP = termState.payStartFP;
            this.skipOffset = termState.skipOffset;
            this.totalTermFreq = termState.totalTermFreq;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.posPendingFP = this.posTermStartFP;
            this.payPendingFP = this.payTermStartFP;
            this.posPendingCount = 0;
            this.lastPosBlockFP = termState.totalTermFreq < 128L ? this.posTermStartFP : (termState.totalTermFreq == 128L ? -1L : this.posTermStartFP + termState.lastPosBlockOffset);
            this.needsOffsets = (flags & 1) != 0;
            this.needsPayloads = (flags & 2) != 0;
            this.doc = -1;
            this.accum = 0;
            this.docUpto = 0;
            this.nextSkipDoc = 127;
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }

        @Override
        public int freq() throws IOException {
            return this.freq;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        private void refillDocs() throws IOException {
            int left = this.docFreq - this.docUpto;
            assert (left > 0);
            if (left >= 128) {
                Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
            } else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            } else {
                Lucene41PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, true);
            }
            this.docBufferUpto = 0;
        }

        private void refillPositions() throws IOException {
            if (this.posIn.getFilePointer() == this.lastPosBlockFP) {
                int count = (int)(this.totalTermFreq % 128L);
                int payloadLength = 0;
                int offsetLength = 0;
                this.payloadByteUpto = 0;
                for (int i = 0; i < count; ++i) {
                    int code = this.posIn.readVInt();
                    if (this.indexHasPayloads) {
                        if ((code & 1) != 0) {
                            payloadLength = this.posIn.readVInt();
                        }
                        this.payloadLengthBuffer[i] = payloadLength;
                        this.posDeltaBuffer[i] = code >>> 1;
                        if (payloadLength != 0) {
                            if (this.payloadByteUpto + payloadLength > this.payloadBytes.length) {
                                this.payloadBytes = ArrayUtil.grow(this.payloadBytes, this.payloadByteUpto + payloadLength);
                            }
                            this.posIn.readBytes(this.payloadBytes, this.payloadByteUpto, payloadLength);
                            this.payloadByteUpto += payloadLength;
                        }
                    } else {
                        this.posDeltaBuffer[i] = code;
                    }
                    if (!this.indexHasOffsets) continue;
                    int deltaCode = this.posIn.readVInt();
                    if ((deltaCode & 1) != 0) {
                        offsetLength = this.posIn.readVInt();
                    }
                    this.offsetStartDeltaBuffer[i] = deltaCode >>> 1;
                    this.offsetLengthBuffer[i] = offsetLength;
                }
                this.payloadByteUpto = 0;
            } else {
                Lucene41PostingsReader.this.forUtil.readBlock(this.posIn, this.encoded, this.posDeltaBuffer);
                if (this.indexHasPayloads) {
                    if (this.needsPayloads) {
                        Lucene41PostingsReader.this.forUtil.readBlock(this.payIn, this.encoded, this.payloadLengthBuffer);
                        int numBytes = this.payIn.readVInt();
                        if (numBytes > this.payloadBytes.length) {
                            this.payloadBytes = ArrayUtil.grow(this.payloadBytes, numBytes);
                        }
                        this.payIn.readBytes(this.payloadBytes, 0, numBytes);
                    } else {
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                        int numBytes = this.payIn.readVInt();
                        this.payIn.seek(this.payIn.getFilePointer() + (long)numBytes);
                    }
                    this.payloadByteUpto = 0;
                }
                if (this.indexHasOffsets) {
                    if (this.needsOffsets) {
                        Lucene41PostingsReader.this.forUtil.readBlock(this.payIn, this.encoded, this.offsetStartDeltaBuffer);
                        Lucene41PostingsReader.this.forUtil.readBlock(this.payIn, this.encoded, this.offsetLengthBuffer);
                    } else {
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                    }
                }
            }
        }

        @Override
        public int nextDoc() throws IOException {
            do {
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                this.freq = this.freqBuffer[this.docBufferUpto];
                this.posPendingCount += this.freq;
                ++this.docBufferUpto;
                ++this.docUpto;
            } while (this.liveDocs != null && !this.liveDocs.get(this.accum));
            this.doc = this.accum;
            this.position = 0;
            this.lastStartOffset = 0;
            return this.doc;
        }

        @Override
        public int advance(int target) throws IOException {
            block10: {
                if (this.docFreq > 128 && target > this.nextSkipDoc) {
                    int newDocUpto;
                    if (this.skipper == null) {
                        this.skipper = new Lucene41SkipReader(this.docIn.clone(), 10, 128, true, this.indexHasOffsets, this.indexHasPayloads);
                    }
                    if (!this.skipped) {
                        assert (this.skipOffset != -1L);
                        this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, this.posTermStartFP, this.payTermStartFP, this.docFreq);
                        this.skipped = true;
                    }
                    if ((newDocUpto = this.skipper.skipTo(target) + 1) > this.docUpto) {
                        assert (newDocUpto % 128 == 0) : "got " + newDocUpto;
                        this.docUpto = newDocUpto;
                        this.docBufferUpto = 128;
                        this.accum = this.skipper.getDoc();
                        this.docIn.seek(this.skipper.getDocPointer());
                        this.posPendingFP = this.skipper.getPosPointer();
                        this.payPendingFP = this.skipper.getPayPointer();
                        this.posPendingCount = this.skipper.getPosBufferUpto();
                        this.lastStartOffset = 0;
                        this.payloadByteUpto = this.skipper.getPayloadByteUpto();
                    }
                    this.nextSkipDoc = this.skipper.getNextSkipDoc();
                }
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                do {
                    this.accum += this.docDeltaBuffer[this.docBufferUpto];
                    this.freq = this.freqBuffer[this.docBufferUpto];
                    this.posPendingCount += this.freq;
                    ++this.docBufferUpto;
                    ++this.docUpto;
                    if (this.accum >= target) break block10;
                } while (this.docUpto != this.docFreq);
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            if (this.liveDocs == null || this.liveDocs.get(this.accum)) {
                this.position = 0;
                this.lastStartOffset = 0;
                this.doc = this.accum;
                return this.doc;
            }
            return this.nextDoc();
        }

        private void skipPositions() throws IOException {
            int toSkip = this.posPendingCount - this.freq;
            int leftInBlock = 128 - this.posBufferUpto;
            if (toSkip < leftInBlock) {
                int end = this.posBufferUpto + toSkip;
                while (this.posBufferUpto < end) {
                    if (this.indexHasPayloads) {
                        this.payloadByteUpto += this.payloadLengthBuffer[this.posBufferUpto];
                    }
                    ++this.posBufferUpto;
                }
            } else {
                toSkip -= leftInBlock;
                while (toSkip >= 128) {
                    assert (this.posIn.getFilePointer() != this.lastPosBlockFP);
                    Lucene41PostingsReader.this.forUtil.skipBlock(this.posIn);
                    if (this.indexHasPayloads) {
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                        int numBytes = this.payIn.readVInt();
                        this.payIn.seek(this.payIn.getFilePointer() + (long)numBytes);
                    }
                    if (this.indexHasOffsets) {
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.payIn);
                    }
                    toSkip -= 128;
                }
                this.refillPositions();
                this.payloadByteUpto = 0;
                this.posBufferUpto = 0;
                while (this.posBufferUpto < toSkip) {
                    if (this.indexHasPayloads) {
                        this.payloadByteUpto += this.payloadLengthBuffer[this.posBufferUpto];
                    }
                    ++this.posBufferUpto;
                }
            }
            this.position = 0;
            this.lastStartOffset = 0;
        }

        @Override
        public int nextPosition() throws IOException {
            if (this.posPendingFP != -1L) {
                this.posIn.seek(this.posPendingFP);
                this.posPendingFP = -1L;
                if (this.payPendingFP != -1L) {
                    this.payIn.seek(this.payPendingFP);
                    this.payPendingFP = -1L;
                }
                this.posBufferUpto = 128;
            }
            if (this.posPendingCount > this.freq) {
                this.skipPositions();
                this.posPendingCount = this.freq;
            }
            if (this.posBufferUpto == 128) {
                this.refillPositions();
                this.posBufferUpto = 0;
            }
            this.position += this.posDeltaBuffer[this.posBufferUpto];
            if (this.indexHasPayloads) {
                this.payloadLength = this.payloadLengthBuffer[this.posBufferUpto];
                this.payload.bytes = this.payloadBytes;
                this.payload.offset = this.payloadByteUpto;
                this.payload.length = this.payloadLength;
                this.payloadByteUpto += this.payloadLength;
            }
            if (this.indexHasOffsets) {
                this.startOffset = this.lastStartOffset + this.offsetStartDeltaBuffer[this.posBufferUpto];
                this.endOffset = this.startOffset + this.offsetLengthBuffer[this.posBufferUpto];
                this.lastStartOffset = this.startOffset;
            }
            ++this.posBufferUpto;
            --this.posPendingCount;
            return this.position;
        }

        @Override
        public int startOffset() {
            return this.startOffset;
        }

        @Override
        public int endOffset() {
            return this.endOffset;
        }

        @Override
        public BytesRef getPayload() {
            if (this.payloadLength == 0) {
                return null;
            }
            return this.payload;
        }

        @Override
        public long cost() {
            return this.docFreq;
        }
    }

    final class BlockDocsAndPositionsEnum
    extends DocsAndPositionsEnum {
        private final byte[] encoded;
        private final int[] docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private int docBufferUpto;
        private int posBufferUpto;
        private Lucene41SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final IndexInput posIn;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private int position;
        private int posPendingCount;
        private long posPendingFP;
        private long docTermStartFP;
        private long posTermStartFP;
        private long payTermStartFP;
        private long lastPosBlockFP;
        private long skipOffset;
        private int nextSkipDoc;
        private Bits liveDocs;
        private int singletonDocID;

        public BlockDocsAndPositionsEnum(FieldInfo fieldInfo) throws IOException {
            this.startDocIn = Lucene41PostingsReader.this.docIn;
            this.docIn = null;
            this.posIn = Lucene41PostingsReader.this.posIn.clone();
            this.encoded = new byte[512];
            this.indexHasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            this.indexHasPayloads = fieldInfo.hasPayloads();
        }

        public boolean canReuse(IndexInput docIn, FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasOffsets == fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }

        public DocsAndPositionsEnum reset(Bits liveDocs, IntBlockTermState termState) throws IOException {
            this.liveDocs = liveDocs;
            this.docFreq = termState.docFreq;
            this.docTermStartFP = termState.docStartFP;
            this.posTermStartFP = termState.posStartFP;
            this.payTermStartFP = termState.payStartFP;
            this.skipOffset = termState.skipOffset;
            this.totalTermFreq = termState.totalTermFreq;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.posPendingFP = this.posTermStartFP;
            this.posPendingCount = 0;
            this.lastPosBlockFP = termState.totalTermFreq < 128L ? this.posTermStartFP : (termState.totalTermFreq == 128L ? -1L : this.posTermStartFP + termState.lastPosBlockOffset);
            this.doc = -1;
            this.accum = 0;
            this.docUpto = 0;
            this.nextSkipDoc = 127;
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }

        @Override
        public int freq() throws IOException {
            return this.freq;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        private void refillDocs() throws IOException {
            int left = this.docFreq - this.docUpto;
            assert (left > 0);
            if (left >= 128) {
                Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
            } else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            } else {
                Lucene41PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, true);
            }
            this.docBufferUpto = 0;
        }

        private void refillPositions() throws IOException {
            if (this.posIn.getFilePointer() == this.lastPosBlockFP) {
                int count = (int)(this.totalTermFreq % 128L);
                int payloadLength = 0;
                for (int i = 0; i < count; ++i) {
                    int code = this.posIn.readVInt();
                    if (this.indexHasPayloads) {
                        if ((code & 1) != 0) {
                            payloadLength = this.posIn.readVInt();
                        }
                        this.posDeltaBuffer[i] = code >>> 1;
                        if (payloadLength != 0) {
                            this.posIn.seek(this.posIn.getFilePointer() + (long)payloadLength);
                        }
                    } else {
                        this.posDeltaBuffer[i] = code;
                    }
                    if (!this.indexHasOffsets || (this.posIn.readVInt() & 1) == 0) continue;
                    this.posIn.readVInt();
                }
            } else {
                Lucene41PostingsReader.this.forUtil.readBlock(this.posIn, this.encoded, this.posDeltaBuffer);
            }
        }

        @Override
        public int nextDoc() throws IOException {
            do {
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                this.freq = this.freqBuffer[this.docBufferUpto];
                this.posPendingCount += this.freq;
                ++this.docBufferUpto;
                ++this.docUpto;
            } while (this.liveDocs != null && !this.liveDocs.get(this.accum));
            this.doc = this.accum;
            this.position = 0;
            return this.doc;
        }

        @Override
        public int advance(int target) throws IOException {
            block10: {
                if (this.docFreq > 128 && target > this.nextSkipDoc) {
                    int newDocUpto;
                    if (this.skipper == null) {
                        this.skipper = new Lucene41SkipReader(this.docIn.clone(), 10, 128, true, this.indexHasOffsets, this.indexHasPayloads);
                    }
                    if (!this.skipped) {
                        assert (this.skipOffset != -1L);
                        this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, this.posTermStartFP, this.payTermStartFP, this.docFreq);
                        this.skipped = true;
                    }
                    if ((newDocUpto = this.skipper.skipTo(target) + 1) > this.docUpto) {
                        assert (newDocUpto % 128 == 0) : "got " + newDocUpto;
                        this.docUpto = newDocUpto;
                        this.docBufferUpto = 128;
                        this.accum = this.skipper.getDoc();
                        this.docIn.seek(this.skipper.getDocPointer());
                        this.posPendingFP = this.skipper.getPosPointer();
                        this.posPendingCount = this.skipper.getPosBufferUpto();
                    }
                    this.nextSkipDoc = this.skipper.getNextSkipDoc();
                }
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                do {
                    this.accum += this.docDeltaBuffer[this.docBufferUpto];
                    this.freq = this.freqBuffer[this.docBufferUpto];
                    this.posPendingCount += this.freq;
                    ++this.docBufferUpto;
                    ++this.docUpto;
                    if (this.accum >= target) break block10;
                } while (this.docUpto != this.docFreq);
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            if (this.liveDocs == null || this.liveDocs.get(this.accum)) {
                this.position = 0;
                this.doc = this.accum;
                return this.doc;
            }
            return this.nextDoc();
        }

        private void skipPositions() throws IOException {
            int toSkip = this.posPendingCount - this.freq;
            int leftInBlock = 128 - this.posBufferUpto;
            if (toSkip < leftInBlock) {
                this.posBufferUpto += toSkip;
            } else {
                toSkip -= leftInBlock;
                while (toSkip >= 128) {
                    assert (this.posIn.getFilePointer() != this.lastPosBlockFP);
                    Lucene41PostingsReader.this.forUtil.skipBlock(this.posIn);
                    toSkip -= 128;
                }
                this.refillPositions();
                this.posBufferUpto = toSkip;
            }
            this.position = 0;
        }

        @Override
        public int nextPosition() throws IOException {
            if (this.posPendingFP != -1L) {
                this.posIn.seek(this.posPendingFP);
                this.posPendingFP = -1L;
                this.posBufferUpto = 128;
            }
            if (this.posPendingCount > this.freq) {
                this.skipPositions();
                this.posPendingCount = this.freq;
            }
            if (this.posBufferUpto == 128) {
                this.refillPositions();
                this.posBufferUpto = 0;
            }
            this.position += this.posDeltaBuffer[this.posBufferUpto++];
            --this.posPendingCount;
            return this.position;
        }

        @Override
        public int startOffset() {
            return -1;
        }

        @Override
        public int endOffset() {
            return -1;
        }

        @Override
        public BytesRef getPayload() {
            return null;
        }

        @Override
        public long cost() {
            return this.docFreq;
        }
    }

    final class BlockDocsEnum
    extends DocsEnum {
        private final byte[] encoded;
        private final int[] docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private final int[] freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
        private int docBufferUpto;
        private Lucene41SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final boolean indexHasFreq;
        final boolean indexHasPos;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private long docTermStartFP;
        private long skipOffset;
        private int nextSkipDoc;
        private Bits liveDocs;
        private boolean needsFreq;
        private int singletonDocID;

        public BlockDocsEnum(FieldInfo fieldInfo) throws IOException {
            this.startDocIn = Lucene41PostingsReader.this.docIn;
            this.docIn = null;
            this.indexHasFreq = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
            this.indexHasPos = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
            this.indexHasOffsets = fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
            this.indexHasPayloads = fieldInfo.hasPayloads();
            this.encoded = new byte[512];
        }

        public boolean canReuse(IndexInput docIn, FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasFreq == fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0 && this.indexHasPos == fieldInfo.getIndexOptions().compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }

        public DocsEnum reset(Bits liveDocs, IntBlockTermState termState, int flags) throws IOException {
            this.liveDocs = liveDocs;
            this.docFreq = termState.docFreq;
            this.totalTermFreq = this.indexHasFreq ? termState.totalTermFreq : (long)this.docFreq;
            this.docTermStartFP = termState.docStartFP;
            this.skipOffset = termState.skipOffset;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.doc = -1;
            boolean bl = this.needsFreq = (flags & 1) != 0;
            if (!this.indexHasFreq) {
                Arrays.fill(this.freqBuffer, 1);
            }
            this.accum = 0;
            this.docUpto = 0;
            this.nextSkipDoc = 127;
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }

        @Override
        public int freq() throws IOException {
            return this.freq;
        }

        @Override
        public int docID() {
            return this.doc;
        }

        private void refillDocs() throws IOException {
            int left = this.docFreq - this.docUpto;
            assert (left > 0);
            if (left >= 128) {
                Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                if (this.indexHasFreq) {
                    if (this.needsFreq) {
                        Lucene41PostingsReader.this.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
                    } else {
                        Lucene41PostingsReader.this.forUtil.skipBlock(this.docIn);
                    }
                }
            } else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            } else {
                Lucene41PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, this.indexHasFreq);
            }
            this.docBufferUpto = 0;
        }

        @Override
        public int nextDoc() throws IOException {
            while (true) {
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                ++this.docUpto;
                if (this.liveDocs == null || this.liveDocs.get(this.accum)) {
                    this.doc = this.accum;
                    this.freq = this.freqBuffer[this.docBufferUpto];
                    ++this.docBufferUpto;
                    return this.doc;
                }
                ++this.docBufferUpto;
            }
        }

        @Override
        public int advance(int target) throws IOException {
            block10: {
                if (this.docFreq > 128 && target > this.nextSkipDoc) {
                    int newDocUpto;
                    if (this.skipper == null) {
                        this.skipper = new Lucene41SkipReader(this.docIn.clone(), 10, 128, this.indexHasPos, this.indexHasOffsets, this.indexHasPayloads);
                    }
                    if (!this.skipped) {
                        assert (this.skipOffset != -1L);
                        this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, 0L, 0L, this.docFreq);
                        this.skipped = true;
                    }
                    if ((newDocUpto = this.skipper.skipTo(target) + 1) > this.docUpto) {
                        assert (newDocUpto % 128 == 0) : "got " + newDocUpto;
                        this.docUpto = newDocUpto;
                        this.docBufferUpto = 128;
                        this.accum = this.skipper.getDoc();
                        this.docIn.seek(this.skipper.getDocPointer());
                    }
                    this.nextSkipDoc = this.skipper.getNextSkipDoc();
                }
                if (this.docUpto == this.docFreq) {
                    this.doc = Integer.MAX_VALUE;
                    return Integer.MAX_VALUE;
                }
                if (this.docBufferUpto == 128) {
                    this.refillDocs();
                }
                do {
                    this.accum += this.docDeltaBuffer[this.docBufferUpto];
                    ++this.docUpto;
                    if (this.accum >= target) break block10;
                    ++this.docBufferUpto;
                } while (this.docUpto != this.docFreq);
                this.doc = Integer.MAX_VALUE;
                return Integer.MAX_VALUE;
            }
            if (this.liveDocs == null || this.liveDocs.get(this.accum)) {
                this.freq = this.freqBuffer[this.docBufferUpto];
                ++this.docBufferUpto;
                this.doc = this.accum;
                return this.doc;
            }
            ++this.docBufferUpto;
            return this.nextDoc();
        }

        @Override
        public long cost() {
            return this.docFreq;
        }
    }

    private static final class IntBlockTermState
    extends BlockTermState {
        long docStartFP;
        long posStartFP;
        long payStartFP;
        long skipOffset;
        long lastPosBlockOffset;
        int singletonDocID;
        ByteArrayDataInput bytesReader;
        byte[] bytes;

        private IntBlockTermState() {
        }

        @Override
        public IntBlockTermState clone() {
            IntBlockTermState other = new IntBlockTermState();
            other.copyFrom(this);
            return other;
        }

        @Override
        public void copyFrom(TermState _other) {
            super.copyFrom(_other);
            IntBlockTermState other = (IntBlockTermState)_other;
            this.docStartFP = other.docStartFP;
            this.posStartFP = other.posStartFP;
            this.payStartFP = other.payStartFP;
            this.lastPosBlockOffset = other.lastPosBlockOffset;
            this.skipOffset = other.skipOffset;
            this.singletonDocID = other.singletonDocID;
        }

        @Override
        public String toString() {
            return super.toString() + " docStartFP=" + this.docStartFP + " posStartFP=" + this.posStartFP + " payStartFP=" + this.payStartFP + " lastPosBlockOffset=" + this.lastPosBlockOffset + " singletonDocID=" + this.singletonDocID;
        }
    }
}

