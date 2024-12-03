/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum KeyManagerType {
    AWS("AWS"),
    CUSTOMER("CUSTOMER");

    private String value;

    private KeyManagerType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static KeyManagerType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (KeyManagerType enumEntry : KeyManagerType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

