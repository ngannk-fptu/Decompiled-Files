/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TailStream
extends FilterInputStream {
    private static final int SKIP_SIZE = 4096;
    private final byte[] tailBuffer;
    private final int tailSize;
    private byte[] markBuffer;
    private long bytesRead;
    private long markBytesRead;
    private int currentIndex;
    private int markIndex;

    public TailStream(InputStream in, int size) {
        super(in);
        this.tailSize = size;
        this.tailBuffer = new byte[size];
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c != -1) {
            this.appendByte((byte)c);
        }
        return c;
    }

    @Override
    public int read(byte[] buf) throws IOException {
        int read = super.read(buf);
        if (read > 0) {
            this.appendBuf(buf, 0, read);
        }
        return read;
    }

    @Override
    public int read(byte[] buf, int ofs, int length) throws IOException {
        int read = super.read(buf, ofs, length);
        if (read > 0) {
            this.appendBuf(buf, ofs, read);
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        int bufSize = (int)Math.min(n, 4096L);
        byte[] buf = new byte[bufSize];
        long bytesSkipped = 0L;
        int bytesRead = 0;
        while (bytesSkipped < n && bytesRead != -1) {
            int len = (int)Math.min((long)bufSize, n - bytesSkipped);
            bytesRead = this.read(buf, 0, len);
            if (bytesRead == -1) continue;
            bytesSkipped += (long)bytesRead;
        }
        return bytesRead < 0 && bytesSkipped == 0L ? -1L : bytesSkipped;
    }

    @Override
    public void mark(int limit) {
        this.markBuffer = new byte[this.tailSize];
        System.arraycopy(this.tailBuffer, 0, this.markBuffer, 0, this.tailSize);
        this.markIndex = this.currentIndex;
        this.markBytesRead = this.bytesRead;
    }

    @Override
    public void reset() {
        if (this.markBuffer != null) {
            System.arraycopy(this.markBuffer, 0, this.tailBuffer, 0, this.tailSize);
            this.currentIndex = this.markIndex;
            this.bytesRead = this.markBytesRead;
        }
    }

    public byte[] getTail() {
        int size = (int)Math.min((long)this.tailSize, this.bytesRead);
        byte[] result = new byte[size];
        System.arraycopy(this.tailBuffer, this.currentIndex, result, 0, size - this.currentIndex);
        System.arraycopy(this.tailBuffer, 0, result, size - this.currentIndex, this.currentIndex);
        return result;
    }

    private void appendByte(byte b) {
        this.tailBuffer[this.currentIndex++] = b;
        if (this.currentIndex >= this.tailSize) {
            this.currentIndex = 0;
        }
        ++this.bytesRead;
    }

    private void appendBuf(byte[] buf, int ofs, int length) {
        if (length >= this.tailSize) {
            this.replaceTailBuffer(buf, ofs, length);
        } else {
            this.copyToTailBuffer(buf, ofs, length);
        }
        this.bytesRead += (long)length;
    }

    private void replaceTailBuffer(byte[] buf, int ofs, int length) {
        System.arraycopy(buf, ofs + length - this.tailSize, this.tailBuffer, 0, this.tailSize);
        this.currentIndex = 0;
    }

    private void copyToTailBuffer(byte[] buf, int ofs, int length) {
        int remaining = this.tailSize - this.currentIndex;
        int size1 = Math.min(remaining, length);
        System.arraycopy(buf, ofs, this.tailBuffer, this.currentIndex, size1);
        System.arraycopy(buf, ofs + size1, this.tailBuffer, 0, length - size1);
        this.currentIndex = (this.currentIndex + length) % this.tailSize;
    }
}

