/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.remote;

import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.internal.status.error.ApplinkErrorCategory;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ApplinkStatusAccessException
extends ApplinkStatusException
implements AuthorisationUriAwareApplinkError {
    private final AuthorisationURIGenerator uriGenerator;
    private final ApplinkErrorType errorType;

    public ApplinkStatusAccessException(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull AuthorisationURIGenerator uriGenerator, @Nullable String message) {
        this(applinkErrorType, uriGenerator, message, null);
    }

    public ApplinkStatusAccessException(@Nonnull ApplinkErrorType applinkErrorType, @Nonnull AuthorisationURIGenerator uriGenerator, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        Objects.requireNonNull(applinkErrorType, "errorType");
        Preconditions.checkState((ApplinkErrorCategory.ACCESS_ERROR == applinkErrorType.getCategory() ? 1 : 0) != 0, (Object)("Only ACCESS_ERROR types allowed, was: " + (Object)((Object)applinkErrorType)));
        this.errorType = applinkErrorType;
        this.uriGenerator = Objects.requireNonNull(uriGenerator, "uriGenerator");
    }

    public ApplinkStatusAccessException(@Nonnull AuthorisationUriAwareApplinkError error, @Nullable Throwable cause) {
        this(Objects.requireNonNull(error, "error").getType(), error.getAuthorisationUriGenerator(), error.getDetails(), cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return this.errorType;
    }

    @Override
    @Nullable
    public String getDetails() {
        return null;
    }

    @Override
    @Nonnull
    public AuthorisationURIGenerator getAuthorisationUriGenerator() {
        return this.uriGenerator;
    }

    @Override
    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

