/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import java.io.Reader;

final class ReusableStringReader
extends Reader {
    int upto;
    int left;
    String s;

    ReusableStringReader() {
    }

    void init(String s) {
        this.s = s;
        this.left = s.length();
        this.upto = 0;
    }

    public int read(char[] c) {
        return this.read(c, 0, c.length);
    }

    public int read(char[] c, int off, int len) {
        if (this.left > len) {
            this.s.getChars(this.upto, this.upto + len, c, off);
            this.upto += len;
            this.left -= len;
            return len;
        }
        if (0 == this.left) {
            this.s = null;
            return -1;
        }
        this.s.getChars(this.upto, this.upto + this.left, c, off);
        int r = this.left;
        this.left = 0;
        this.upto = this.s.length();
        return r;
    }

    public void close() {
    }
}

