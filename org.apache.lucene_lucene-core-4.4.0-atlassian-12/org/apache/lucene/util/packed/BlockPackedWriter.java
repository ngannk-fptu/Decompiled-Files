/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.AbstractBlockPackedWriter;
import org.apache.lucene.util.packed.PackedInts;

public final class BlockPackedWriter
extends AbstractBlockPackedWriter {
    public BlockPackedWriter(DataOutput out, int blockSize) {
        super(out, blockSize);
    }

    @Override
    protected void flush() throws IOException {
        int bitsRequired;
        assert (this.off > 0);
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < this.off; ++i) {
            min = Math.min(this.values[i], min);
            max = Math.max(this.values[i], max);
        }
        long delta = max - min;
        int n = delta < 0L ? 64 : (bitsRequired = delta == 0L ? 0 : PackedInts.bitsRequired(delta));
        if (bitsRequired == 64) {
            min = 0L;
        } else if (min > 0L) {
            min = Math.max(0L, max - PackedInts.maxValue(bitsRequired));
        }
        int token = bitsRequired << 1 | (min == 0L ? 1 : 0);
        this.out.writeByte((byte)token);
        if (min != 0L) {
            BlockPackedWriter.writeVLong(this.out, BlockPackedWriter.zigZagEncode(min) - 1L);
        }
        if (bitsRequired > 0) {
            if (min != 0L) {
                int i = 0;
                while (i < this.off) {
                    int n2 = i++;
                    this.values[n2] = this.values[n2] - min;
                }
            }
            this.writeValues(bitsRequired);
        }
        this.off = 0;
    }
}

