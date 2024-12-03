/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.google.common.base.Predicate;
import java.net.SocketException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteStatusUnknownException
extends ApplinkStatusException {
    private static final Package JAVA_NET_PACKAGE = SocketException.class.getPackage();
    private static final Predicate<Throwable> IN_JAVA_NET_PACKAGE = new Predicate<Throwable>(){

        public boolean apply(Throwable input) {
            return JAVA_NET_PACKAGE.equals(input.getClass().getPackage());
        }
    };

    public RemoteStatusUnknownException() {
    }

    public RemoteStatusUnknownException(@Nullable String message) {
        super(message);
    }

    public RemoteStatusUnknownException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return ApplinkErrorType.UNKNOWN;
    }

    @Override
    @Nullable
    public String getDetails() {
        Throwable networkCause = ApplinkErrors.findCauseMatching(this, IN_JAVA_NET_PACKAGE);
        if (networkCause != null) {
            return ApplinkErrors.toDetails(networkCause);
        }
        if (this.getCause() != null) {
            return ApplinkErrors.toDetails(this.getCause());
        }
        return null;
    }
}

