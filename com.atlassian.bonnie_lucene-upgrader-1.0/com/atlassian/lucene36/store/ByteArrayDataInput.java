/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.DataInput;
import com.atlassian.lucene36.util.BytesRef;

public final class ByteArrayDataInput
extends DataInput {
    private byte[] bytes;
    private int pos;
    private int limit;

    public ByteArrayDataInput(byte[] bytes) {
        this.reset(bytes);
    }

    public ByteArrayDataInput(byte[] bytes, int offset, int len) {
        this.reset(bytes, offset, len);
    }

    public ByteArrayDataInput() {
        this.reset(BytesRef.EMPTY_BYTES);
    }

    public void reset(byte[] bytes) {
        this.reset(bytes, 0, bytes.length);
    }

    public int getPosition() {
        return this.pos;
    }

    public void reset(byte[] bytes, int offset, int len) {
        this.bytes = bytes;
        this.pos = offset;
        this.limit = offset + len;
    }

    public boolean eof() {
        return this.pos == this.limit;
    }

    public void skipBytes(int count) {
        this.pos += count;
    }

    public short readShort() {
        return (short)((this.bytes[this.pos++] & 0xFF) << 8 | this.bytes[this.pos++] & 0xFF);
    }

    public int readInt() {
        return (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | this.bytes[this.pos++] & 0xFF;
    }

    public long readLong() {
        int i1 = (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | this.bytes[this.pos++] & 0xFF;
        int i2 = (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | this.bytes[this.pos++] & 0xFF;
        return (long)i1 << 32 | (long)i2 & 0xFFFFFFFFL;
    }

    public int readVInt() {
        byte b = this.bytes[this.pos++];
        int i = b & 0x7F;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 21;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0xF) << 28;
        if ((b & 0xF0) == 0) {
            return i;
        }
        throw new RuntimeException("Invalid vInt detected (too many bits)");
    }

    public long readVLong() {
        byte b = this.bytes[this.pos++];
        long i = (long)b & 0x7FL;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 7;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 14;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 21;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 28;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 35;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 42;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 49;
        if ((b & 0x80) == 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 56;
        if ((b & 0x80) == 0) {
            return i;
        }
        throw new RuntimeException("Invalid vLong detected (negative values disallowed)");
    }

    public byte readByte() {
        return this.bytes[this.pos++];
    }

    public void readBytes(byte[] b, int offset, int len) {
        System.arraycopy(this.bytes, this.pos, b, offset, len);
        this.pos += len;
    }
}

