/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.vault.support.DurationParser;

public class VaultMetadataRequest {
    @JsonProperty(value="max_versions")
    private final int maxVersions;
    @JsonProperty(value="cas_required")
    private final boolean casRequired;
    @JsonProperty(value="delete_version_after")
    private final String deleteVersionAfter;

    private VaultMetadataRequest(int maxVersions, boolean casRequired, @Nullable Duration deleteVersionAfter) {
        this.maxVersions = maxVersions;
        this.casRequired = casRequired;
        this.deleteVersionAfter = DurationParser.formatDuration(deleteVersionAfter != null ? deleteVersionAfter : Duration.ZERO);
    }

    public static VaultMetadataRequestBuilder builder() {
        return new VaultMetadataRequestBuilder();
    }

    public int getMaxVersions() {
        return this.maxVersions;
    }

    public boolean isCasRequired() {
        return this.casRequired;
    }

    public String getDeleteVersionAfter() {
        return this.deleteVersionAfter;
    }

    public static class VaultMetadataRequestBuilder {
        private int maxVersions;
        private boolean casRequired;
        @Nullable
        private Duration deleteVersionAfter;

        public VaultMetadataRequestBuilder maxVersions(int maxVersions) {
            this.maxVersions = maxVersions;
            return this;
        }

        public VaultMetadataRequestBuilder casRequired(boolean casRequired) {
            this.casRequired = casRequired;
            return this;
        }

        public VaultMetadataRequestBuilder deleteVersionAfter(Duration deleteVersionAfter) {
            this.deleteVersionAfter = deleteVersionAfter;
            return this;
        }

        public VaultMetadataRequest build() {
            return new VaultMetadataRequest(this.maxVersions, this.casRequired, this.deleteVersionAfter);
        }
    }
}

