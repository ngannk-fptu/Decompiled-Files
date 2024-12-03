/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.capabilities;

import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RemoteApplicationCapabilities {
    @Nullable
    public ApplicationVersion getApplicationVersion();

    @Nullable
    public ApplicationVersion getApplinksVersion();

    @Nonnull
    public Set<ApplinksCapabilities> getCapabilities();

    @Nullable
    public ApplinkError getError();

    public boolean hasError();
}

