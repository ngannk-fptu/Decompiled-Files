/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ByteArrayOutputStream
extends OutputStream {
    static final int DEFAULT_SIZE = 1024;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final List<byte[]> buffers = new ArrayList<byte[]>();
    private int currentBufferIndex;
    private int filledBufferSum;
    private byte[] currentBuffer;
    private int count;

    public ByteArrayOutputStream() {
        this(1024);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        ByteArrayOutputStream byteArrayOutputStream = this;
        synchronized (byteArrayOutputStream) {
            this.needNewBuffer(size);
        }
    }

    private void needNewBuffer(int newcount) {
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            this.filledBufferSum += this.currentBuffer.length;
            ++this.currentBufferIndex;
            this.currentBuffer = this.buffers.get(this.currentBufferIndex);
        } else {
            int newBufferSize;
            if (this.currentBuffer == null) {
                newBufferSize = newcount;
                this.filledBufferSum = 0;
            } else {
                newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
                this.filledBufferSum += this.currentBuffer.length;
            }
            ++this.currentBufferIndex;
            this.currentBuffer = new byte[newBufferSize];
            this.buffers.add(this.currentBuffer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = this;
        synchronized (byteArrayOutputStream) {
            int newcount = this.count + len;
            int remaining = len;
            int inBufferPos = this.count - this.filledBufferSum;
            while (remaining > 0) {
                int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
                System.arraycopy(b, off + len - remaining, this.currentBuffer, inBufferPos, part);
                if ((remaining -= part) <= 0) continue;
                this.needNewBuffer(newcount);
                inBufferPos = 0;
            }
            this.count = newcount;
        }
    }

    @Override
    public synchronized void write(int b) {
        int inBufferPos = this.count - this.filledBufferSum;
        if (inBufferPos == this.currentBuffer.length) {
            this.needNewBuffer(this.count + 1);
            inBufferPos = 0;
        }
        this.currentBuffer[inBufferPos] = (byte)b;
        ++this.count;
    }

    public synchronized int write(InputStream in) throws IOException {
        int readCount = 0;
        int inBufferPos = this.count - this.filledBufferSum;
        int n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos);
        while (n != -1) {
            readCount += n;
            this.count += n;
            if ((inBufferPos += n) == this.currentBuffer.length) {
                this.needNewBuffer(this.currentBuffer.length);
                inBufferPos = 0;
            }
            n = in.read(this.currentBuffer, inBufferPos, this.currentBuffer.length - inBufferPos);
        }
        return readCount;
    }

    @Override
    public void close() throws IOException {
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        int remaining = this.count;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            out.write(buf, 0, c);
            if ((remaining -= c) != 0) continue;
            break;
        }
    }

    public synchronized byte[] toByteArray() {
        int remaining = this.count;
        if (remaining == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] newbuf = new byte[remaining];
        int pos = 0;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, newbuf, pos, c);
            pos += c;
            if ((remaining -= c) != 0) continue;
            break;
        }
        return newbuf;
    }
}

