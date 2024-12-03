/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum MacAlgorithmSpec {
    HMAC_SHA_224("HMAC_SHA_224"),
    HMAC_SHA_256("HMAC_SHA_256"),
    HMAC_SHA_384("HMAC_SHA_384"),
    HMAC_SHA_512("HMAC_SHA_512");

    private String value;

    private MacAlgorithmSpec(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static MacAlgorithmSpec fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (MacAlgorithmSpec enumEntry : MacAlgorithmSpec.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

