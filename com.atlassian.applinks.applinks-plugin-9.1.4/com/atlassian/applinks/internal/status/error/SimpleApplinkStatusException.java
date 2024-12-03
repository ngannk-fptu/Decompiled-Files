/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleApplinkStatusException
extends ApplinkStatusException {
    private final ApplinkErrorType errorType;

    public SimpleApplinkStatusException(@Nonnull ApplinkErrorType errorType, @Nullable String details, @Nullable Throwable cause) {
        super(details, cause);
        this.errorType = Objects.requireNonNull(errorType, "errorType");
    }

    public SimpleApplinkStatusException(@Nonnull ApplinkErrorType errorType, @Nullable String details) {
        super(details);
        this.errorType = Objects.requireNonNull(errorType, "errorType");
    }

    public SimpleApplinkStatusException(@Nonnull ApplinkErrorType errorType) {
        this(errorType, null);
    }

    public SimpleApplinkStatusException(@Nonnull ApplinkError applinkError) {
        this(applinkError, null);
    }

    public SimpleApplinkStatusException(@Nonnull ApplinkError applinkError, @Nullable Throwable cause) {
        this(Objects.requireNonNull(applinkError, "applinkError").getType(), applinkError.getDetails(), cause);
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return this.errorType;
    }

    @Override
    @Nullable
    public String getDetails() {
        return this.getMessage();
    }
}

