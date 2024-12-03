/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.wp.usermodel;

public enum HeaderFooterType {
    DEFAULT(2),
    EVEN(1),
    FIRST(3);

    private final int code;

    private HeaderFooterType(int i) {
        this.code = i;
    }

    public int toInt() {
        return this.code;
    }

    public static HeaderFooterType forInt(int i) {
        for (HeaderFooterType type : HeaderFooterType.values()) {
            if (type.code != i) continue;
            return type;
        }
        throw new IllegalArgumentException("Invalid HeaderFooterType code: " + i);
    }
}

