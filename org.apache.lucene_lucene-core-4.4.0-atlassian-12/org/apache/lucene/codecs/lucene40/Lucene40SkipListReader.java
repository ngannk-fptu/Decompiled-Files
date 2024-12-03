/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene40;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.MultiLevelSkipListReader;
import org.apache.lucene.store.IndexInput;

@Deprecated
public class Lucene40SkipListReader
extends MultiLevelSkipListReader {
    private boolean currentFieldStoresPayloads;
    private boolean currentFieldStoresOffsets;
    private long[] freqPointer;
    private long[] proxPointer;
    private int[] payloadLength;
    private int[] offsetLength;
    private long lastFreqPointer;
    private long lastProxPointer;
    private int lastPayloadLength;
    private int lastOffsetLength;

    public Lucene40SkipListReader(IndexInput skipStream, int maxSkipLevels, int skipInterval) {
        super(skipStream, maxSkipLevels, skipInterval);
        this.freqPointer = new long[maxSkipLevels];
        this.proxPointer = new long[maxSkipLevels];
        this.payloadLength = new int[maxSkipLevels];
        this.offsetLength = new int[maxSkipLevels];
    }

    public void init(long skipPointer, long freqBasePointer, long proxBasePointer, int df, boolean storesPayloads, boolean storesOffsets) {
        super.init(skipPointer, df);
        this.currentFieldStoresPayloads = storesPayloads;
        this.currentFieldStoresOffsets = storesOffsets;
        this.lastFreqPointer = freqBasePointer;
        this.lastProxPointer = proxBasePointer;
        Arrays.fill(this.freqPointer, freqBasePointer);
        Arrays.fill(this.proxPointer, proxBasePointer);
        Arrays.fill(this.payloadLength, 0);
        Arrays.fill(this.offsetLength, 0);
    }

    public long getFreqPointer() {
        return this.lastFreqPointer;
    }

    public long getProxPointer() {
        return this.lastProxPointer;
    }

    public int getPayloadLength() {
        return this.lastPayloadLength;
    }

    public int getOffsetLength() {
        return this.lastOffsetLength;
    }

    @Override
    protected void seekChild(int level) throws IOException {
        super.seekChild(level);
        this.freqPointer[level] = this.lastFreqPointer;
        this.proxPointer[level] = this.lastProxPointer;
        this.payloadLength[level] = this.lastPayloadLength;
        this.offsetLength[level] = this.lastOffsetLength;
    }

    @Override
    protected void setLastSkipData(int level) {
        super.setLastSkipData(level);
        this.lastFreqPointer = this.freqPointer[level];
        this.lastProxPointer = this.proxPointer[level];
        this.lastPayloadLength = this.payloadLength[level];
        this.lastOffsetLength = this.offsetLength[level];
    }

    @Override
    protected int readSkipData(int level, IndexInput skipStream) throws IOException {
        int delta;
        if (this.currentFieldStoresPayloads || this.currentFieldStoresOffsets) {
            delta = skipStream.readVInt();
            if ((delta & 1) != 0) {
                if (this.currentFieldStoresPayloads) {
                    this.payloadLength[level] = skipStream.readVInt();
                }
                if (this.currentFieldStoresOffsets) {
                    this.offsetLength[level] = skipStream.readVInt();
                }
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

