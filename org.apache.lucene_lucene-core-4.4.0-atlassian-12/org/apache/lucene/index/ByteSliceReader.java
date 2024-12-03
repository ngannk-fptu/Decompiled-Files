/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.ByteBlockPool;

final class ByteSliceReader
extends DataInput {
    ByteBlockPool pool;
    int bufferUpto;
    byte[] buffer;
    public int upto;
    int limit;
    int level;
    public int bufferOffset;
    public int endIndex;

    ByteSliceReader() {
    }

    public void init(ByteBlockPool pool, int startIndex, int endIndex) {
        assert (endIndex - startIndex >= 0);
        assert (startIndex >= 0);
        assert (endIndex >= 0);
        this.pool = pool;
        this.endIndex = endIndex;
        this.level = 0;
        this.bufferUpto = startIndex / 32768;
        this.bufferOffset = this.bufferUpto * 32768;
        this.buffer = pool.buffers[this.bufferUpto];
        this.upto = startIndex & Short.MAX_VALUE;
        int firstSize = ByteBlockPool.LEVEL_SIZE_ARRAY[0];
        this.limit = startIndex + firstSize >= endIndex ? endIndex & Short.MAX_VALUE : this.upto + firstSize - 4;
    }

    public boolean eof() {
        assert (this.upto + this.bufferOffset <= this.endIndex);
        return this.upto + this.bufferOffset == this.endIndex;
    }

    @Override
    public byte readByte() {
        assert (!this.eof());
        assert (this.upto <= this.limit);
        if (this.upto == this.limit) {
            this.nextSlice();
        }
        return this.buffer[this.upto++];
    }

    public long writeTo(DataOutput out) throws IOException {
        long size = 0L;
        while (true) {
            if (this.limit + this.bufferOffset == this.endIndex) {
                assert (this.endIndex - this.bufferOffset >= this.upto);
                break;
            }
            out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
            size += (long)(this.limit - this.upto);
            this.nextSlice();
        }
        out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
        return size += (long)(this.limit - this.upto);
    }

    public void nextSlice() {
        int nextIndex = ((this.buffer[this.limit] & 0xFF) << 24) + ((this.buffer[1 + this.limit] & 0xFF) << 16) + ((this.buffer[2 + this.limit] & 0xFF) << 8) + (this.buffer[3 + this.limit] & 0xFF);
        this.level = ByteBlockPool.NEXT_LEVEL_ARRAY[this.level];
        int newSize = ByteBlockPool.LEVEL_SIZE_ARRAY[this.level];
        this.bufferUpto = nextIndex / 32768;
        this.bufferOffset = this.bufferUpto * 32768;
        this.buffer = this.pool.buffers[this.bufferUpto];
        this.upto = nextIndex & Short.MAX_VALUE;
        if (nextIndex + newSize >= this.endIndex) {
            assert (this.endIndex - nextIndex > 0);
            this.limit = this.endIndex - this.bufferOffset;
        } else {
            this.limit = this.upto + newSize - 4;
        }
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) {
        while (len > 0) {
            int numLeft = this.limit - this.upto;
            if (numLeft < len) {
                System.arraycopy(this.buffer, this.upto, b, offset, numLeft);
                offset += numLeft;
                len -= numLeft;
                this.nextSlice();
                continue;
            }
            System.arraycopy(this.buffer, this.upto, b, offset, len);
            this.upto += len;
            break;
        }
    }
}

