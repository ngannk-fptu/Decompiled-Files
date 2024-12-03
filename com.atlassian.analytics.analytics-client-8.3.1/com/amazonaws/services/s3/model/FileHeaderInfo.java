/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum FileHeaderInfo {
    USE("USE"),
    IGNORE("IGNORE"),
    NONE("NONE");

    private final String headerInfo;

    private FileHeaderInfo(String headerInfo) {
        this.headerInfo = headerInfo;
    }

    public String toString() {
        return this.headerInfo;
    }

    public static FileHeaderInfo fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (FileHeaderInfo enumEntry : FileHeaderInfo.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

