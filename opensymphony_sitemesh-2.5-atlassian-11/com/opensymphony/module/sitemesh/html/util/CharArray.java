/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.util;

import java.io.PrintWriter;

public class CharArray {
    int size = 0;
    char[] buffer;
    int subStrStart = 0;
    int subStrLen = 0;

    public CharArray(int size) {
        this.buffer = new char[size];
    }

    public String toString() {
        return new String(this.buffer, 0, this.size);
    }

    public char charAt(int pos) {
        return this.buffer[pos];
    }

    public void setLength(int newSize) {
        if (newSize < 0) {
            newSize = 0;
        }
        if (newSize <= this.size) {
            this.size = newSize;
        } else {
            if (newSize >= this.buffer.length) {
                this.grow(newSize);
            }
            while (this.size < newSize) {
                this.buffer[this.size] = '\u0000';
                ++this.size;
            }
        }
    }

    public int length() {
        return this.size;
    }

    public CharArray append(CharArray chars) {
        return this.append(chars.buffer, 0, chars.size);
    }

    public CharArray append(char[] chars) {
        return this.append(chars, 0, chars.length);
    }

    public CharArray append(char[] chars, int position, int length) {
        int requiredSize = length + this.size;
        if (requiredSize >= this.buffer.length) {
            this.grow(requiredSize);
        }
        System.arraycopy(chars, position, this.buffer, this.size, length);
        this.size = requiredSize;
        return this;
    }

    public CharArray append(char c) {
        if (this.buffer.length == this.size) {
            this.grow(0);
        }
        this.buffer[this.size++] = c;
        return this;
    }

    public CharArray append(String str) {
        int requiredSize = str.length() + this.size;
        if (requiredSize >= this.buffer.length) {
            this.grow(requiredSize);
        }
        for (int i = 0; i < str.length(); ++i) {
            this.buffer[this.size + i] = str.charAt(i);
        }
        this.size = requiredSize;
        return this;
    }

    public String substring(int begin, int end) {
        return new String(this.buffer, begin, end - begin);
    }

    public void setSubstr(int begin, int end) {
        this.subStrStart = begin;
        this.subStrLen = end - begin;
    }

    public String getLowerSubstr() {
        int i = this.subStrStart;
        while (i < this.subStrStart + this.subStrLen) {
            int n = i++;
            this.buffer[n] = (char)(this.buffer[n] | 0x20);
        }
        return new String(this.buffer, this.subStrStart, this.subStrLen);
    }

    public boolean compareLowerSubstr(String lowerStr) {
        if (lowerStr.length() != this.subStrLen || this.subStrLen <= 0) {
            return false;
        }
        for (int i = 0; i < lowerStr.length(); ++i) {
            if ((this.buffer[this.subStrStart + i] | 0x20) == lowerStr.charAt(i)) continue;
            return false;
        }
        return true;
    }

    public int substrHashCode() {
        int hash = 0;
        int offset = this.subStrStart;
        for (int i = 0; i < this.subStrLen; ++i) {
            hash = 31 * hash + (this.buffer[offset++] | 0x20);
        }
        return hash;
    }

    public boolean compareLower(String lowerStr, int offset) {
        if (offset < 0 || offset + lowerStr.length() > this.size) {
            return false;
        }
        for (int i = 0; i < lowerStr.length(); ++i) {
            if ((this.buffer[offset + i] | 0x20) == lowerStr.charAt(i)) continue;
            return false;
        }
        return true;
    }

    private final void grow(int minSize) {
        int newCapacity = (this.buffer.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minSize > newCapacity) {
            newCapacity = minSize;
        }
        char[] newBuffer = new char[newCapacity];
        System.arraycopy(this.buffer, 0, newBuffer, 0, this.size);
        this.buffer = newBuffer;
    }

    public final void clear() {
        this.size = 0;
    }

    public void writeTo(PrintWriter writer) {
        writer.write(this.buffer, 0, this.size);
    }
}

