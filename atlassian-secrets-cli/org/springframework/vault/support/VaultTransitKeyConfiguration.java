/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

public class VaultTransitKeyConfiguration {
    @JsonProperty(value="deletion_allowed")
    @Nullable
    private final Boolean deletionAllowed;
    @JsonProperty(value="min_decryption_version")
    @Nullable
    private final Integer minDecryptionVersion;
    @JsonProperty(value="min_encryption_version")
    @Nullable
    private final Integer minEncryptionVersion;

    private VaultTransitKeyConfiguration(@Nullable Boolean deletionAllowed, @Nullable Integer minDecryptionVersion, @Nullable Integer minEncryptionVersion) {
        this.deletionAllowed = deletionAllowed;
        this.minDecryptionVersion = minDecryptionVersion;
        this.minEncryptionVersion = minEncryptionVersion;
    }

    public static VaultTransitKeyConfigurationBuilder builder() {
        return new VaultTransitKeyConfigurationBuilder();
    }

    @Nullable
    public Boolean getDeletionAllowed() {
        return this.deletionAllowed;
    }

    @Nullable
    public Integer getMinDecryptionVersion() {
        return this.minDecryptionVersion;
    }

    @Nullable
    public Integer getMinEncryptionVersion() {
        return this.minEncryptionVersion;
    }

    public static class VaultTransitKeyConfigurationBuilder {
        @Nullable
        private Boolean deletionAllowed;
        @Nullable
        private Integer minDecryptionVersion;
        @Nullable
        private Integer minEncryptionVersion;

        VaultTransitKeyConfigurationBuilder() {
        }

        public VaultTransitKeyConfigurationBuilder deletionAllowed(boolean deletionAllowed) {
            this.deletionAllowed = deletionAllowed;
            return this;
        }

        public VaultTransitKeyConfigurationBuilder minDecryptionVersion(int minDecryptionVersion) {
            this.minDecryptionVersion = minDecryptionVersion;
            return this;
        }

        public VaultTransitKeyConfigurationBuilder minEncryptionVersion(int minEncryptionVersion) {
            this.minEncryptionVersion = minEncryptionVersion;
            return this;
        }

        public VaultTransitKeyConfiguration build() {
            return new VaultTransitKeyConfiguration(this.deletionAllowed, this.minDecryptionVersion, this.minEncryptionVersion);
        }
    }
}

