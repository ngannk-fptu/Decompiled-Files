/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.remote.RemoteNetworkException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteOAuthException
extends RemoteNetworkException {
    public RemoteOAuthException(@Nonnull ApplinkErrorType applinkErrorType, @Nullable String message) {
        super(applinkErrorType, OAuthMessageProblemException.class, message);
    }

    public RemoteOAuthException(@Nonnull ApplinkErrorType applinkErrorType, @Nullable String message, @Nullable Throwable cause) {
        super(applinkErrorType, OAuthMessageProblemException.class, message, cause);
    }

    @Override
    @Nullable
    protected String toErrorDetails(Throwable underlyingError) {
        return ((OAuthMessageProblemException)((Object)OAuthMessageProblemException.class.cast(underlyingError))).getOAuthProblem();
    }
}

