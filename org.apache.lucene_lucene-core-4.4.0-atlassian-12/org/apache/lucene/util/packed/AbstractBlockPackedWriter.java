/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.packed;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.PackedInts;

abstract class AbstractBlockPackedWriter {
    static final int MIN_BLOCK_SIZE = 64;
    static final int MAX_BLOCK_SIZE = 0x8000000;
    static final int MIN_VALUE_EQUALS_0 = 1;
    static final int BPV_SHIFT = 1;
    protected DataOutput out;
    protected final long[] values;
    protected byte[] blocks;
    protected int off;
    protected long ord;
    protected boolean finished;

    static long zigZagEncode(long n) {
        return n >> 63 ^ n << 1;
    }

    static void writeVLong(DataOutput out, long i) throws IOException {
        int k = 0;
        while ((i & 0xFFFFFFFFFFFFFF80L) != 0L && k++ < 8) {
            out.writeByte((byte)(i & 0x7FL | 0x80L));
            i >>>= 7;
        }
        out.writeByte((byte)i);
    }

    public AbstractBlockPackedWriter(DataOutput out, int blockSize) {
        PackedInts.checkBlockSize(blockSize, 64, 0x8000000);
        this.reset(out);
        this.values = new long[blockSize];
    }

    public void reset(DataOutput out) {
        assert (out != null);
        this.out = out;
        this.off = 0;
        this.ord = 0L;
        this.finished = false;
    }

    private void checkNotFinished() {
        if (this.finished) {
            throw new IllegalStateException("Already finished");
        }
    }

    public void add(long l) throws IOException {
        this.checkNotFinished();
        if (this.off == this.values.length) {
            this.flush();
        }
        this.values[this.off++] = l;
        ++this.ord;
    }

    void addBlockOfZeros() throws IOException {
        this.checkNotFinished();
        if (this.off != 0 && this.off != this.values.length) {
            throw new IllegalStateException("" + this.off);
        }
        if (this.off == this.values.length) {
            this.flush();
        }
        Arrays.fill(this.values, 0L);
        this.off = this.values.length;
        this.ord += (long)this.values.length;
    }

    public void finish() throws IOException {
        this.checkNotFinished();
        if (this.off > 0) {
            this.flush();
        }
        this.finished = true;
    }

    public long ord() {
        return this.ord;
    }

    protected abstract void flush() throws IOException;

    protected final void writeValues(int bitsRequired) throws IOException {
        PackedInts.Encoder encoder = PackedInts.getEncoder(PackedInts.Format.PACKED, 1, bitsRequired);
        int iterations = this.values.length / encoder.byteValueCount();
        int blockSize = encoder.byteBlockCount() * iterations;
        if (this.blocks == null || this.blocks.length < blockSize) {
            this.blocks = new byte[blockSize];
        }
        if (this.off < this.values.length) {
            Arrays.fill(this.values, this.off, this.values.length, 0L);
        }
        encoder.encode(this.values, 0, this.blocks, 0, iterations);
        int blockCount = (int)PackedInts.Format.PACKED.byteCount(1, this.off, bitsRequired);
        this.out.writeBytes(this.blocks, blockCount);
    }
}

