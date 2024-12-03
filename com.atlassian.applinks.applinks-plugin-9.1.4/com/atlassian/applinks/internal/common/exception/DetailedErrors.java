/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.DetailedError;
import javax.annotation.Nonnull;

public interface DetailedErrors {
    @Nonnull
    public Iterable<DetailedError> getErrors();

    public boolean hasErrors();
}

