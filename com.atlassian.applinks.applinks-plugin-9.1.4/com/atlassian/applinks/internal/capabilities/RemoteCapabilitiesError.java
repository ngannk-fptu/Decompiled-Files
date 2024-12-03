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

public class RemoteCapabilitiesError
implements RemoteApplicationCapabilities {
    private final ApplinkError error;

    public RemoteCapabilitiesError(@Nonnull ApplinkError error) {
        this.error = Objects.requireNonNull(error, "error");
    }

    @Override
    @Nullable
    public ApplicationVersion getApplicationVersion() {
        return null;
    }

    @Override
    @Nullable
    public ApplicationVersion getApplinksVersion() {
        return null;
    }

    @Override
    @Nonnull
    public Set<ApplinksCapabilities> getCapabilities() {
        return EnumSet.noneOf(ApplinksCapabilities.class);
    }

    @Override
    @Nonnull
    public ApplinkError getError() {
        return this.error;
    }

    @Override
    public boolean hasError() {
        return true;
    }
}

