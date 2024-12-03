/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.ByteBlockPool;

final class ByteSliceWriter
extends DataOutput {
    private byte[] slice;
    private int upto;
    private final ByteBlockPool pool;
    int offset0;

    public ByteSliceWriter(ByteBlockPool pool) {
        this.pool = pool;
    }

    public void init(int address) {
        this.slice = this.pool.buffers[address >> 15];
        assert (this.slice != null);
        this.upto = address & Short.MAX_VALUE;
        this.offset0 = address;
        assert (this.upto < this.slice.length);
    }

    @Override
    public void writeByte(byte b) {
        assert (this.slice != null);
        if (this.slice[this.upto] != 0) {
            this.upto = this.pool.allocSlice(this.slice, this.upto);
            this.slice = this.pool.buffer;
            this.offset0 = this.pool.byteOffset;
            assert (this.slice != null);
        }
        this.slice[this.upto++] = b;
        assert (this.upto != this.slice.length);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int len) {
        int offsetEnd = offset + len;
        while (offset < offsetEnd) {
            if (this.slice[this.upto] != 0) {
                this.upto = this.pool.allocSlice(this.slice, this.upto);
                this.slice = this.pool.buffer;
                this.offset0 = this.pool.byteOffset;
            }
            this.slice[this.upto++] = b[offset++];
            assert (this.upto != this.slice.length);
        }
    }

    public int getAddress() {
        return this.upto + (this.offset0 & Short.MIN_VALUE);
    }
}

