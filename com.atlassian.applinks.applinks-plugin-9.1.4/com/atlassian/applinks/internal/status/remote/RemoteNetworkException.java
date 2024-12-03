/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrors;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RemoteNetworkException
extends ApplinkStatusException {
    private final ApplinkErrorType applinkErrorType;
    private final Class<? extends Throwable> underlyingErrorType;

    public RemoteNetworkException(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull Class<? extends Throwable> underlyingErrorType, @Nullable String message) {
        super(message);
        this.applinkErrorType = Objects.requireNonNull(applinkErrorType, "applinkErrorType");
        this.underlyingErrorType = Objects.requireNonNull(underlyingErrorType, "underlyingErrorType");
    }

    public RemoteNetworkException(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull Class<? extends Throwable> underlyingErrorType, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.applinkErrorType = Objects.requireNonNull(applinkErrorType, "applinkErrorType");
        this.underlyingErrorType = Objects.requireNonNull(underlyingErrorType, "underlyingErrorType");
    }

    @Override
    @Nonnull
    public final ApplinkErrorType getType() {
        return this.applinkErrorType;
    }

    @Override
    @Nullable
    public final String getDetails() {
        Throwable underlyingError = ApplinkErrors.findCauseOfType(this, this.underlyingErrorType);
        if (underlyingError != null) {
            return this.toErrorDetails(underlyingError);
        }
        return null;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + this.internalToString();
    }

    @Nullable
    protected String toErrorDetails(Throwable underlyingError) {
        return ApplinkErrors.toDetails(underlyingError);
    }

    private String internalToString() {
        StringBuilder sb = new StringBuilder(250);
        return sb.append("RemoteNetworkException{").append("errorType=").append((Object)this.applinkErrorType).append(", ").append("details=").append(this.getDetails()).append("}").toString();
    }
}

