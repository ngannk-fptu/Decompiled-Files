/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

public class OpenStringBuilder
implements Appendable,
CharSequence {
    protected char[] buf;
    protected int len;

    public OpenStringBuilder() {
        this(32);
    }

    public OpenStringBuilder(int size) {
        this.buf = new char[size];
    }

    public OpenStringBuilder(char[] arr, int len) {
        this.set(arr, len);
    }

    public void setLength(int len) {
        this.len = len;
    }

    public void set(char[] arr, int end) {
        this.buf = arr;
        this.len = end;
    }

    public char[] getArray() {
        return this.buf;
    }

    public int size() {
        return this.len;
    }

    @Override
    public int length() {
        return this.len;
    }

    public int capacity() {
        return this.buf.length;
    }

    @Override
    public Appendable append(CharSequence csq) {
        return this.append(csq, 0, csq.length());
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) {
        this.reserve(end - start);
        for (int i = start; i < end; ++i) {
            this.unsafeWrite(csq.charAt(i));
        }
        return this;
    }

    @Override
    public Appendable append(char c) {
        this.write(c);
        return this;
    }

    @Override
    public char charAt(int index) {
        return this.buf[index];
    }

    public void setCharAt(int index, char ch) {
        this.buf[index] = ch;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        throw new UnsupportedOperationException();
    }

    public void unsafeWrite(char b) {
        this.buf[this.len++] = b;
    }

    public void unsafeWrite(int b) {
        this.unsafeWrite((char)b);
    }

    public void unsafeWrite(char[] b, int off, int len) {
        System.arraycopy(b, off, this.buf, this.len, len);
        this.len += len;
    }

    protected void resize(int len) {
        char[] newbuf = new char[Math.max(this.buf.length << 1, len)];
        System.arraycopy(this.buf, 0, newbuf, 0, this.size());
        this.buf = newbuf;
    }

    public void reserve(int num) {
        if (this.len + num > this.buf.length) {
            this.resize(this.len + num);
        }
    }

    public void write(char b) {
        if (this.len >= this.buf.length) {
            this.resize(this.len + 1);
        }
        this.unsafeWrite(b);
    }

    public void write(int b) {
        this.write((char)b);
    }

    public final void write(char[] b) {
        this.write(b, 0, b.length);
    }

    public void write(char[] b, int off, int len) {
        this.reserve(len);
        this.unsafeWrite(b, off, len);
    }

    public final void write(OpenStringBuilder arr) {
        this.write(arr.buf, 0, this.len);
    }

    public void write(String s) {
        this.reserve(s.length());
        s.getChars(0, s.length(), this.buf, this.len);
        this.len += s.length();
    }

    public void flush() {
    }

    public final void reset() {
        this.len = 0;
    }

    public char[] toCharArray() {
        char[] newbuf = new char[this.size()];
        System.arraycopy(this.buf, 0, newbuf, 0, this.size());
        return newbuf;
    }

    @Override
    public String toString() {
        return new String(this.buf, 0, this.size());
    }
}

