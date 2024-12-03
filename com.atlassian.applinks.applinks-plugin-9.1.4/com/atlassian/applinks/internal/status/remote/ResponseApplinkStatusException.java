/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResponseApplinkStatusException
extends ApplinkStatusException
implements ResponseApplinkError {
    private final ResponseApplinkError responseError;

    public ResponseApplinkStatusException(@Nonnull ResponseApplinkError responseError) {
        super(responseError.getDetails());
        this.responseError = Objects.requireNonNull(responseError, "responseError");
    }

    public ResponseApplinkStatusException(@Nonnull ResponseApplinkError responseError, @Nullable String message) {
        super(message);
        this.responseError = Objects.requireNonNull(responseError, "responseError");
    }

    public ResponseApplinkStatusException(@Nonnull ResponseApplinkError responseError, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.responseError = Objects.requireNonNull(responseError, "responseError");
    }

    public ResponseApplinkStatusException(@Nonnull ResponseApplinkError responseError, @Nullable Throwable cause) {
        this(responseError, responseError.getDetails(), cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return this.responseError.getType();
    }

    @Override
    @Nullable
    public String getDetails() {
        return this.responseError.getDetails();
    }

    @Override
    public int getStatusCode() {
        return this.responseError.getStatusCode();
    }

    @Override
    @Nullable
    public String getBody() {
        return this.responseError.getBody();
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.responseError.getContentType();
    }

    @Override
    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

