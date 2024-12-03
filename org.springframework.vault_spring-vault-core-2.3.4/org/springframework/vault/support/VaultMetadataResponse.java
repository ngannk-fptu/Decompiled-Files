/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.vault.support;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.vault.support.Versioned;

public class VaultMetadataResponse {
    private final boolean casRequired;
    private final Instant createdTime;
    private final int currentVersion;
    private final Duration deleteVersionAfter;
    private final int maxVersions;
    private final int oldestVersion;
    private final Instant updatedTime;
    private final List<Versioned.Metadata> versions;

    private VaultMetadataResponse(boolean casRequired, Instant createdTime, int currentVersion, Duration deleteVersionAfter, int maxVersions, int oldestVersion, Instant updatedTime, List<Versioned.Metadata> versions) {
        this.casRequired = casRequired;
        this.createdTime = createdTime;
        this.currentVersion = currentVersion;
        this.deleteVersionAfter = deleteVersionAfter;
        this.maxVersions = maxVersions;
        this.oldestVersion = oldestVersion;
        this.updatedTime = updatedTime;
        this.versions = versions;
    }

    public static VaultMetadataResponseBuilder builder() {
        return new VaultMetadataResponseBuilder();
    }

    public boolean isCasRequired() {
        return this.casRequired;
    }

    public Instant getCreatedTime() {
        return this.createdTime;
    }

    public int getCurrentVersion() {
        return this.currentVersion;
    }

    @Nullable
    public Duration getDeleteVersionAfter() {
        return this.deleteVersionAfter;
    }

    public int getMaxVersions() {
        return this.maxVersions;
    }

    public int getOldestVersion() {
        return this.oldestVersion;
    }

    public Instant getUpdatedTime() {
        return this.updatedTime;
    }

    public List<Versioned.Metadata> getVersions() {
        return this.versions;
    }

    public static class VaultMetadataResponseBuilder {
        private boolean casRequired;
        private Instant createdTime;
        private int currentVersion;
        private Duration deleteVersionAfter;
        private int maxVersions;
        private int oldestVersion;
        private Instant updatedTime;
        private List<Versioned.Metadata> versions;

        public VaultMetadataResponseBuilder casRequired(boolean casRequired) {
            this.casRequired = casRequired;
            return this;
        }

        public VaultMetadataResponseBuilder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public VaultMetadataResponseBuilder currentVersion(int currentVersion) {
            this.currentVersion = currentVersion;
            return this;
        }

        public VaultMetadataResponseBuilder deleteVersionAfter(Duration deleteVersionAfter) {
            this.deleteVersionAfter = deleteVersionAfter;
            return this;
        }

        public VaultMetadataResponseBuilder maxVersions(int maxVersions) {
            this.maxVersions = maxVersions;
            return this;
        }

        public VaultMetadataResponseBuilder oldestVersion(int oldestVersion) {
            this.oldestVersion = oldestVersion;
            return this;
        }

        public VaultMetadataResponseBuilder updatedTime(Instant updatedTime) {
            this.updatedTime = updatedTime;
            return this;
        }

        public VaultMetadataResponseBuilder versions(List<Versioned.Metadata> versions) {
            this.versions = versions;
            return this;
        }

        public VaultMetadataResponse build() {
            return new VaultMetadataResponse(this.casRequired, this.createdTime, this.currentVersion, this.deleteVersionAfter, this.maxVersions, this.oldestVersion, this.updatedTime, this.versions);
        }
    }
}

