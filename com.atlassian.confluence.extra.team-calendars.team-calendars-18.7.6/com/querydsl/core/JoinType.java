/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

public enum JoinType {
    DEFAULT(false, false),
    INNERJOIN(true, false),
    JOIN(true, false),
    LEFTJOIN(false, true),
    RIGHTJOIN(false, true),
    FULLJOIN(false, true);

    private final boolean inner;
    private final boolean outer;

    private JoinType(boolean inner, boolean outer) {
        this.inner = inner;
        this.outer = outer;
    }

    public boolean isInner() {
        return this.inner;
    }

    public boolean isOuter() {
        return this.outer;
    }
}

