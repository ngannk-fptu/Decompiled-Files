/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.CallbackResult
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.CallbackResult;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface RowCallback<T> {
    @Nonnull
    public CallbackResult onRow(T var1);
}

