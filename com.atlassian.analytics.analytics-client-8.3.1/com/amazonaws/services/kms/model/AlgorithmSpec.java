/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum AlgorithmSpec {
    RSAES_PKCS1_V1_5("RSAES_PKCS1_V1_5"),
    RSAES_OAEP_SHA_1("RSAES_OAEP_SHA_1"),
    RSAES_OAEP_SHA_256("RSAES_OAEP_SHA_256");

    private String value;

    private AlgorithmSpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static AlgorithmSpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (AlgorithmSpec enumEntry : AlgorithmSpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

