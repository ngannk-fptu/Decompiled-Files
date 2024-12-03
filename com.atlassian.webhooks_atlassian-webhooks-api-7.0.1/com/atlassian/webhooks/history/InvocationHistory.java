/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.HistoricalInvocation;
import com.atlassian.webhooks.history.InvocationCounts;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface InvocationHistory {
    @Nonnull
    public Optional<HistoricalInvocation> getLastError();

    @Nonnull
    public Optional<HistoricalInvocation> getLastFailure();

    @Nonnull
    public Optional<HistoricalInvocation> getLastSuccess();

    @Nonnull
    public InvocationCounts getCounts();
}

