/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum DataKeySpec {
    AES_256("AES_256"),
    AES_128("AES_128");

    private String value;

    private DataKeySpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static DataKeySpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (DataKeySpec enumEntry : DataKeySpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

