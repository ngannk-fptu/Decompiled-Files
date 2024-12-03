/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum SigningAlgorithmSpec {
    RSASSA_PSS_SHA_256("RSASSA_PSS_SHA_256"),
    RSASSA_PSS_SHA_384("RSASSA_PSS_SHA_384"),
    RSASSA_PSS_SHA_512("RSASSA_PSS_SHA_512"),
    RSASSA_PKCS1_V1_5_SHA_256("RSASSA_PKCS1_V1_5_SHA_256"),
    RSASSA_PKCS1_V1_5_SHA_384("RSASSA_PKCS1_V1_5_SHA_384"),
    RSASSA_PKCS1_V1_5_SHA_512("RSASSA_PKCS1_V1_5_SHA_512"),
    ECDSA_SHA_256("ECDSA_SHA_256"),
    ECDSA_SHA_384("ECDSA_SHA_384"),
    ECDSA_SHA_512("ECDSA_SHA_512"),
    SM2DSA("SM2DSA");

    private String value;

    private SigningAlgorithmSpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static SigningAlgorithmSpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (SigningAlgorithmSpec enumEntry : SigningAlgorithmSpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

