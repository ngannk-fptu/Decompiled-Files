/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.google.common.annotations.Beta;

@Beta
public interface RetryListener {
    public <V> void onRetry(Attempt<V> var1);
}

