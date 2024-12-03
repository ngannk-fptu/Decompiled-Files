/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.DetailedInvocationRequest;
import com.atlassian.webhooks.history.DetailedInvocationResult;
import com.atlassian.webhooks.history.HistoricalInvocation;
import javax.annotation.Nonnull;

public interface DetailedInvocation
extends HistoricalInvocation {
    @Override
    @Nonnull
    public DetailedInvocationRequest getRequest();

    @Override
    @Nonnull
    public DetailedInvocationResult getResult();
}

