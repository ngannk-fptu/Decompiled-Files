/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DetailedError {
    @Nullable
    public String getContext();

    @Nonnull
    public String getSummary();

    @Nullable
    public String getDetails();
}

