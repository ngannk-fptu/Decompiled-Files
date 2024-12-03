/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.codecs.TermStats;
import org.apache.lucene.codecs.lucene41.ForUtil;
import org.apache.lucene.codecs.lucene41.Lucene41SkipWriter;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public final class Lucene41PostingsWriter
extends PostingsWriterBase {
    static final int maxSkipLevels = 10;
    static final String TERMS_CODEC = "Lucene41PostingsWriterTerms";
    static final String DOC_CODEC = "Lucene41PostingsWriterDoc";
    static final String POS_CODEC = "Lucene41PostingsWriterPos";
    static final String PAY_CODEC = "Lucene41PostingsWriterPay";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    final IndexOutput docOut;
    final IndexOutput posOut;
    final IndexOutput payOut;
    private IndexOutput termsOut;
    private boolean fieldHasFreqs;
    private boolean fieldHasPositions;
    private boolean fieldHasOffsets;
    private boolean fieldHasPayloads;
    private long docTermStartFP;
    private long posTermStartFP;
    private long payTermStartFP;
    final int[] docDeltaBuffer;
    final int[] freqBuffer;
    private int docBufferUpto;
    final int[] posDeltaBuffer;
    final int[] payloadLengthBuffer;
    final int[] offsetStartDeltaBuffer;
    final int[] offsetLengthBuffer;
    private int posBufferUpto;
    private byte[] payloadBytes;
    private int payloadByteUpto;
    private int lastBlockDocID;
    private long lastBlockPosFP;
    private long lastBlockPayFP;
    private int lastBlockPosBufferUpto;
    private int lastBlockPayloadByteUpto;
    private int lastDocID;
    private int lastPosition;
    private int lastStartOffset;
    private int docCount;
    final byte[] encoded;
    private final ForUtil forUtil;
    private final Lucene41SkipWriter skipWriter;
    private final List<PendingTerm> pendingTerms;
    private final RAMOutputStream bytesWriter;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Lucene41PostingsWriter(SegmentWriteState state, float acceptableOverheadRatio) throws IOException {
        IndexOutput payOut;
        IndexOutput posOut;
        block10: {
            this.pendingTerms = new ArrayList<PendingTerm>();
            this.bytesWriter = new RAMOutputStream();
            this.docOut = state.directory.createOutput(IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "doc"), state.context);
            posOut = null;
            payOut = null;
            boolean success = false;
            try {
                CodecUtil.writeHeader(this.docOut, DOC_CODEC, 0);
                this.forUtil = new ForUtil(acceptableOverheadRatio, this.docOut);
                if (state.fieldInfos.hasProx()) {
                    this.posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                    posOut = state.directory.createOutput(IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pos"), state.context);
                    CodecUtil.writeHeader(posOut, POS_CODEC, 0);
                    if (state.fieldInfos.hasPayloads()) {
                        this.payloadBytes = new byte[128];
                        this.payloadLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                    } else {
                        this.payloadBytes = null;
                        this.payloadLengthBuffer = null;
                    }
                    if (state.fieldInfos.hasOffsets()) {
                        this.offsetStartDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                        this.offsetLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                    } else {
                        this.offsetStartDeltaBuffer = null;
                        this.offsetLengthBuffer = null;
                    }
                    if (state.fieldInfos.hasPayloads() || state.fieldInfos.hasOffsets()) {
                        payOut = state.directory.createOutput(IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pay"), state.context);
                        CodecUtil.writeHeader(payOut, PAY_CODEC, 0);
                    }
                } else {
                    this.posDeltaBuffer = null;
                    this.payloadLengthBuffer = null;
                    this.offsetStartDeltaBuffer = null;
                    this.offsetLengthBuffer = null;
                    this.payloadBytes = null;
                }
                this.payOut = payOut;
                this.posOut = posOut;
                success = true;
                if (success) break block10;
            }
            catch (Throwable throwable) {
                if (!success) {
                    IOUtils.closeWhileHandlingException(this.docOut, posOut, payOut);
                }
                throw throwable;
            }
            IOUtils.closeWhileHandlingException(this.docOut, posOut, payOut);
        }
        this.docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        this.freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
        this.skipWriter = new Lucene41SkipWriter(10, 128, state.segmentInfo.getDocCount(), this.docOut, posOut, payOut);
        this.encoded = new byte[512];
    }

    public Lucene41PostingsWriter(SegmentWriteState state) throws IOException {
        this(state, 0.0f);
    }

    @Override
    public void start(IndexOutput termsOut) throws IOException {
        this.termsOut = termsOut;
        CodecUtil.writeHeader(termsOut, TERMS_CODEC, 0);
        termsOut.writeVInt(128);
    }

    @Override
    public void setField(FieldInfo fieldInfo) {
        FieldInfo.IndexOptions indexOptions = fieldInfo.getIndexOptions();
        this.fieldHasFreqs = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS) >= 0;
        this.fieldHasPositions = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        this.fieldHasOffsets = indexOptions.compareTo(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        this.fieldHasPayloads = fieldInfo.hasPayloads();
        this.skipWriter.setField(this.fieldHasPositions, this.fieldHasOffsets, this.fieldHasPayloads);
    }

    @Override
    public void startTerm() {
        this.docTermStartFP = this.docOut.getFilePointer();
        if (this.fieldHasPositions) {
            this.posTermStartFP = this.posOut.getFilePointer();
            if (this.fieldHasPayloads || this.fieldHasOffsets) {
                this.payTermStartFP = this.payOut.getFilePointer();
            }
        }
        this.lastDocID = 0;
        this.lastBlockDocID = -1;
        this.skipWriter.resetSkip();
    }

    @Override
    public void startDoc(int docID, int termDocFreq) throws IOException {
        if (this.lastBlockDocID != -1 && this.docBufferUpto == 0) {
            this.skipWriter.bufferSkip(this.lastBlockDocID, this.docCount, this.lastBlockPosFP, this.lastBlockPayFP, this.lastBlockPosBufferUpto, this.lastBlockPayloadByteUpto);
        }
        int docDelta = docID - this.lastDocID;
        if (docID < 0 || this.docCount > 0 && docDelta <= 0) {
            throw new CorruptIndexException("docs out of order (" + docID + " <= " + this.lastDocID + " ) (docOut: " + this.docOut + ")");
        }
        this.docDeltaBuffer[this.docBufferUpto] = docDelta;
        if (this.fieldHasFreqs) {
            this.freqBuffer[this.docBufferUpto] = termDocFreq;
        }
        ++this.docBufferUpto;
        ++this.docCount;
        if (this.docBufferUpto == 128) {
            this.forUtil.writeBlock(this.docDeltaBuffer, this.encoded, this.docOut);
            if (this.fieldHasFreqs) {
                this.forUtil.writeBlock(this.freqBuffer, this.encoded, this.docOut);
            }
        }
        this.lastDocID = docID;
        this.lastPosition = 0;
        this.lastStartOffset = 0;
    }

    @Override
    public void addPosition(int position, BytesRef payload, int startOffset, int endOffset) throws IOException {
        this.posDeltaBuffer[this.posBufferUpto] = position - this.lastPosition;
        if (this.fieldHasPayloads) {
            if (payload == null || payload.length == 0) {
                this.payloadLengthBuffer[this.posBufferUpto] = 0;
            } else {
                this.payloadLengthBuffer[this.posBufferUpto] = payload.length;
                if (this.payloadByteUpto + payload.length > this.payloadBytes.length) {
                    this.payloadBytes = ArrayUtil.grow(this.payloadBytes, this.payloadByteUpto + payload.length);
                }
                System.arraycopy(payload.bytes, payload.offset, this.payloadBytes, this.payloadByteUpto, payload.length);
                this.payloadByteUpto += payload.length;
            }
        }
        if (this.fieldHasOffsets) {
            assert (startOffset >= this.lastStartOffset);
            assert (endOffset >= startOffset);
            this.offsetStartDeltaBuffer[this.posBufferUpto] = startOffset - this.lastStartOffset;
            this.offsetLengthBuffer[this.posBufferUpto] = endOffset - startOffset;
            this.lastStartOffset = startOffset;
        }
        ++this.posBufferUpto;
        this.lastPosition = position;
        if (this.posBufferUpto == 128) {
            this.forUtil.writeBlock(this.posDeltaBuffer, this.encoded, this.posOut);
            if (this.fieldHasPayloads) {
                this.forUtil.writeBlock(this.payloadLengthBuffer, this.encoded, this.payOut);
                this.payOut.writeVInt(this.payloadByteUpto);
                this.payOut.writeBytes(this.payloadBytes, 0, this.payloadByteUpto);
                this.payloadByteUpto = 0;
            }
            if (this.fieldHasOffsets) {
                this.forUtil.writeBlock(this.offsetStartDeltaBuffer, this.encoded, this.payOut);
                this.forUtil.writeBlock(this.offsetLengthBuffer, this.encoded, this.payOut);
            }
            this.posBufferUpto = 0;
        }
    }

    @Override
    public void finishDoc() throws IOException {
        if (this.docBufferUpto == 128) {
            this.lastBlockDocID = this.lastDocID;
            if (this.posOut != null) {
                if (this.payOut != null) {
                    this.lastBlockPayFP = this.payOut.getFilePointer();
                }
                this.lastBlockPosFP = this.posOut.getFilePointer();
                this.lastBlockPosBufferUpto = this.posBufferUpto;
                this.lastBlockPayloadByteUpto = this.payloadByteUpto;
            }
            this.docBufferUpto = 0;
        }
    }

    @Override
    public void finishTerm(TermStats stats) throws IOException {
        long lastPosBlockOffset;
        int singletonDocID;
        assert (stats.docFreq > 0);
        assert (stats.docFreq == this.docCount) : stats.docFreq + " vs " + this.docCount;
        if (stats.docFreq == 1) {
            singletonDocID = this.docDeltaBuffer[0];
        } else {
            singletonDocID = -1;
            for (int i = 0; i < this.docBufferUpto; ++i) {
                int docDelta = this.docDeltaBuffer[i];
                int freq = this.freqBuffer[i];
                if (!this.fieldHasFreqs) {
                    this.docOut.writeVInt(docDelta);
                    continue;
                }
                if (this.freqBuffer[i] == 1) {
                    this.docOut.writeVInt(docDelta << 1 | 1);
                    continue;
                }
                this.docOut.writeVInt(docDelta << 1);
                this.docOut.writeVInt(freq);
            }
        }
        if (this.fieldHasPositions) {
            assert (stats.totalTermFreq != -1L);
            lastPosBlockOffset = stats.totalTermFreq > 128L ? this.posOut.getFilePointer() - this.posTermStartFP : -1L;
            if (this.posBufferUpto > 0) {
                int lastPayloadLength = -1;
                int lastOffsetLength = -1;
                int payloadBytesReadUpto = 0;
                for (int i = 0; i < this.posBufferUpto; ++i) {
                    int posDelta = this.posDeltaBuffer[i];
                    if (this.fieldHasPayloads) {
                        int payloadLength = this.payloadLengthBuffer[i];
                        if (payloadLength != lastPayloadLength) {
                            lastPayloadLength = payloadLength;
                            this.posOut.writeVInt(posDelta << 1 | 1);
                            this.posOut.writeVInt(payloadLength);
                        } else {
                            this.posOut.writeVInt(posDelta << 1);
                        }
                        if (payloadLength != 0) {
                            this.posOut.writeBytes(this.payloadBytes, payloadBytesReadUpto, payloadLength);
                            payloadBytesReadUpto += payloadLength;
                        }
                    } else {
                        this.posOut.writeVInt(posDelta);
                    }
                    if (!this.fieldHasOffsets) continue;
                    int delta = this.offsetStartDeltaBuffer[i];
                    int length = this.offsetLengthBuffer[i];
                    if (length == lastOffsetLength) {
                        this.posOut.writeVInt(delta << 1);
                        continue;
                    }
                    this.posOut.writeVInt(delta << 1 | 1);
                    this.posOut.writeVInt(length);
                    lastOffsetLength = length;
                }
                if (this.fieldHasPayloads) {
                    assert (payloadBytesReadUpto == this.payloadByteUpto);
                    this.payloadByteUpto = 0;
                }
            }
        } else {
            lastPosBlockOffset = -1L;
        }
        long skipOffset = this.docCount > 128 ? this.skipWriter.writeSkip(this.docOut) - this.docTermStartFP : -1L;
        long payStartFP = stats.totalTermFreq >= 128L ? this.payTermStartFP : -1L;
        this.pendingTerms.add(new PendingTerm(this.docTermStartFP, this.posTermStartFP, payStartFP, skipOffset, lastPosBlockOffset, singletonDocID));
        this.docBufferUpto = 0;
        this.posBufferUpto = 0;
        this.lastDocID = 0;
        this.docCount = 0;
    }

    @Override
    public void flushTermsBlock(int start, int count) throws IOException {
        if (count == 0) {
            this.termsOut.writeByte((byte)0);
            return;
        }
        assert (start <= this.pendingTerms.size());
        assert (count <= start);
        int limit = this.pendingTerms.size() - start + count;
        long lastDocStartFP = 0L;
        long lastPosStartFP = 0L;
        long lastPayStartFP = 0L;
        for (int idx = limit - count; idx < limit; ++idx) {
            PendingTerm term = this.pendingTerms.get(idx);
            if (term.singletonDocID == -1) {
                this.bytesWriter.writeVLong(term.docStartFP - lastDocStartFP);
                lastDocStartFP = term.docStartFP;
            } else {
                this.bytesWriter.writeVInt(term.singletonDocID);
            }
            if (this.fieldHasPositions) {
                this.bytesWriter.writeVLong(term.posStartFP - lastPosStartFP);
                lastPosStartFP = term.posStartFP;
                if (term.lastPosBlockOffset != -1L) {
                    this.bytesWriter.writeVLong(term.lastPosBlockOffset);
                }
                if ((this.fieldHasPayloads || this.fieldHasOffsets) && term.payStartFP != -1L) {
                    this.bytesWriter.writeVLong(term.payStartFP - lastPayStartFP);
                    lastPayStartFP = term.payStartFP;
                }
            }
            if (term.skipOffset == -1L) continue;
            this.bytesWriter.writeVLong(term.skipOffset);
        }
        this.termsOut.writeVInt((int)this.bytesWriter.getFilePointer());
        this.bytesWriter.writeTo(this.termsOut);
        this.bytesWriter.reset();
        this.pendingTerms.subList(limit - count, limit).clear();
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(this.docOut, this.posOut, this.payOut);
    }

    private static class PendingTerm {
        public final long docStartFP;
        public final long posStartFP;
        public final long payStartFP;
        public final long skipOffset;
        public final long lastPosBlockOffset;
        public final int singletonDocID;

        public PendingTerm(long docStartFP, long posStartFP, long payStartFP, long skipOffset, long lastPosBlockOffset, int singletonDocID) {
            this.docStartFP = docStartFP;
            this.posStartFP = posStartFP;
            this.payStartFP = payStartFP;
            this.skipOffset = skipOffset;
            this.lastPosBlockOffset = lastPosBlockOffset;
            this.singletonDocID = singletonDocID;
        }
    }
}

