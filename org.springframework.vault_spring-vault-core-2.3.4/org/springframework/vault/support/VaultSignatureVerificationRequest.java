/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.support.Hmac;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.Signature;

public class VaultSignatureVerificationRequest {
    private final Plaintext plaintext;
    @Nullable
    private final Signature signature;
    @Nullable
    private final Hmac hmac;
    @Nullable
    private final String algorithm;

    private VaultSignatureVerificationRequest(Plaintext plaintext, @Nullable Signature signature, @Nullable Hmac hmac, @Nullable String algorithm) {
        this.plaintext = plaintext;
        this.signature = signature;
        this.hmac = hmac;
        this.algorithm = algorithm;
    }

    public static VaultSignatureVerificationRequestBuilder builder() {
        return new VaultSignatureVerificationRequestBuilder();
    }

    public static VaultSignatureVerificationRequest create(Plaintext plaintext, Signature signature) {
        return VaultSignatureVerificationRequest.builder().plaintext(plaintext).signature(signature).build();
    }

    public static VaultSignatureVerificationRequest create(Plaintext plaintext, Hmac hmac) {
        return VaultSignatureVerificationRequest.builder().plaintext(plaintext).hmac(hmac).build();
    }

    public Plaintext getPlaintext() {
        return this.plaintext;
    }

    @Nullable
    public Signature getSignature() {
        return this.signature;
    }

    @Nullable
    public Hmac getHmac() {
        return this.hmac;
    }

    @Nullable
    public String getAlgorithm() {
        return this.algorithm;
    }

    public static class VaultSignatureVerificationRequestBuilder {
        @Nullable
        private Plaintext input;
        @Nullable
        private Signature signature;
        @Nullable
        private Hmac hmac;
        @Nullable
        private String algorithm;

        public VaultSignatureVerificationRequestBuilder plaintext(Plaintext input) {
            Assert.notNull((Object)input, (String)"Plaintext must not be null");
            this.input = input;
            return this;
        }

        public VaultSignatureVerificationRequestBuilder signature(Signature signature) {
            Assert.notNull((Object)signature, (String)"Signature must not be null");
            this.hmac = null;
            this.signature = signature;
            return this;
        }

        public VaultSignatureVerificationRequestBuilder hmac(Hmac hmac) {
            Assert.notNull((Object)hmac, (String)"HMAC must not be null");
            this.signature = null;
            this.hmac = hmac;
            return this;
        }

        public VaultSignatureVerificationRequestBuilder algorithm(String algorithm) {
            Assert.hasText((String)algorithm, (String)"Algorithm must not be null or empty");
            this.algorithm = algorithm;
            return this;
        }

        public VaultSignatureVerificationRequest build() {
            Assert.notNull((Object)this.input, (String)"Plaintext input must not be null");
            Assert.isTrue((this.hmac != null || this.signature != null ? 1 : 0) != 0, (String)"Either Signature or Hmac must not be null");
            return new VaultSignatureVerificationRequest(this.input, this.signature, this.hmac, this.algorithm);
        }
    }
}

