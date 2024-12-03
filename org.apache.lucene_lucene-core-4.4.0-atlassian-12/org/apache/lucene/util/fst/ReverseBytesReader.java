/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.fst;

import org.apache.lucene.util.fst.FST;

final class ReverseBytesReader
extends FST.BytesReader {
    private final byte[] bytes;
    private int pos;

    public ReverseBytesReader(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte readByte() {
        return this.bytes[this.pos--];
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) {
        for (int i = 0; i < len; ++i) {
            b[offset + i] = this.bytes[this.pos--];
        }
    }

    @Override
    public void skipBytes(int count) {
        this.pos -= count;
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
        return true;
    }
}

