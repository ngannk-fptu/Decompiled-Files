/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.MultiLevelSkipListReader;
import com.atlassian.lucene36.store.IndexInput;
import java.io.IOException;
import java.util.Arrays;

class DefaultSkipListReader
extends MultiLevelSkipListReader {
    private boolean currentFieldStoresPayloads;
    private long[] freqPointer;
    private long[] proxPointer;
    private int[] payloadLength;
    private long lastFreqPointer;
    private long lastProxPointer;
    private int lastPayloadLength;

    DefaultSkipListReader(IndexInput skipStream, int maxSkipLevels, int skipInterval) {
        super(skipStream, maxSkipLevels, skipInterval);
        this.freqPointer = new long[maxSkipLevels];
        this.proxPointer = new long[maxSkipLevels];
        this.payloadLength = new int[maxSkipLevels];
    }

    void init(long skipPointer, long freqBasePointer, long proxBasePointer, int df, boolean storesPayloads) {
        super.init(skipPointer, df);
        this.currentFieldStoresPayloads = storesPayloads;
        this.lastFreqPointer = freqBasePointer;
        this.lastProxPointer = proxBasePointer;
        Arrays.fill(this.freqPointer, freqBasePointer);
        Arrays.fill(this.proxPointer, proxBasePointer);
        Arrays.fill(this.payloadLength, 0);
    }

    long getFreqPointer() {
        return this.lastFreqPointer;
    }

    long getProxPointer() {
        return this.lastProxPointer;
    }

    int getPayloadLength() {
        return this.lastPayloadLength;
    }

    protected void seekChild(int level) throws IOException {
        super.seekChild(level);
        this.freqPointer[level] = this.lastFreqPointer;
        this.proxPointer[level] = this.lastProxPointer;
        this.payloadLength[level] = this.lastPayloadLength;
    }

    protected void setLastSkipData(int level) {
        super.setLastSkipData(level);
        this.lastFreqPointer = this.freqPointer[level];
        this.lastProxPointer = this.proxPointer[level];
        this.lastPayloadLength = this.payloadLength[level];
    }

    protected int readSkipData(int level, IndexInput skipStream) throws IOException {
        int delta;
        if (this.currentFieldStoresPayloads) {
            delta = skipStream.readVInt();
            if ((delta & 1) != 0) {
                this.payloadLength[level] = skipStream.readVInt();
            }
            delta >>>= 1;
        } else {
            delta = skipStream.readVInt();
        }
        int n = level;
        this.freqPointer[n] = this.freqPointer[n] + (long)skipStream.readVInt();
        int n2 = level;
        this.proxPointer[n2] = this.proxPointer[n2] + (long)skipStream.readVInt();
        return delta;
    }
}

