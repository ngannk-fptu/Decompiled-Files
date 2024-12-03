/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

public enum FetchMode {
    DEFAULT,
    JOIN,
    SELECT;

    @Deprecated
    public static final FetchMode LAZY;
    @Deprecated
    public static final FetchMode EAGER;

    static {
        LAZY = SELECT;
        EAGER = JOIN;
    }
}

