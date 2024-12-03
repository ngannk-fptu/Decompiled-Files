/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum SSEAlgorithm {
    AES256("AES256"),
    KMS("aws:kms");

    private final String algorithm;

    public String getAlgorithm() {
        return this.algorithm;
    }

    private SSEAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String toString() {
        return this.algorithm;
    }

    public static SSEAlgorithm fromString(String algorithm) {
        if (algorithm == null) {
            return null;
        }
        for (SSEAlgorithm e : SSEAlgorithm.values()) {
            if (!e.getAlgorithm().equals(algorithm)) continue;
            return e;
        }
        throw new IllegalArgumentException("Unsupported algorithm " + algorithm);
    }

    public static SSEAlgorithm getDefault() {
        return AES256;
    }
}

