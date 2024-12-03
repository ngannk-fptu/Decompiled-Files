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
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SimpleApplinkError
implements ApplinkError {
    private final ApplinkErrorType errorType;
    private final String error;

    public SimpleApplinkError(@Nonnull ApplinkErrorType errorType) {
        this(errorType, null);
    }

    public SimpleApplinkError(@Nonnull ApplinkErrorType errorType, @Nullable String error) {
        this.errorType = Objects.requireNonNull(errorType, "errorType");
        this.error = error;
    }

    @Override
    @Nonnull
    public ApplinkErrorType getType() {
        return this.errorType;
    }

    @Override
    @Nullable
    public String getDetails() {
        return this.error;
    }

    @Override
    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

