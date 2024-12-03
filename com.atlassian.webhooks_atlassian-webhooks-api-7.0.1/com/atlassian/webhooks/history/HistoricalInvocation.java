/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.history.InvocationRequest;
import com.atlassian.webhooks.history.InvocationResult;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

public interface HistoricalInvocation {
    @Nonnull
    public Duration getDuration();

    @Nonnull
    public WebhookEvent getEvent();

    @Nonnull
    public Instant getFinish();

    @Nonnull
    public String getId();

    @Nonnull
    public InvocationRequest getRequest();

    @Nonnull
    public InvocationResult getResult();

    @Nonnull
    public Instant getStart();
}

