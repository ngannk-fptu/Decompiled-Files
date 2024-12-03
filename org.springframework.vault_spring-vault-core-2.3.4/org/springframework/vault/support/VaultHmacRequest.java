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

public class VaultHmacRequest {
    private final Plaintext plaintext;
    @Nullable
    private final String algorithm;
    @Nullable
    private final Integer keyVersion;

    private VaultHmacRequest(Plaintext plaintext, @Nullable String algorithm, @Nullable Integer keyVersion) {
        this.algorithm = algorithm;
        this.plaintext = plaintext;
        this.keyVersion = keyVersion;
    }

    public static VaultHmacRequestBuilder builder() {
        return new VaultHmacRequestBuilder();
    }

    public static VaultHmacRequest create(Plaintext input) {
        return VaultHmacRequest.builder().plaintext(input).build();
    }

    public Plaintext getPlaintext() {
        return this.plaintext;
    }

    @Nullable
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Nullable
    public Integer getKeyVersion() {
        return this.keyVersion;
    }

    public static class VaultHmacRequestBuilder {
        @Nullable
        private Plaintext plaintext;
        @Nullable
        private String algorithm;
        @Nullable
        private Integer keyVersion;

        public VaultHmacRequestBuilder plaintext(Plaintext input) {
            Assert.notNull((Object)input, (String)"Plaintext must not be null");
            this.plaintext = input;
            return this;
        }

        public VaultHmacRequestBuilder algorithm(String algorithm) {
            Assert.hasText((String)algorithm, (String)"Algorithm must not be null or empty");
            this.algorithm = algorithm;
            return this;
        }

        public VaultHmacRequestBuilder keyVersion(int version) {
            this.keyVersion = version;
            return this;
        }

        public VaultHmacRequest build() {
            Assert.notNull((Object)this.plaintext, (String)"Plaintext input must not be null");
            return new VaultHmacRequest(this.plaintext, this.algorithm, this.keyVersion);
        }
    }
}

