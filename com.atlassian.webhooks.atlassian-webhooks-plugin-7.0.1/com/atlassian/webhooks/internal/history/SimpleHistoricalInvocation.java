/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.history.HistoricalInvocation
 *  com.atlassian.webhooks.history.InvocationRequest
 *  com.atlassian.webhooks.history.InvocationResult
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.history.HistoricalInvocation;
import com.atlassian.webhooks.history.InvocationRequest;
import com.atlassian.webhooks.history.InvocationResult;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleHistoricalInvocation
implements HistoricalInvocation {
    private final WebhookEvent event;
    private final Instant finish;
    private final InvocationRequest request;
    private final InvocationResult result;
    private final Instant start;
    private final String id;

    protected SimpleHistoricalInvocation(@Nonnull String id, @Nonnull WebhookEvent event, @Nonnull Instant start, @Nonnull Instant finish, @Nonnull InvocationRequest request, @Nonnull InvocationResult result) {
        this.id = Objects.requireNonNull(id, "id");
        this.event = Objects.requireNonNull(event, "event");
        this.finish = Objects.requireNonNull(finish, "finish");
        this.request = Objects.requireNonNull(request, "request");
        this.result = Objects.requireNonNull(result, "result");
        this.start = Objects.requireNonNull(start, "start");
    }

    @Nonnull
    public Duration getDuration() {
        return Duration.between(this.start, this.finish);
    }

    @Nonnull
    public WebhookEvent getEvent() {
        return this.event;
    }

    @Nonnull
    public Instant getFinish() {
        return this.finish;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public InvocationRequest getRequest() {
        return this.request;
    }

    @Nonnull
    public InvocationResult getResult() {
        return this.result;
    }

    @Nonnull
    public Instant getStart() {
        return this.start;
    }
}

