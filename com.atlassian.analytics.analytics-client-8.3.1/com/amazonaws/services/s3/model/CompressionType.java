/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum CompressionType {
    NONE("NONE"),
    GZIP("GZIP"),
    BZIP2("BZIP2");

    private final String compressionType;

    private CompressionType(String compressionType) {
        this.compressionType = compressionType;
    }

    public String toString() {
        return this.compressionType;
    }

    public static CompressionType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (CompressionType enumEntry : CompressionType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

