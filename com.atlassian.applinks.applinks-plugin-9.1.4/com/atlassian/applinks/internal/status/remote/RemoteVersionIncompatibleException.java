/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteVersionIncompatibleException
extends ApplinkStatusException {
    public RemoteVersionIncompatibleException() {
    }

    public RemoteVersionIncompatibleException(@Nullable String message) {
        super(message);
    }

    public RemoteVersionIncompatibleException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return ApplinkErrorType.REMOTE_VERSION_INCOMPATIBLE;
    }

    @Override
    @Nullable
    public String getDetails() {
        return null;
    }
}

