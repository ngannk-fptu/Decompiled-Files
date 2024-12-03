/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.model.CryptoKeyWrapAlgorithm;
import com.amazonaws.services.s3.model.EncryptionMaterials;

public class KeyWrapAlgorithmResolver {
    public static CryptoKeyWrapAlgorithm getDefaultKeyWrapAlgorithm(EncryptionMaterials encryptionMaterials) {
        if (encryptionMaterials.isKMSEnabled()) {
            return CryptoKeyWrapAlgorithm.KMS;
        }
        if (encryptionMaterials.getKeyPair() != null) {
            return KeyWrapAlgorithmResolver.getDefaultAsymmetricKeyWrapAlgorithm();
        }
        return KeyWrapAlgorithmResolver.getDefaultSymmetricKeyWrapAlgorithm();
    }

    private static CryptoKeyWrapAlgorithm getDefaultSymmetricKeyWrapAlgorithm() {
        return CryptoKeyWrapAlgorithm.AES_GCM_NoPadding;
    }

    private static CryptoKeyWrapAlgorithm getDefaultAsymmetricKeyWrapAlgorithm() {
        return CryptoKeyWrapAlgorithm.RSA_OAEP_SHA1;
    }
}

