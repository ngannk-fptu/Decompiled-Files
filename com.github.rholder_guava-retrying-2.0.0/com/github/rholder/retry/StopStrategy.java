/*
 * Decompiled with CFR 0.152.
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;

public interface StopStrategy {
    public boolean shouldStop(Attempt var1);
}

