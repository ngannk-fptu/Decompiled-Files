/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.fpx;

class Property {
    private int type;
    private int offset;

    public Property(int type, int offset) {
        this.type = type;
        this.offset = offset;
    }

    public int getType() {
        return this.type;
    }

    public int getOffset() {
        return this.offset;
    }
}

