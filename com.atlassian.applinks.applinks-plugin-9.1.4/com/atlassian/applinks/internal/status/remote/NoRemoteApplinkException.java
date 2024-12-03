/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NoRemoteApplinkException
extends ApplinkStatusException
implements ApplinkError {
    public NoRemoteApplinkException(@Nullable String message) {
        super(message);
    }

    public NoRemoteApplinkException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return ApplinkErrorType.NO_REMOTE_APPLINK;
    }

    @Override
    @Nullable
    public String getDetails() {
        return null;
    }
}

