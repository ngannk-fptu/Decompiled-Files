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

public class NoOutgoingAuthenticationException
extends ApplinkStatusException
implements ApplinkError {
    public NoOutgoingAuthenticationException(@Nullable String message) {
        super(message);
    }

    public NoOutgoingAuthenticationException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return ApplinkErrorType.NO_OUTGOING_AUTH;
    }

    @Override
    @Nullable
    public String getDetails() {
        return null;
    }
}

