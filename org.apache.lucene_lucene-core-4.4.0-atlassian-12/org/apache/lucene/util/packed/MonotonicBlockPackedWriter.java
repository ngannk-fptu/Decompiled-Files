/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.AbstractBlockPackedWriter;
import org.apache.lucene.util.packed.PackedInts;

public final class MonotonicBlockPackedWriter
extends AbstractBlockPackedWriter {
    public MonotonicBlockPackedWriter(DataOutput out, int blockSize) {
        super(out, blockSize);
    }

    @Override
    public void add(long l) throws IOException {
        assert (l >= 0L);
        super.add(l);
    }

    @Override
    protected void flush() throws IOException {
        assert (this.off > 0);
        long min = this.values[0];
        float avg = this.off == 1 ? 0.0f : (float)(this.values[this.off - 1] - min) / (float)(this.off - 1);
        long maxZigZagDelta = 0L;
        for (int i = 0; i < this.off; ++i) {
            this.values[i] = MonotonicBlockPackedWriter.zigZagEncode(this.values[i] - min - (long)(avg * (float)i));
            maxZigZagDelta = Math.max(maxZigZagDelta, this.values[i]);
        }
        this.out.writeVLong(min);
        this.out.writeInt(Float.floatToIntBits(avg));
        if (maxZigZagDelta == 0L) {
            this.out.writeVInt(0);
        } else {
            int bitsRequired = PackedInts.bitsRequired(maxZigZagDelta);
            this.out.writeVInt(bitsRequired);
            this.writeValues(bitsRequired);
        }
        this.off = 0;
    }
}

