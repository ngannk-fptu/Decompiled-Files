/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.data;

public enum Dir {
    LTR(1),
    RTL(-1),
    NEUTRAL(0);

    public final int ord;

    private Dir(int ord) {
        this.ord = ord;
    }

    public boolean isOppositeTo(Dir dir) {
        return this.ord * dir.ord < 0;
    }
}

