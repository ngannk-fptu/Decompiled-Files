/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.capabilities;

import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultRemoteCapabilities
implements RemoteApplicationCapabilities {
    private final ApplicationVersion applicationVersion;
    private final ApplicationVersion applinksVersion;
    private final Set<ApplinksCapabilities> capabilities;
    private final ApplinkError error;

    private DefaultRemoteCapabilities(Builder builder) {
        this.applicationVersion = builder.applicationVersion;
        this.applinksVersion = builder.applinksVersion;
        this.capabilities = builder.capabilities;
        this.error = builder.error;
    }

    @Override
    @Nullable
    public ApplicationVersion getApplicationVersion() {
        return this.applicationVersion;
    }

    @Override
    @Nullable
    public ApplicationVersion getApplinksVersion() {
        return this.applinksVersion;
    }

    @Override
    @Nonnull
    public Set<ApplinksCapabilities> getCapabilities() {
        return this.capabilities;
    }

    @Override
    @Nullable
    public ApplinkError getError() {
        return this.error;
    }

    @Override
    public boolean hasError() {
        return this.error != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultRemoteCapabilities that = (DefaultRemoteCapabilities)o;
        return Objects.equals(this.applicationVersion, that.applicationVersion) && Objects.equals(this.applinksVersion, that.applinksVersion) && Objects.equals(this.capabilities, that.capabilities) && Objects.equals(this.error, that.error);
    }

    public int hashCode() {
        return Objects.hash(this.applicationVersion, this.applinksVersion, this.capabilities, this.error);
    }

    public static class Builder {
        private ApplicationVersion applicationVersion;
        private ApplicationVersion applinksVersion;
        private Set<ApplinksCapabilities> capabilities = EnumSet.noneOf(ApplinksCapabilities.class);
        private ApplinkError error;

        public Builder() {
        }

        public Builder(@Nonnull RemoteApplicationCapabilities that) {
            this.applicationVersion = that.getApplicationVersion();
            this.applinksVersion = that.getApplinksVersion();
            this.capabilities = that.getCapabilities().isEmpty() ? EnumSet.noneOf(ApplinksCapabilities.class) : EnumSet.copyOf(that.getCapabilities());
            this.error = that.getError();
        }

        @Nonnull
        public Builder applicationVersion(@Nullable ApplicationVersion applicationVersion) {
            this.applicationVersion = applicationVersion;
            return this;
        }

        @Nonnull
        public Builder applinksVersion(@Nullable ApplicationVersion applinksVersion) {
            this.applinksVersion = applinksVersion;
            return this;
        }

        @Nonnull
        public Builder capabilities(@Nonnull Set<ApplinksCapabilities> capabilities) {
            this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
            return this;
        }

        @Nonnull
        public Builder error(@Nullable ApplinkError error) {
            this.error = error;
            return this;
        }

        @Nonnull
        public DefaultRemoteCapabilities build() {
            return new DefaultRemoteCapabilities(this);
        }
    }
}

