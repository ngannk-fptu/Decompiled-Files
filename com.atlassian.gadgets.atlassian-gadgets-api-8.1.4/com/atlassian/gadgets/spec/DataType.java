/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.spec;

public enum DataType {
    STRING,
    HIDDEN,
    BOOL,
    ENUM,
    LIST,
    NUMBER;


    public static DataType parse(String value) {
        for (DataType type : DataType.values()) {
            if (type.toString().compareToIgnoreCase(value) != 0) continue;
            return type;
        }
        return STRING;
    }
}

