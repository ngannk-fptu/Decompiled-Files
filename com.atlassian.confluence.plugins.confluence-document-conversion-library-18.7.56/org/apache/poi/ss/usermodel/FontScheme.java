/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

public enum FontScheme {
    NONE(1),
    MAJOR(2),
    MINOR(3);

    private final int value;
    private static final FontScheme[] _table;

    private FontScheme(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static FontScheme valueOf(int value) {
        return _table[value];
    }

    static {
        _table = new FontScheme[]{null, NONE, MAJOR, MINOR};
    }
}

