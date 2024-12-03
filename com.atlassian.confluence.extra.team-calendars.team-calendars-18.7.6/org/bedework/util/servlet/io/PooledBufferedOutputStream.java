/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.bedework.util.servlet.io.BufferPool;
import org.bedework.util.servlet.io.PooledBuffers;

public class PooledBufferedOutputStream
extends OutputStream {
    protected int count;
    private ArrayList<BufferPool.Buffer> buffers = new ArrayList();
    private BufferPool.Buffer curBuffer;

    @Override
    public synchronized void write(int b) {
        BufferPool.Buffer buff = this.getBuffer(this.count, false);
        if (buff == null) {
            buff = this.newBuffer();
        }
        if (!buff.putByte(b) && !(buff = this.newBuffer()).putByte(b)) {
            throw new RuntimeException("Logic error in write(b)");
        }
        ++this.count;
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        int srcoffset = off;
        int srclen = len;
        while (true) {
            int remaining;
            int moved;
            BufferPool.Buffer buff;
            if ((buff = this.getBuffer(this.count, false)) == null) {
                buff = this.newBuffer();
            }
            if ((moved = srclen - (remaining = buff.putBytes(b, srcoffset, srclen))) == 0) {
                buff = this.newBuffer();
            }
            this.count += moved;
            if (remaining == 0) break;
            srcoffset += moved;
            srclen -= moved;
        }
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        for (BufferPool.Buffer b : this.buffers) {
            out.write(b.buf, 0, b.pos);
        }
    }

    public synchronized InputStream getInputStream() throws IOException {
        return new PooledBuffersInputStream();
    }

    public synchronized byte[] toByteArray() {
        byte[] outBuff = new byte[this.count];
        int pos = 0;
        for (BufferPool.Buffer b : this.buffers) {
            System.arraycopy(b.buf, 0, outBuff, pos, b.pos);
            pos += b.pos;
        }
        return outBuff;
    }

    public synchronized int size() {
        return this.count;
    }

    public synchronized String toString() {
        return new String(this.toByteArray());
    }

    public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), 0, this.count, charsetName);
    }

    @Override
    public void close() throws IOException {
    }

    public void release() throws IOException {
        for (BufferPool.Buffer b : this.buffers) {
            PooledBuffers.release(b);
        }
        this.buffers.clear();
        this.count = 0;
    }

    private BufferPool.Buffer getBuffer(int pos, boolean forRead) {
        if (this.curBuffer != null && this.curBuffer.contains(pos)) {
            return this.curBuffer;
        }
        for (BufferPool.Buffer b : this.buffers) {
            if (forRead) {
                if (!b.contains(pos)) continue;
                this.curBuffer = b;
                return b;
            }
            if (!b.at(pos)) continue;
            this.curBuffer = b;
            return b;
        }
        return null;
    }

    private BufferPool.Buffer newBuffer() {
        int numBuffers = this.buffers.size();
        this.curBuffer = numBuffers == 0 ? PooledBuffers.getSmallBuffer() : (numBuffers == 1 ? PooledBuffers.getMediumBuffer() : PooledBuffers.getLargeBuffer());
        this.buffers.add(this.curBuffer);
        this.curBuffer.startPos = this.count;
        this.curBuffer.pos = 0;
        return this.curBuffer;
    }

    private class PooledBuffersInputStream
    extends InputStream {
        int pos;

        private PooledBuffersInputStream() {
        }

        @Override
        public int read() throws IOException {
            BufferPool.Buffer b = PooledBufferedOutputStream.this.getBuffer(this.pos, true);
            if (b == null) {
                return -1;
            }
            int res = b.getByte(this.pos);
            ++this.pos;
            return res;
        }
    }
}

