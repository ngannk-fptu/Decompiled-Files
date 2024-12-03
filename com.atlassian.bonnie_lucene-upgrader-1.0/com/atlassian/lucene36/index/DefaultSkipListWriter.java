/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.MultiLevelSkipListWriter;
import com.atlassian.lucene36.store.IndexOutput;
import java.io.IOException;
import java.util.Arrays;

class DefaultSkipListWriter
extends MultiLevelSkipListWriter {
    private int[] lastSkipDoc;
    private int[] lastSkipPayloadLength;
    private long[] lastSkipFreqPointer;
    private long[] lastSkipProxPointer;
    private IndexOutput freqOutput;
    private IndexOutput proxOutput;
    private int curDoc;
    private boolean curStorePayloads;
    private int curPayloadLength;
    private long curFreqPointer;
    private long curProxPointer;

    DefaultSkipListWriter(int skipInterval, int numberOfSkipLevels, int docCount, IndexOutput freqOutput, IndexOutput proxOutput) {
        super(skipInterval, numberOfSkipLevels, docCount);
        this.freqOutput = freqOutput;
        this.proxOutput = proxOutput;
        this.lastSkipDoc = new int[numberOfSkipLevels];
        this.lastSkipPayloadLength = new int[numberOfSkipLevels];
        this.lastSkipFreqPointer = new long[numberOfSkipLevels];
        this.lastSkipProxPointer = new long[numberOfSkipLevels];
    }

    void setFreqOutput(IndexOutput freqOutput) {
        this.freqOutput = freqOutput;
    }

    void setProxOutput(IndexOutput proxOutput) {
        this.proxOutput = proxOutput;
    }

    void setSkipData(int doc, boolean storePayloads, int payloadLength) {
        this.curDoc = doc;
        this.curStorePayloads = storePayloads;
        this.curPayloadLength = payloadLength;
        this.curFreqPointer = this.freqOutput.getFilePointer();
        if (this.proxOutput != null) {
            this.curProxPointer = this.proxOutput.getFilePointer();
        }
    }

    protected void resetSkip() {
        super.resetSkip();
        Arrays.fill(this.lastSkipDoc, 0);
        Arrays.fill(this.lastSkipPayloadLength, -1);
        Arrays.fill(this.lastSkipFreqPointer, this.freqOutput.getFilePointer());
        if (this.proxOutput != null) {
            Arrays.fill(this.lastSkipProxPointer, this.proxOutput.getFilePointer());
        }
    }

    protected void writeSkipData(int level, IndexOutput skipBuffer) throws IOException {
        if (this.curStorePayloads) {
            int delta = this.curDoc - this.lastSkipDoc[level];
            if (this.curPayloadLength == this.lastSkipPayloadLength[level]) {
                skipBuffer.writeVInt(delta * 2);
            } else {
                skipBuffer.writeVInt(delta * 2 + 1);
                skipBuffer.writeVInt(this.curPayloadLength);
                this.lastSkipPayloadLength[level] = this.curPayloadLength;
            }
        } else {
            skipBuffer.writeVInt(this.curDoc - this.lastSkipDoc[level]);
        }
        skipBuffer.writeVInt((int)(this.curFreqPointer - this.lastSkipFreqPointer[level]));
        skipBuffer.writeVInt((int)(this.curProxPointer - this.lastSkipProxPointer[level]));
        this.lastSkipDoc[level] = this.curDoc;
        this.lastSkipFreqPointer[level] = this.curFreqPointer;
        this.lastSkipProxPointer[level] = this.curProxPointer;
    }
}

