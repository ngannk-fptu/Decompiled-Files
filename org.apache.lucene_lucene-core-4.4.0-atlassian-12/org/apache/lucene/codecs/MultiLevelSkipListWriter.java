/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.util.MathUtil;

public abstract class MultiLevelSkipListWriter {
    protected int numberOfSkipLevels;
    private int skipInterval;
    private int skipMultiplier;
    private RAMOutputStream[] skipBuffer;

    protected MultiLevelSkipListWriter(int skipInterval, int skipMultiplier, int maxSkipLevels, int df) {
        this.skipInterval = skipInterval;
        this.skipMultiplier = skipMultiplier;
        this.numberOfSkipLevels = df <= skipInterval ? 1 : 1 + MathUtil.log(df / skipInterval, skipMultiplier);
        if (this.numberOfSkipLevels > maxSkipLevels) {
            this.numberOfSkipLevels = maxSkipLevels;
        }
    }

    protected MultiLevelSkipListWriter(int skipInterval, int maxSkipLevels, int df) {
        this(skipInterval, skipInterval, maxSkipLevels, df);
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

    public void bufferSkip(int df) throws IOException {
        assert (df % this.skipInterval == 0);
        int numLevels = 1;
        df /= this.skipInterval;
        while (df % this.skipMultiplier == 0 && numLevels < this.numberOfSkipLevels) {
            ++numLevels;
            df /= this.skipMultiplier;
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

    public long writeSkip(IndexOutput output) throws IOException {
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

