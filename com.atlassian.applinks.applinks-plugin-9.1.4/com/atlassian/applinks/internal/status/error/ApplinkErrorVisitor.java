/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.status.error;

import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ApplinkErrorVisitor<T> {
    @Nullable
    public T visit(@Nonnull ApplinkError var1);

    @Nullable
    public T visit(@Nonnull AuthorisationUriAwareApplinkError var1);

    @Nullable
    public T visit(@Nonnull ResponseApplinkError var1);
}

