/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum EncryptionAlgorithmSpec {
    SYMMETRIC_DEFAULT("SYMMETRIC_DEFAULT"),
    RSAES_OAEP_SHA_1("RSAES_OAEP_SHA_1"),
    RSAES_OAEP_SHA_256("RSAES_OAEP_SHA_256"),
    SM2PKE("SM2PKE");

    private String value;

    private EncryptionAlgorithmSpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static EncryptionAlgorithmSpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (EncryptionAlgorithmSpec enumEntry : EncryptionAlgorithmSpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

