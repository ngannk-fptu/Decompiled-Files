/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.PageSummary;
import javax.annotation.Nonnull;

public abstract class AbstractPageCallback<T, R>
implements PageCallback<T, R> {
    protected R value;

    protected AbstractPageCallback(R value) {
        this.value = value;
    }

    @Override
    public void onStart(@Nonnull PageRequest pageRequest) {
    }

    @Override
    public R onEnd(@Nonnull PageSummary summary) {
        return this.value;
    }
}

