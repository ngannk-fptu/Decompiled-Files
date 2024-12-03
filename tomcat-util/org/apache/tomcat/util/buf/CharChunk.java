/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.io.IOException;
import org.apache.tomcat.util.buf.AbstractChunk;
import org.apache.tomcat.util.buf.Ascii;
import org.apache.tomcat.util.buf.StringCache;

public final class CharChunk
extends AbstractChunk
implements CharSequence {
    private static final long serialVersionUID = 1L;
    private char[] buff;
    private transient CharInputChannel in = null;
    private transient CharOutputChannel out = null;

    public CharChunk() {
    }

    public CharChunk(int initial) {
        this.allocate(initial, -1);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void allocate(int initial, int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new char[initial];
        }
        this.setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public void setChars(char[] c, int off, int len) {
        this.buff = c;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }

    public char[] getChars() {
        return this.getBuffer();
    }

    public char[] getBuffer() {
        return this.buff;
    }

    public void setCharInputChannel(CharInputChannel in) {
        this.in = in;
    }

    public void setCharOutputChannel(CharOutputChannel out) {
        this.out = out;
    }

    public void append(char c) throws IOException {
        this.makeSpace(1);
        int limit = this.getLimitInternal();
        if (this.end >= limit) {
            this.flushBuffer();
        }
        this.buff[this.end++] = c;
    }

    public void append(CharChunk src) throws IOException {
        this.append(src.getBuffer(), src.getOffset(), src.getLength());
    }

    public void append(char[] src, int off, int len) throws IOException {
        this.makeSpace(len);
        int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteChars(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
            return;
        }
        if (len + this.end < 2 * limit) {
            int avail = limit - this.end;
            System.arraycopy(src, off, this.buff, this.end, avail);
            this.end += avail;
            this.flushBuffer();
            System.arraycopy(src, off + avail, this.buff, this.end, len - avail);
            this.end += len - avail;
        } else {
            this.flushBuffer();
            this.out.realWriteChars(src, off, len);
        }
    }

    public void append(String s) throws IOException {
        this.append(s, 0, s.length());
    }

    public void append(String s, int off, int len) throws IOException {
        if (s == null) {
            return;
        }
        this.makeSpace(len);
        int limit = this.getLimitInternal();
        int sOff = off;
        int sEnd = off + len;
        while (sOff < sEnd) {
            int d = this.min(limit - this.end, sEnd - sOff);
            s.getChars(sOff, sOff + d, this.buff, this.end);
            sOff += d;
            this.end += d;
            if (this.end < limit) continue;
            this.flushBuffer();
        }
    }

    @Deprecated
    public int substract() throws IOException {
        return this.subtract();
    }

    public int subtract() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++];
    }

    @Deprecated
    public int substract(char[] dest, int off, int len) throws IOException {
        return this.subtract(dest, off, len);
    }

    public int subtract(char[] dest, int off, int len) throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        int n = len;
        if (len > this.getLength()) {
            n = this.getLength();
        }
        System.arraycopy(this.buff, this.start, dest, off, n);
        this.start += n;
        return n;
    }

    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            int n = this.in.realReadChars();
            if (n < 0) {
                return true;
            }
        }
        return false;
    }

    public void flushBuffer() throws IOException {
        if (this.out == null) {
            throw new IOException(sm.getString("chunk.overflow", this.getLimit(), this.buff.length));
        }
        this.out.realWriteChars(this.buff, this.start, this.end - this.start);
        this.end = this.start;
    }

    public void makeSpace(int count) {
        char[] tmp = null;
        long desiredSize = this.end + count;
        int limit = this.getLimitInternal();
        if (desiredSize > (long)limit) {
            desiredSize = limit;
        }
        if (this.buff == null) {
            if (desiredSize < 256L) {
                desiredSize = 256L;
            }
            this.buff = new char[(int)desiredSize];
        }
        if (desiredSize <= (long)this.buff.length) {
            return;
        }
        long newSize = desiredSize < 2L * (long)this.buff.length ? (long)this.buff.length * 2L : (long)this.buff.length * 2L + (long)count;
        if (newSize > (long)limit) {
            newSize = limit;
        }
        tmp = new char[(int)newSize];
        System.arraycopy(this.buff, 0, tmp, 0, this.end);
        this.buff = tmp;
        tmp = null;
    }

    @Override
    public String toString() {
        if (this.isNull()) {
            return null;
        }
        if (this.end - this.start == 0) {
            return "";
        }
        return StringCache.toString(this);
    }

    public String toStringInternal() {
        return new String(this.buff, this.start, this.end - this.start);
    }

    public boolean equals(Object obj) {
        if (obj instanceof CharChunk) {
            return this.equals((CharChunk)obj);
        }
        return false;
    }

    public boolean equals(String s) {
        char[] c = this.buff;
        int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (c[off++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public boolean equalsIgnoreCase(String s) {
        char[] c = this.buff;
        int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(c[off++]) == Ascii.toLower(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public boolean equals(CharChunk cc) {
        return this.equals(cc.getChars(), cc.getOffset(), cc.getLength());
    }

    public boolean equals(char[] b2, int off2, int len2) {
        char[] b1 = this.buff;
        if (b1 == null && b2 == null) {
            return true;
        }
        int len = this.end - this.start;
        if (len != len2 || b1 == null || b2 == null) {
            return false;
        }
        int off1 = this.start;
        while (len-- > 0) {
            if (b1[off1++] == b2[off2++]) continue;
            return false;
        }
        return true;
    }

    public boolean startsWith(String s) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (c[off++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public boolean startsWithIgnoreCase(String s, int pos) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(c[off++]) == Ascii.toLower(s.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public boolean endsWith(String s) {
        char[] c = this.buff;
        int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.end - len;
        for (int i = 0; i < len; ++i) {
            if (c[off++] == s.charAt(i)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected int getBufferElement(int index) {
        return this.buff[index];
    }

    public int indexOf(char c) {
        return this.indexOf(c, this.start);
    }

    public int indexOf(char c, int starting) {
        int ret = CharChunk.indexOf(this.buff, this.start + starting, this.end, c);
        return ret >= this.start ? ret - this.start : -1;
    }

    public static int indexOf(char[] chars, int start, int end, char s) {
        for (int offset = start; offset < end; ++offset) {
            char c = chars[offset];
            if (c != s) continue;
            return offset;
        }
        return -1;
    }

    private int min(int a, int b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    @Override
    public char charAt(int index) {
        return this.buff[index + this.start];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        try {
            CharChunk result = (CharChunk)this.clone();
            result.setOffset(this.start + start);
            result.setEnd(this.start + end);
            return result;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public int length() {
        return this.end - this.start;
    }

    @Deprecated
    public void setOptimizedWrite(boolean optimizedWrite) {
    }

    public static interface CharInputChannel {
        public int realReadChars() throws IOException;
    }

    public static interface CharOutputChannel {
        public void realWriteChars(char[] var1, int var2, int var3) throws IOException;
    }
}

