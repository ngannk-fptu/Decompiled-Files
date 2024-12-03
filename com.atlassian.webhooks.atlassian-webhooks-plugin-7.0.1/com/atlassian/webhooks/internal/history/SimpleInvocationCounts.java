/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.InvocationCounts
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.history.InvocationCounts;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleInvocationCounts
implements InvocationCounts {
    private final Duration duration;
    private final int errors;
    private final int failures;
    private final int successes;

    public SimpleInvocationCounts(@Nonnull Duration duration, int errors, int failures, int successes) {
        this.duration = Objects.requireNonNull(duration, "duration");
        this.errors = errors;
        this.failures = failures;
        this.successes = successes;
    }

    public int getErrors() {
        return this.errors;
    }

    public int getFailures() {
        return this.failures;
    }

    public int getSuccesses() {
        return this.successes;
    }

    @Nonnull
    public Duration getWindowDuration() {
        return this.duration;
    }

    @Nonnull
    public Instant getWindowStart() {
        return Instant.now().minus(this.duration);
    }
}

