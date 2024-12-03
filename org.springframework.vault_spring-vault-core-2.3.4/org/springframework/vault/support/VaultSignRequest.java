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
import org.springframework.vault.support.Plaintext;

public class VaultSignRequest {
    private final Plaintext plaintext;
    @Nullable
    private final String algorithm;

    private VaultSignRequest(Plaintext plaintext, @Nullable String algorithm) {
        this.plaintext = plaintext;
        this.algorithm = algorithm;
    }

    public static VaultSignRequestBuilder builder() {
        return new VaultSignRequestBuilder();
    }

    public static VaultSignRequest create(Plaintext input) {
        return VaultSignRequest.builder().plaintext(input).build();
    }

    public Plaintext getPlaintext() {
        return this.plaintext;
    }

    @Nullable
    public String getAlgorithm() {
        return this.algorithm;
    }

    public static class VaultSignRequestBuilder {
        @Nullable
        private Plaintext plaintext;
        @Nullable
        private String algorithm;

        public VaultSignRequestBuilder plaintext(Plaintext input) {
            Assert.notNull((Object)input, (String)"Plaintext must not be null");
            this.plaintext = input;
            return this;
        }

        public VaultSignRequestBuilder algorithm(String algorithm) {
            Assert.hasText((String)algorithm, (String)"Algorithm must not be null or empty");
            this.algorithm = algorithm;
            return this;
        }

        public VaultSignRequest build() {
            Assert.notNull((Object)this.plaintext, (String)"Plaintext input must not be null");
            return new VaultSignRequest(this.plaintext, this.algorithm);
        }
    }
}

