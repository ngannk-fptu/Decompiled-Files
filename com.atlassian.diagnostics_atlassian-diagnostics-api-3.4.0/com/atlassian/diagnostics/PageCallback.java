/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PageSummary;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface PageCallback<T, R> {
    default public void onStart(@Nonnull PageRequest pageRequest) {
    }

    @Nonnull
    public CallbackResult onItem(@Nonnull T var1);

    default public R onEnd(@Nonnull PageSummary summary) {
        return null;
    }
}

