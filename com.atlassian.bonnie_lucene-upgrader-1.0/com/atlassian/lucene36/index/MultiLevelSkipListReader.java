/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.store.BufferedIndexInput;
import com.atlassian.lucene36.store.IndexInput;
import java.io.IOException;
import java.util.Arrays;

abstract class MultiLevelSkipListReader {
    private int maxNumberOfSkipLevels;
    private int numberOfSkipLevels;
    private int numberOfLevelsToBuffer = 1;
    private int docCount;
    private boolean haveSkipped;
    private IndexInput[] skipStream;
    private long[] skipPointer;
    private int[] skipInterval;
    private int[] numSkipped;
    private int[] skipDoc;
    private int lastDoc;
    private long[] childPointer;
    private long lastChildPointer;
    private boolean inputIsBuffered;

    public MultiLevelSkipListReader(IndexInput skipStream, int maxSkipLevels, int skipInterval) {
        this.skipStream = new IndexInput[maxSkipLevels];
        this.skipPointer = new long[maxSkipLevels];
        this.childPointer = new long[maxSkipLevels];
        this.numSkipped = new int[maxSkipLevels];
        this.maxNumberOfSkipLevels = maxSkipLevels;
        this.skipInterval = new int[maxSkipLevels];
        this.skipStream[0] = skipStream;
        this.inputIsBuffered = skipStream instanceof BufferedIndexInput;
        this.skipInterval[0] = skipInterval;
        for (int i = 1; i < maxSkipLevels; ++i) {
            this.skipInterval[i] = this.skipInterval[i - 1] * skipInterval;
        }
        this.skipDoc = new int[maxSkipLevels];
    }

    int getDoc() {
        return this.lastDoc;
    }

    int skipTo(int target) throws IOException {
        int level;
        if (!this.haveSkipped) {
            this.loadSkipLevels();
            this.haveSkipped = true;
        }
        for (level = 0; level < this.numberOfSkipLevels - 1 && target > this.skipDoc[level + 1]; ++level) {
        }
        while (level >= 0) {
            if (target > this.skipDoc[level]) {
                if (this.loadNextSkip(level)) continue;
                continue;
            }
            if (level > 0 && this.lastChildPointer > this.skipStream[level - 1].getFilePointer()) {
                this.seekChild(level - 1);
            }
            --level;
        }
        return this.numSkipped[0] - this.skipInterval[0] - 1;
    }

    private boolean loadNextSkip(int level) throws IOException {
        this.setLastSkipData(level);
        int n = level;
        this.numSkipped[n] = this.numSkipped[n] + this.skipInterval[level];
        if (this.numSkipped[level] > this.docCount) {
            this.skipDoc[level] = Integer.MAX_VALUE;
            if (this.numberOfSkipLevels > level) {
                this.numberOfSkipLevels = level;
            }
            return false;
        }
        int n2 = level;
        this.skipDoc[n2] = this.skipDoc[n2] + this.readSkipData(level, this.skipStream[level]);
        if (level != 0) {
            this.childPointer[level] = this.skipStream[level].readVLong() + this.skipPointer[level - 1];
        }
        return true;
    }

    protected void seekChild(int level) throws IOException {
        this.skipStream[level].seek(this.lastChildPointer);
        this.numSkipped[level] = this.numSkipped[level + 1] - this.skipInterval[level + 1];
        this.skipDoc[level] = this.lastDoc;
        if (level > 0) {
            this.childPointer[level] = this.skipStream[level].readVLong() + this.skipPointer[level - 1];
        }
    }

    void close() throws IOException {
        for (int i = 1; i < this.skipStream.length; ++i) {
            if (this.skipStream[i] == null) continue;
            this.skipStream[i].close();
        }
    }

    void init(long skipPointer, int df) {
        this.skipPointer[0] = skipPointer;
        this.docCount = df;
        Arrays.fill(this.skipDoc, 0);
        Arrays.fill(this.numSkipped, 0);
        Arrays.fill(this.childPointer, 0L);
        this.haveSkipped = false;
        for (int i = 1; i < this.numberOfSkipLevels; ++i) {
            this.skipStream[i] = null;
        }
    }

    private void loadSkipLevels() throws IOException {
        int n = this.numberOfSkipLevels = this.docCount == 0 ? 0 : (int)Math.floor(Math.log(this.docCount) / Math.log(this.skipInterval[0]));
        if (this.numberOfSkipLevels > this.maxNumberOfSkipLevels) {
            this.numberOfSkipLevels = this.maxNumberOfSkipLevels;
        }
        this.skipStream[0].seek(this.skipPointer[0]);
        int toBuffer = this.numberOfLevelsToBuffer;
        for (int i = this.numberOfSkipLevels - 1; i > 0; --i) {
            long length = this.skipStream[0].readVLong();
            this.skipPointer[i] = this.skipStream[0].getFilePointer();
            if (toBuffer > 0) {
                this.skipStream[i] = new SkipBuffer(this.skipStream[0], (int)length);
                --toBuffer;
                continue;
            }
            this.skipStream[i] = (IndexInput)this.skipStream[0].clone();
            if (this.inputIsBuffered && length < 1024L) {
                ((BufferedIndexInput)this.skipStream[i]).setBufferSize((int)length);
            }
            this.skipStream[0].seek(this.skipStream[0].getFilePointer() + length);
        }
        this.skipPointer[0] = this.skipStream[0].getFilePointer();
    }

    protected abstract int readSkipData(int var1, IndexInput var2) throws IOException;

    protected void setLastSkipData(int level) {
        this.lastDoc = this.skipDoc[level];
        this.lastChildPointer = this.childPointer[level];
    }

    private static final class SkipBuffer
    extends IndexInput {
        private byte[] data;
        private long pointer;
        private int pos;

        SkipBuffer(IndexInput input, int length) throws IOException {
            super("SkipBuffer on " + input);
            this.data = new byte[length];
            this.pointer = input.getFilePointer();
            input.readBytes(this.data, 0, length);
        }

        public void close() throws IOException {
            this.data = null;
        }

        public long getFilePointer() {
            return this.pointer + (long)this.pos;
        }

        public long length() {
            return this.data.length;
        }

        public byte readByte() throws IOException {
            return this.data[this.pos++];
        }

        public void readBytes(byte[] b, int offset, int len) throws IOException {
            System.arraycopy(this.data, this.pos, b, offset, len);
            this.pos += len;
        }

        public void seek(long pos) throws IOException {
            this.pos = (int)(pos - this.pointer);
        }
    }
}

