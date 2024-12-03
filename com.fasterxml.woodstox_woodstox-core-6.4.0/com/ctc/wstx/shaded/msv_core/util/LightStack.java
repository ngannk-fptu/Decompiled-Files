/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util;

public final class LightStack {
    private Object[] buf = new Object[8];
    private int len = 0;

    public void push(Object o) {
        try {
            this.buf[this.len] = o;
            ++this.len;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Object[] nbuf = new Object[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nbuf, 0, this.buf.length);
            this.buf = nbuf;
            this.buf[this.len++] = o;
        }
    }

    public Object pop() {
        return this.buf[--this.len];
    }

    public Object top() {
        return this.buf[this.len - 1];
    }

    public int size() {
        return this.len;
    }

    public boolean contains(Object o) {
        for (int i = 0; i < this.len; ++i) {
            if (this.buf[i] != o) continue;
            return true;
        }
        return false;
    }
}

