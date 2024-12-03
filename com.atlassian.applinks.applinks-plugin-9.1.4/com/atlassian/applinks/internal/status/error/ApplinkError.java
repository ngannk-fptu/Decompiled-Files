/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ApplinkError {
    @Nonnull
    public ApplinkErrorType getType();

    @Nullable
    public String getDetails();

    @Nullable
    public <T> T accept(@Nonnull ApplinkErrorVisitor<T> var1);
}

