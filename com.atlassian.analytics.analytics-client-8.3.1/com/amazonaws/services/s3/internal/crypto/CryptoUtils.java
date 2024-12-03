/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

public final class CryptoUtils {
    private static final String AES_GCM_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String AES_CTR_CIPHER_ALGORITHM = "AES/CTR/NoPadding";

    private CryptoUtils() {
    }

    public static String normalizeContentAlgorithmForValidation(String contentAlgorithm) {
        if (AES_CTR_CIPHER_ALGORITHM.equals(contentAlgorithm)) {
            return AES_GCM_CIPHER_ALGORITHM;
        }
        return contentAlgorithm;
    }
}

