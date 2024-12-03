/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.HistoricalInvocation
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationHistory
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.history.HistoricalInvocation;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationHistory;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SimpleInvocationHistory
implements InvocationHistory {
    private final HistoricalInvocation lastError;
    private final HistoricalInvocation lastFailure;
    private final HistoricalInvocation lastSuccess;
    private final InvocationCounts counts;

    public SimpleInvocationHistory(@Nonnull InvocationCounts counts, HistoricalInvocation lastError, HistoricalInvocation lastFailure, HistoricalInvocation lastSuccess) {
        this.counts = Objects.requireNonNull(counts, "counts");
        this.lastError = lastError;
        this.lastFailure = lastFailure;
        this.lastSuccess = lastSuccess;
    }

    @Nonnull
    public InvocationCounts getCounts() {
        return this.counts;
    }

    @Nonnull
    public Optional<HistoricalInvocation> getLastError() {
        return Optional.ofNullable(this.lastError);
    }

    @Nonnull
    public Optional<HistoricalInvocation> getLastFailure() {
        return Optional.ofNullable(this.lastFailure);
    }

    @Nonnull
    public Optional<HistoricalInvocation> getLastSuccess() {
        return Optional.ofNullable(this.lastSuccess);
    }
}

