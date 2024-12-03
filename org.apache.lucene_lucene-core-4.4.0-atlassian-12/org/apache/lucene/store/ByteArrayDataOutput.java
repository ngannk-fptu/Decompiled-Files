/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.BytesRef;

public class ByteArrayDataOutput
extends DataOutput {
    private byte[] bytes;
    private int pos;
    private int limit;

    public ByteArrayDataOutput(byte[] bytes) {
        this.reset(bytes);
    }

    public ByteArrayDataOutput(byte[] bytes, int offset, int len) {
        this.reset(bytes, offset, len);
    }

    public ByteArrayDataOutput() {
        this.reset(BytesRef.EMPTY_BYTES);
    }

    public void reset(byte[] bytes) {
        this.reset(bytes, 0, bytes.length);
    }

    public void reset(byte[] bytes, int offset, int len) {
        this.bytes = bytes;
        this.pos = offset;
        this.limit = offset + len;
    }

    public int getPosition() {
        return this.pos;
    }

    @Override
    public void writeByte(byte b) {
        assert (this.pos < this.limit);
        this.bytes[this.pos++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) {
        assert (this.pos + length <= this.limit);
        System.arraycopy(b, offset, this.bytes, this.pos, length);
        this.pos += length;
    }
}

