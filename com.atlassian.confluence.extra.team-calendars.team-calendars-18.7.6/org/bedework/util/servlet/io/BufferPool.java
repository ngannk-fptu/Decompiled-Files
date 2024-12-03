/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class BufferPool {
    private int bufferSize;
    private int poolMaxSize;
    private Deque<Buffer> pool = new ArrayDeque<Buffer>();
    private long gets;
    private long puts;
    private long discards;
    private long creates;

    BufferPool(int bufferSize, int poolMaxSize) {
        this.bufferSize = bufferSize;
        this.poolMaxSize = poolMaxSize;
    }

    synchronized Buffer get() {
        Buffer buff = null;
        ++this.gets;
        if (this.pool.size() > 0) {
            buff = this.pool.remove();
        }
        if (buff == null || buff.buf.length != this.bufferSize) {
            ++this.creates;
            buff = new Buffer();
            buff.buf = new byte[this.bufferSize];
        }
        buff.pos = 0;
        buff.startPos = -1;
        return buff;
    }

    synchronized void put(Buffer buff) {
        ++this.puts;
        if (this.pool.size() < this.poolMaxSize && buff.buf.length == this.bufferSize) {
            this.pool.add(buff);
        } else {
            ++this.discards;
        }
    }

    int getBufferSize() {
        return this.bufferSize;
    }

    void setBufferSize(int val) {
        this.bufferSize = val;
    }

    int getPoolMaxSize() {
        return this.poolMaxSize;
    }

    void setPoolMaxSize(int val) {
        this.poolMaxSize = val;
    }

    String getStats() {
        StringBuffer sb = new StringBuffer();
        this.statline(sb, "bufferSize", this.bufferSize);
        this.statline(sb, "poolMaxSize", this.poolMaxSize);
        this.statline(sb, "poolCurSize", this.pool.size());
        this.statline(sb, "gets", this.gets);
        this.statline(sb, "puts", this.puts);
        this.statline(sb, "discards", this.discards);
        this.statline(sb, "creates", this.creates);
        return sb.toString();
    }

    private void statline(StringBuffer sb, String name, long val) {
        sb.append(name);
        sb.append(": ");
        sb.append(val);
        sb.append("\n");
    }

    static class Buffer {
        int startPos;
        int pos;
        byte[] buf;

        Buffer() {
        }

        int getRemainingCount() {
            return this.buf.length - this.pos;
        }

        boolean contains(int i) {
            if (i < this.startPos) {
                return false;
            }
            return i < this.startPos + this.pos;
        }

        boolean at(int i) {
            if (i < this.startPos) {
                return false;
            }
            if (i == this.startPos + this.pos) {
                return !this.full();
            }
            return false;
        }

        boolean full() {
            return this.pos == this.buf.length;
        }

        int getByte(int at) throws IOException {
            int i = at - this.startPos;
            if (i < 0 || i >= this.pos) {
                throw new IOException("Bad offsets at: " + at + " startPos: " + this.startPos + " pos: " + this.pos);
            }
            return this.buf[i];
        }

        boolean putByte(int b) {
            if (this.getRemainingCount() == 0) {
                return false;
            }
            this.buf[this.pos] = (byte)b;
            ++this.pos;
            return true;
        }

        int putBytes(byte[] bytes, int offset, int len) throws IOException {
            int toCopy = len;
            if (this.getRemainingCount() < toCopy) {
                toCopy = this.getRemainingCount();
            }
            if (toCopy > 0) {
                try {
                    System.arraycopy(bytes, offset, this.buf, this.pos, toCopy);
                }
                catch (Throwable t) {
                    throw new IOException("Logic error in putBytes toCopy: " + toCopy + " startPos: " + this.startPos + " pos: " + this.pos);
                }
            }
            this.pos += toCopy;
            return len - toCopy;
        }
    }
}

