/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.IndexInput;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.store.RAMFile;
import java.io.EOFException;
import java.io.IOException;

public class RAMInputStream
extends IndexInput
implements Cloneable {
    static final int BUFFER_SIZE = 1024;
    private RAMFile file;
    private long length;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;

    @Deprecated
    public RAMInputStream(RAMFile f) throws IOException {
        this("anonymous", f);
    }

    public RAMInputStream(String name, RAMFile f) throws IOException {
        super("RAMInputStream(name=" + name + ")");
        this.file = f;
        this.length = this.file.length;
        if (this.length / 1024L >= Integer.MAX_VALUE) {
            throw new IOException("RAMInputStream too large length=" + this.length + ": " + name);
        }
        this.currentBufferIndex = -1;
        this.currentBuffer = null;
    }

    public void close() {
    }

    public long length() {
        return this.length;
    }

    public byte readByte() throws IOException {
        if (this.bufferPosition >= this.bufferLength) {
            ++this.currentBufferIndex;
            this.switchCurrentBuffer(true);
        }
        return this.currentBuffer[this.bufferPosition++];
    }

    public void readBytes(byte[] b, int offset, int len) throws IOException {
        while (len > 0) {
            int remainInBuffer;
            if (this.bufferPosition >= this.bufferLength) {
                ++this.currentBufferIndex;
                this.switchCurrentBuffer(true);
            }
            int bytesToCopy = len < (remainInBuffer = this.bufferLength - this.bufferPosition) ? len : remainInBuffer;
            System.arraycopy(this.currentBuffer, this.bufferPosition, b, offset, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            this.bufferPosition += bytesToCopy;
        }
    }

    private final void switchCurrentBuffer(boolean enforceEOF) throws IOException {
        this.bufferStart = 1024L * (long)this.currentBufferIndex;
        if (this.currentBufferIndex >= this.file.numBuffers()) {
            if (enforceEOF) {
                throw new EOFException("read past EOF: " + this);
            }
            --this.currentBufferIndex;
            this.bufferPosition = 1024;
        } else {
            this.currentBuffer = this.file.getBuffer(this.currentBufferIndex);
            this.bufferPosition = 0;
            long buflen = this.length - this.bufferStart;
            this.bufferLength = buflen > 1024L ? 1024 : (int)buflen;
        }
    }

    public void copyBytes(IndexOutput out, long numBytes) throws IOException {
        long left;
        int toCopy;
        assert (numBytes >= 0L) : "numBytes=" + numBytes;
        for (left = numBytes; left > 0L; left -= (long)toCopy) {
            int bytesInBuffer;
            if (this.bufferPosition == this.bufferLength) {
                ++this.currentBufferIndex;
                this.switchCurrentBuffer(true);
            }
            toCopy = (int)((long)(bytesInBuffer = this.bufferLength - this.bufferPosition) < left ? (long)bytesInBuffer : left);
            out.writeBytes(this.currentBuffer, this.bufferPosition, toCopy);
            this.bufferPosition += toCopy;
        }
        assert (left == 0L) : "Insufficient bytes to copy: numBytes=" + numBytes + " copied=" + (numBytes - left);
    }

    public long getFilePointer() {
        return this.currentBufferIndex < 0 ? 0L : this.bufferStart + (long)this.bufferPosition;
    }

    public void seek(long pos) throws IOException {
        if (this.currentBuffer == null || pos < this.bufferStart || pos >= this.bufferStart + 1024L) {
            this.currentBufferIndex = (int)(pos / 1024L);
            this.switchCurrentBuffer(false);
        }
        this.bufferPosition = (int)(pos % 1024L);
    }
}

