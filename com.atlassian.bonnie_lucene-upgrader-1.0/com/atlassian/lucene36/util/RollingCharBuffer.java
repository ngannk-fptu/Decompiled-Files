/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;
import java.io.Reader;

public final class RollingCharBuffer {
    private Reader reader;
    private char[] buffer = new char[32];
    private int nextWrite;
    private int nextPos;
    private int count;
    private boolean end;

    public void reset(Reader reader) {
        this.reader = reader;
        this.nextPos = 0;
        this.nextWrite = 0;
        this.count = 0;
        this.end = false;
    }

    public int get(int pos) throws IOException {
        if (pos == this.nextPos) {
            if (this.end) {
                return -1;
            }
            int ch = this.reader.read();
            if (ch == -1) {
                this.end = true;
                return -1;
            }
            if (this.count == this.buffer.length) {
                char[] newBuffer = new char[ArrayUtil.oversize(1 + this.count, 2)];
                System.arraycopy(this.buffer, this.nextWrite, newBuffer, 0, this.buffer.length - this.nextWrite);
                System.arraycopy(this.buffer, 0, newBuffer, this.buffer.length - this.nextWrite, this.nextWrite);
                this.nextWrite = this.buffer.length;
                this.buffer = newBuffer;
            }
            if (this.nextWrite == this.buffer.length) {
                this.nextWrite = 0;
            }
            this.buffer[this.nextWrite++] = (char)ch;
            ++this.count;
            ++this.nextPos;
            return ch;
        }
        assert (pos < this.nextPos);
        assert (this.nextPos - pos <= this.count) : "nextPos=" + this.nextPos + " pos=" + pos + " count=" + this.count;
        int index = this.getIndex(pos);
        return this.buffer[index];
    }

    private boolean inBounds(int pos) {
        return pos >= 0 && pos < this.nextPos && pos >= this.nextPos - this.count;
    }

    private int getIndex(int pos) {
        int index = this.nextWrite - (this.nextPos - pos);
        if (index < 0) assert ((index += this.buffer.length) >= 0);
        return index;
    }

    public char[] get(int posStart, int length) {
        assert (length > 0);
        assert (this.inBounds(posStart)) : "posStart=" + posStart + " length=" + length;
        int startIndex = this.getIndex(posStart);
        int endIndex = this.getIndex(posStart + length);
        char[] result = new char[length];
        if (endIndex >= startIndex && length < this.buffer.length) {
            System.arraycopy(this.buffer, startIndex, result, 0, endIndex - startIndex);
        } else {
            int part1 = this.buffer.length - startIndex;
            System.arraycopy(this.buffer, startIndex, result, 0, part1);
            System.arraycopy(this.buffer, 0, result, this.buffer.length - startIndex, length - part1);
        }
        return result;
    }

    public void freeBefore(int pos) {
        assert (pos >= 0);
        assert (pos <= this.nextPos);
        int newCount = this.nextPos - pos;
        assert (newCount <= this.count) : "newCount=" + newCount + " count=" + this.count;
        assert (newCount <= this.buffer.length) : "newCount=" + newCount + " buf.length=" + this.buffer.length;
        this.count = newCount;
    }
}

