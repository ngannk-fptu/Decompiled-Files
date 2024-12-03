/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum WrappingKeySpec {
    RSA_2048("RSA_2048");

    private String value;

    private WrappingKeySpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static WrappingKeySpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (WrappingKeySpec enumEntry : WrappingKeySpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

