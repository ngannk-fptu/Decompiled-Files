/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

class ChunkContentIterator {
    private final byte[] signedChunk;
    private int pos;

    public ChunkContentIterator(byte[] signedChunk) {
        this.signedChunk = signedChunk;
    }

    public boolean hasNext() {
        return this.pos < this.signedChunk.length;
    }

    public int read(byte[] output, int offset, int length) {
        if (length == 0) {
            return 0;
        }
        if (!this.hasNext()) {
            return -1;
        }
        int remaingBytesNum = this.signedChunk.length - this.pos;
        int bytesToRead = Math.min(remaingBytesNum, length);
        System.arraycopy(this.signedChunk, this.pos, output, offset, bytesToRead);
        this.pos += bytesToRead;
        return bytesToRead;
    }
}

