/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SupportZipCreationRequest {
    private final List<SupportZipBundle> bundles;
    private final boolean checkPermissions;
    private final Boolean limitFileSizes;
    private final Integer fileConstraintSize;
    private final Integer fileConstraintLastModified;
    private final String clusterTaskId;

    SupportZipCreationRequest(Builder builder) {
        this.bundles = builder.bundles.build();
        this.checkPermissions = builder.checkPermissions;
        this.limitFileSizes = builder.limitFileSizes;
        this.fileConstraintSize = builder.fileConstraintSize;
        this.fileConstraintLastModified = builder.fileConstraintLastModified;
        this.clusterTaskId = builder.clusterTaskId;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public List<SupportZipBundle> getBundles() {
        return this.bundles;
    }

    public Optional<Integer> getMaxMegaBytesPerFile() {
        if (this.fileConstraintSize != null) {
            return this.fileConstraintSize == -1 ? Optional.empty() : Optional.of(this.fileConstraintSize);
        }
        if (this.limitFileSizes != null) {
            return Boolean.TRUE.equals(this.limitFileSizes) ? Optional.of(100) : Optional.empty();
        }
        return Optional.of(100);
    }

    public Optional<Integer> getMaxBytesPerFile() {
        return this.getMaxMegaBytesPerFile().map(megabytes -> (int)((long)megabytes.intValue() * 0x100000L));
    }

    public Optional<Integer> getFileConstraintLastModified() {
        if (this.fileConstraintLastModified != null && this.fileConstraintLastModified != -1) {
            return Optional.of(this.fileConstraintLastModified);
        }
        return Optional.empty();
    }

    public String getClusterTaskId() {
        return this.clusterTaskId;
    }

    public boolean isCheckPermissions() {
        return this.checkPermissions;
    }

    public int getBundleProgressShare() {
        if (this.bundles.isEmpty()) {
            return 0;
        }
        return 100 / this.bundles.size();
    }

    public static class Builder {
        private final ImmutableList.Builder<SupportZipBundle> bundles = ImmutableList.builder();
        private boolean checkPermissions = true;
        private Boolean limitFileSizes;
        private Integer fileConstraintSize;
        private Integer fileConstraintLastModified;
        private String clusterTaskId;

        @Nonnull
        public Builder skipPermissionCheck() {
            this.checkPermissions = false;
            return this;
        }

        @Nonnull
        public Builder bundles(@Nonnull SupportZipBundle bundle, SupportZipBundle ... moreBundles) {
            this.bundles.add((Object)Objects.requireNonNull(bundle));
            this.bundles.addAll((Iterator)Iterators.forArray((Object[])moreBundles));
            return this;
        }

        @Nonnull
        public Builder bundles(@Nonnull Iterable<SupportZipBundle> values) {
            this.bundles.addAll(Objects.requireNonNull(values));
            return this;
        }

        @Nonnull
        public Builder limitFileSizes(Boolean value) {
            this.limitFileSizes = value;
            return this;
        }

        @Nonnull
        public Builder fileConstraintSize(Integer value) {
            this.fileConstraintSize = value;
            return this;
        }

        @Nonnull
        public Builder fileConstraintLastModified(Integer value) {
            this.fileConstraintLastModified = value;
            return this;
        }

        @Nonnull
        public Builder withClusterTaskId(@Nullable String value) {
            this.clusterTaskId = value;
            return this;
        }

        @Nonnull
        public Builder limitFileSizes() {
            return this.limitFileSizes(true);
        }

        @Nonnull
        public SupportZipCreationRequest build() {
            return new SupportZipCreationRequest(this);
        }
    }
}

