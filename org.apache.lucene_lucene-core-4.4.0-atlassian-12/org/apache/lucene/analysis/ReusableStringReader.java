/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.Reader;

final class ReusableStringReader
extends Reader {
    private int pos = 0;
    private int size = 0;
    private String s = null;

    ReusableStringReader() {
    }

    void setValue(String s) {
        this.s = s;
        this.size = s.length();
        this.pos = 0;
    }

    @Override
    public int read() {
        if (this.pos < this.size) {
            return this.s.charAt(this.pos++);
        }
        this.s = null;
        return -1;
    }

    @Override
    public int read(char[] c, int off, int len) {
        if (this.pos < this.size) {
            len = Math.min(len, this.size - this.pos);
            this.s.getChars(this.pos, this.pos + len, c, off);
            this.pos += len;
            return len;
        }
        this.s = null;
        return -1;
    }

    @Override
    public void close() {
        this.pos = this.size;
        this.s = null;
    }
}

