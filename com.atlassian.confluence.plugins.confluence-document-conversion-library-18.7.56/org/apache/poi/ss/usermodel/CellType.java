/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

public enum CellType {
    _NONE(-1),
    NUMERIC(0),
    STRING(1),
    FORMULA(2),
    BLANK(3),
    BOOLEAN(4),
    ERROR(5);

    @Deprecated
    private final int code;

    private CellType(int code) {
        this.code = code;
    }

    @Deprecated
    public static CellType forInt(int code) {
        for (CellType type : CellType.values()) {
            if (type.code != code) continue;
            return type;
        }
        throw new IllegalArgumentException("Invalid CellType code: " + code);
    }

    @Deprecated
    public int getCode() {
        return this.code;
    }
}

