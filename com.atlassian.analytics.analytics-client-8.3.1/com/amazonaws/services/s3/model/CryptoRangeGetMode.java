/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.CryptoMode;

public enum CryptoRangeGetMode {
    DISABLED(new Predicate(){

        @Override
        public boolean isPermitted(CryptoMode cryptoMode, String algorithm) {
            return false;
        }
    }),
    ALL(new Predicate(){

        @Override
        public boolean isPermitted(CryptoMode cryptoMode, String algorithm) {
            switch (cryptoMode) {
                case AuthenticatedEncryption: {
                    return CryptoRangeGetMode.AES_CTR.equals(algorithm) || CryptoRangeGetMode.AES_CBC_PKCS5.equals(algorithm) || CryptoRangeGetMode.AES_CBC_PKCS7.equals(algorithm);
                }
                case StrictAuthenticatedEncryption: {
                    return CryptoRangeGetMode.AES_CTR.equals(algorithm);
                }
            }
            return false;
        }
    });

    private static final String AES_CTR = "AES/CTR/NoPadding";
    private static final String AES_CBC_PKCS7 = "AES/CBC/PKCS7Padding";
    private static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    private final Predicate predicate;

    public boolean permitsCipherAlgorithm(CryptoMode cryptoMode, String algorithm) {
        return this.predicate.isPermitted(cryptoMode, algorithm);
    }

    private CryptoRangeGetMode(Predicate predicate) {
        this.predicate = predicate;
    }

    private static interface Predicate {
        public boolean isPermitted(CryptoMode var1, String var2);
    }
}

