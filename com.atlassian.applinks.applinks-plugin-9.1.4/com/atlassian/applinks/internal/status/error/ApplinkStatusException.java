/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ApplinkStatusException
extends RuntimeException
implements ApplinkError {
    public ApplinkStatusException() {
    }

    public ApplinkStatusException(@Nullable String message) {
        super(message);
    }

    public ApplinkStatusException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @Override
    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

