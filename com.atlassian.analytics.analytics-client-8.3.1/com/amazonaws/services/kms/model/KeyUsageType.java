/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum KeyUsageType {
    SIGN_VERIFY("SIGN_VERIFY"),
    ENCRYPT_DECRYPT("ENCRYPT_DECRYPT"),
    GENERATE_VERIFY_MAC("GENERATE_VERIFY_MAC");

    private String value;

    private KeyUsageType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static KeyUsageType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (KeyUsageType enumEntry : KeyUsageType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

