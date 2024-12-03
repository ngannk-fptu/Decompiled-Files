/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.core;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ApplinkStatus {
    @Nonnull
    public ApplicationLink getLink();

    @Nullable
    public ApplinkError getError();

    public boolean isWorking();

    @Nonnull
    public ApplinkOAuthStatus getLocalAuthentication();

    @Nullable
    public ApplinkOAuthStatus getRemoteAuthentication();
}

