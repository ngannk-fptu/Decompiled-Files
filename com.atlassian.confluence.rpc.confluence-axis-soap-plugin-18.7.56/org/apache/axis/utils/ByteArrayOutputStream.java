/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.utils.Messages;

public class ByteArrayOutputStream
extends OutputStream {
    private List buffers = new ArrayList();
    private int currentBufferIndex;
    private int filledBufferSum;
    private byte[] currentBuffer;
    private int count;

    public ByteArrayOutputStream() {
        this(1024);
    }

    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException01", Integer.toString(size)));
        }
        this.needNewBuffer(size);
    }

    private byte[] getBuffer(int index) {
        return (byte[])this.buffers.get(index);
    }

    private void needNewBuffer(int newcount) {
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            this.filledBufferSum += this.currentBuffer.length;
            ++this.currentBufferIndex;
            this.currentBuffer = this.getBuffer(this.currentBufferIndex);
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

    public synchronized void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException(Messages.getMessage("indexOutOfBoundsException00"));
        }
        if (len == 0) {
            return;
        }
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

    public synchronized void write(int b) {
        this.write(new byte[]{(byte)b}, 0, 1);
    }

    public int size() {
        return this.count;
    }

    public void close() throws IOException {
    }

    public synchronized void reset() {
        this.count = 0;
        this.filledBufferSum = 0;
        this.currentBufferIndex = 0;
        this.currentBuffer = this.getBuffer(this.currentBufferIndex);
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        int remaining = this.count;
        for (int i = 0; i < this.buffers.size(); ++i) {
            byte[] buf = this.getBuffer(i);
            int c = Math.min(buf.length, remaining);
            out.write(buf, 0, c);
            if ((remaining -= c) == 0) break;
        }
    }

    public synchronized byte[] toByteArray() {
        int remaining = this.count;
        int pos = 0;
        byte[] newbuf = new byte[this.count];
        for (int i = 0; i < this.buffers.size(); ++i) {
            byte[] buf = this.getBuffer(i);
            int c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, newbuf, pos, c);
            pos += c;
            if ((remaining -= c) == 0) break;
        }
        return newbuf;
    }

    public String toString() {
        return new String(this.toByteArray());
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), enc);
    }
}

