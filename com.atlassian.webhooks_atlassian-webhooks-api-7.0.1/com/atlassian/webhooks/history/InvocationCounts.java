/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

public interface InvocationCounts {
    public int getErrors();

    public int getFailures();

    public int getSuccesses();

    @Nonnull
    public Duration getWindowDuration();

    @Nonnull
    public Instant getWindowStart();
}

