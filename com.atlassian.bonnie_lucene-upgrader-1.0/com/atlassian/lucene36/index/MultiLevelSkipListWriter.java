/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.RAMOutputStream;
import java.io.IOException;

abstract class MultiLevelSkipListWriter {
    private int numberOfSkipLevels;
    private int skipInterval;
    private RAMOutputStream[] skipBuffer;

    protected MultiLevelSkipListWriter(int skipInterval, int maxSkipLevels, int df) {
        this.skipInterval = skipInterval;
        int n = this.numberOfSkipLevels = df == 0 ? 0 : (int)Math.floor(Math.log(df) / Math.log(skipInterval));
        if (this.numberOfSkipLevels > maxSkipLevels) {
            this.numberOfSkipLevels = maxSkipLevels;
        }
    }

    protected void init() {
        this.skipBuffer = new RAMOutputStream[this.numberOfSkipLevels];
        for (int i = 0; i < this.numberOfSkipLevels; ++i) {
            this.skipBuffer[i] = new RAMOutputStream();
        }
    }

    protected void resetSkip() {
        if (this.skipBuffer == null) {
            this.init();
        } else {
            for (int i = 0; i < this.skipBuffer.length; ++i) {
                this.skipBuffer[i].reset();
            }
        }
    }

    protected abstract void writeSkipData(int var1, IndexOutput var2) throws IOException;

    void bufferSkip(int df) throws IOException {
        int numLevels = 0;
        while (df % this.skipInterval == 0 && numLevels < this.numberOfSkipLevels) {
            ++numLevels;
            df /= this.skipInterval;
        }
        long childPointer = 0L;
        for (int level = 0; level < numLevels; ++level) {
            this.writeSkipData(level, this.skipBuffer[level]);
            long newChildPointer = this.skipBuffer[level].getFilePointer();
            if (level != 0) {
                this.skipBuffer[level].writeVLong(childPointer);
            }
            childPointer = newChildPointer;
        }
    }

    long writeSkip(IndexOutput output) throws IOException {
        long skipPointer = output.getFilePointer();
        if (this.skipBuffer == null || this.skipBuffer.length == 0) {
            return skipPointer;
        }
        for (int level = this.numberOfSkipLevels - 1; level > 0; --level) {
            long length = this.skipBuffer[level].getFilePointer();
            if (length <= 0L) continue;
            output.writeVLong(length);
            this.skipBuffer[level].writeTo(output);
        }
        this.skipBuffer[0].writeTo(output);
        return skipPointer;
    }
}

