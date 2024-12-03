/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.MultiLevelSkipListWriter;
import org.apache.lucene.store.IndexOutput;

final class Lucene41SkipWriter
extends MultiLevelSkipListWriter {
    private int[] lastSkipDoc;
    private long[] lastSkipDocPointer;
    private long[] lastSkipPosPointer;
    private long[] lastSkipPayPointer;
    private int[] lastPayloadByteUpto;
    private final IndexOutput docOut;
    private final IndexOutput posOut;
    private final IndexOutput payOut;
    private int curDoc;
    private long curDocPointer;
    private long curPosPointer;
    private long curPayPointer;
    private int curPosBufferUpto;
    private int curPayloadByteUpto;
    private boolean fieldHasPositions;
    private boolean fieldHasOffsets;
    private boolean fieldHasPayloads;

    public Lucene41SkipWriter(int maxSkipLevels, int blockSize, int docCount, IndexOutput docOut, IndexOutput posOut, IndexOutput payOut) {
        super(blockSize, 8, maxSkipLevels, docCount);
        this.docOut = docOut;
        this.posOut = posOut;
        this.payOut = payOut;
        this.lastSkipDoc = new int[maxSkipLevels];
        this.lastSkipDocPointer = new long[maxSkipLevels];
        if (posOut != null) {
            this.lastSkipPosPointer = new long[maxSkipLevels];
            if (payOut != null) {
                this.lastSkipPayPointer = new long[maxSkipLevels];
            }
            this.lastPayloadByteUpto = new int[maxSkipLevels];
        }
    }

    public void setField(boolean fieldHasPositions, boolean fieldHasOffsets, boolean fieldHasPayloads) {
        this.fieldHasPositions = fieldHasPositions;
        this.fieldHasOffsets = fieldHasOffsets;
        this.fieldHasPayloads = fieldHasPayloads;
    }

    @Override
    public void resetSkip() {
        super.resetSkip();
        Arrays.fill(this.lastSkipDoc, 0);
        Arrays.fill(this.lastSkipDocPointer, this.docOut.getFilePointer());
        if (this.fieldHasPositions) {
            Arrays.fill(this.lastSkipPosPointer, this.posOut.getFilePointer());
            if (this.fieldHasPayloads) {
                Arrays.fill(this.lastPayloadByteUpto, 0);
            }
            if (this.fieldHasOffsets || this.fieldHasPayloads) {
                Arrays.fill(this.lastSkipPayPointer, this.payOut.getFilePointer());
            }
        }
    }

    public void bufferSkip(int doc, int numDocs, long posFP, long payFP, int posBufferUpto, int payloadByteUpto) throws IOException {
        this.curDoc = doc;
        this.curDocPointer = this.docOut.getFilePointer();
        this.curPosPointer = posFP;
        this.curPayPointer = payFP;
        this.curPosBufferUpto = posBufferUpto;
        this.curPayloadByteUpto = payloadByteUpto;
        this.bufferSkip(numDocs);
    }

    @Override
    protected void writeSkipData(int level, IndexOutput skipBuffer) throws IOException {
        int delta = this.curDoc - this.lastSkipDoc[level];
        skipBuffer.writeVInt(delta);
        this.lastSkipDoc[level] = this.curDoc;
        skipBuffer.writeVInt((int)(this.curDocPointer - this.lastSkipDocPointer[level]));
        this.lastSkipDocPointer[level] = this.curDocPointer;
        if (this.fieldHasPositions) {
            skipBuffer.writeVInt((int)(this.curPosPointer - this.lastSkipPosPointer[level]));
            this.lastSkipPosPointer[level] = this.curPosPointer;
            skipBuffer.writeVInt(this.curPosBufferUpto);
            if (this.fieldHasPayloads) {
                skipBuffer.writeVInt(this.curPayloadByteUpto);
            }
            if (this.fieldHasOffsets || this.fieldHasPayloads) {
                skipBuffer.writeVInt((int)(this.curPayPointer - this.lastSkipPayPointer[level]));
                this.lastSkipPayPointer[level] = this.curPayPointer;
            }
        }
    }
}

