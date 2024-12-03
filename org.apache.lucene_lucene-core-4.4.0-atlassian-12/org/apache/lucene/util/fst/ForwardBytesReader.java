/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import org.apache.lucene.util.fst.FST;

final class ForwardBytesReader
extends FST.BytesReader {
    private final byte[] bytes;
    private int pos;

    public ForwardBytesReader(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte readByte() {
        return this.bytes[this.pos++];
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) {
        System.arraycopy(this.bytes, this.pos, b, offset, len);
        this.pos += len;
    }

    @Override
    public void skipBytes(int count) {
        this.pos += count;
    }

    @Override
    public long getPosition() {
        return this.pos;
    }

    @Override
    public void setPosition(long pos) {
        this.pos = (int)pos;
    }

    @Override
    public boolean reversed() {
        return false;
    }
}

