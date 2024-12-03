/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene41;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.codecs.MultiLevelSkipListReader;
import org.apache.lucene.store.IndexInput;

final class Lucene41SkipReader
extends MultiLevelSkipListReader {
    private final int blockSize;
    private long[] docPointer;
    private long[] posPointer;
    private long[] payPointer;
    private int[] posBufferUpto;
    private int[] payloadByteUpto;
    private long lastPosPointer;
    private long lastPayPointer;
    private int lastPayloadByteUpto;
    private long lastDocPointer;
    private int lastPosBufferUpto;

    public Lucene41SkipReader(IndexInput skipStream, int maxSkipLevels, int blockSize, boolean hasPos, boolean hasOffsets, boolean hasPayloads) {
        super(skipStream, maxSkipLevels, blockSize, 8);
        this.blockSize = blockSize;
        this.docPointer = new long[maxSkipLevels];
        if (hasPos) {
            this.posPointer = new long[maxSkipLevels];
            this.posBufferUpto = new int[maxSkipLevels];
            this.payloadByteUpto = (int[])(hasPayloads ? new int[maxSkipLevels] : null);
            this.payPointer = (long[])(hasOffsets || hasPayloads ? new long[maxSkipLevels] : null);
        } else {
            this.posPointer = null;
        }
    }

    protected int trim(int df) {
        return df % this.blockSize == 0 ? df - 1 : df;
    }

    public void init(long skipPointer, long docBasePointer, long posBasePointer, long payBasePointer, int df) {
        super.init(skipPointer, this.trim(df));
        this.lastDocPointer = docBasePointer;
        this.lastPosPointer = posBasePointer;
        this.lastPayPointer = payBasePointer;
        Arrays.fill(this.docPointer, docBasePointer);
        if (this.posPointer != null) {
            Arrays.fill(this.posPointer, posBasePointer);
            if (this.payPointer != null) {
                Arrays.fill(this.payPointer, payBasePointer);
            }
        } else assert (posBasePointer == 0L);
    }

    public long getDocPointer() {
        return this.lastDocPointer;
    }

    public long getPosPointer() {
        return this.lastPosPointer;
    }

    public int getPosBufferUpto() {
        return this.lastPosBufferUpto;
    }

    public long getPayPointer() {
        return this.lastPayPointer;
    }

    public int getPayloadByteUpto() {
        return this.lastPayloadByteUpto;
    }

    public int getNextSkipDoc() {
        return this.skipDoc[0];
    }

    @Override
    protected void seekChild(int level) throws IOException {
        super.seekChild(level);
        this.docPointer[level] = this.lastDocPointer;
        if (this.posPointer != null) {
            this.posPointer[level] = this.lastPosPointer;
            this.posBufferUpto[level] = this.lastPosBufferUpto;
            if (this.payloadByteUpto != null) {
                this.payloadByteUpto[level] = this.lastPayloadByteUpto;
            }
            if (this.payPointer != null) {
                this.payPointer[level] = this.lastPayPointer;
            }
        }
    }

    @Override
    protected void setLastSkipData(int level) {
        super.setLastSkipData(level);
        this.lastDocPointer = this.docPointer[level];
        if (this.posPointer != null) {
            this.lastPosPointer = this.posPointer[level];
            this.lastPosBufferUpto = this.posBufferUpto[level];
            if (this.payPointer != null) {
                this.lastPayPointer = this.payPointer[level];
            }
            if (this.payloadByteUpto != null) {
                this.lastPayloadByteUpto = this.payloadByteUpto[level];
            }
        }
    }

    @Override
    protected int readSkipData(int level, IndexInput skipStream) throws IOException {
        int delta = skipStream.readVInt();
        int n = level;
        this.docPointer[n] = this.docPointer[n] + (long)skipStream.readVInt();
        if (this.posPointer != null) {
            int n2 = level;
            this.posPointer[n2] = this.posPointer[n2] + (long)skipStream.readVInt();
            this.posBufferUpto[level] = skipStream.readVInt();
            if (this.payloadByteUpto != null) {
                this.payloadByteUpto[level] = skipStream.readVInt();
            }
            if (this.payPointer != null) {
                int n3 = level;
                this.payPointer[n3] = this.payPointer[n3] + (long)skipStream.readVInt();
            }
        }
        return delta;
    }
}

