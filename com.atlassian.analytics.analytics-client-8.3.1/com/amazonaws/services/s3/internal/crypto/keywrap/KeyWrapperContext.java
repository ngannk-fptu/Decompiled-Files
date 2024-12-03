/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto.keywrap;

import com.amazonaws.services.s3.internal.crypto.ContentCryptoScheme;
import com.amazonaws.services.s3.internal.crypto.keywrap.InternalKeyWrapAlgorithm;
import com.amazonaws.services.s3.internal.crypto.keywrap.KMSKeyWrapperContext;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import java.security.Provider;
import java.security.SecureRandom;

public class KeyWrapperContext {
    private final byte[] cekSecured;
    private final EncryptionMaterials materials;
    private final ContentCryptoScheme contentCryptoScheme;
    private final KMSKeyWrapperContext kmsKeyWrapperContext;
    private final InternalKeyWrapAlgorithm internalKeyWrapAlgorithm;
    private final Provider cryptoProvider;
    private final SecureRandom secureRandom;

    private KeyWrapperContext(Builder b) {
        this.cekSecured = b.cekSecured;
        this.internalKeyWrapAlgorithm = b.internalKeyWrapAlgorithm;
        this.materials = b.materials;
        this.contentCryptoScheme = b.contentCryptoScheme;
        this.kmsKeyWrapperContext = b.kmsKeyWrapperContext;
        this.cryptoProvider = b.cryptoProvider;
        this.secureRandom = b.secureRandom;
    }

    public static Builder builder() {
        return new Builder();
    }

    public byte[] cekSecured() {
        return this.cekSecured;
    }

    public InternalKeyWrapAlgorithm internalKeyWrapAlgorithm() {
        return this.internalKeyWrapAlgorithm;
    }

    public EncryptionMaterials materials() {
        return this.materials;
    }

    public ContentCryptoScheme contentCryptoScheme() {
        return this.contentCryptoScheme;
    }

    public KMSKeyWrapperContext kmsKeyWrapperContext() {
        return this.kmsKeyWrapperContext;
    }

    public Provider cryptoProvider() {
        return this.cryptoProvider;
    }

    public SecureRandom secureRandom() {
        return this.secureRandom;
    }

    public static class Builder {
        private byte[] cekSecured;
        private EncryptionMaterials materials;
        private ContentCryptoScheme contentCryptoScheme;
        private KMSKeyWrapperContext kmsKeyWrapperContext;
        private InternalKeyWrapAlgorithm internalKeyWrapAlgorithm;
        private Provider cryptoProvider;
        private SecureRandom secureRandom;

        public Builder cekSecured(byte[] cekSecured) {
            this.cekSecured = cekSecured;
            return this;
        }

        public Builder internalKeyWrapAlgorithm(InternalKeyWrapAlgorithm keyWrapAlgo) {
            this.internalKeyWrapAlgorithm = keyWrapAlgo;
            return this;
        }

        public Builder materials(EncryptionMaterials materials) {
            this.materials = materials;
            return this;
        }

        public Builder contentCryptoScheme(ContentCryptoScheme contentCryptoScheme) {
            this.contentCryptoScheme = contentCryptoScheme;
            return this;
        }

        public Builder kmsKeyWrapperContext(KMSKeyWrapperContext kmsKeyWrapperContext) {
            this.kmsKeyWrapperContext = kmsKeyWrapperContext;
            return this;
        }

        public Builder cryptoProvider(Provider cryptoProvider) {
            this.cryptoProvider = cryptoProvider;
            return this;
        }

        public Builder secureRandom(SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
            return this;
        }

        public KeyWrapperContext build() {
            return new KeyWrapperContext(this);
        }
    }
}

