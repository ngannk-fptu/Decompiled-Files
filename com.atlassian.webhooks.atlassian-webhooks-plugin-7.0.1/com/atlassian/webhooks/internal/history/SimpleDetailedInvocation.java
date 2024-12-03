/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.history.DetailedInvocation
 *  com.atlassian.webhooks.history.DetailedInvocationRequest
 *  com.atlassian.webhooks.history.DetailedInvocationResult
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.atlassian.webhooks.history.InvocationRequest
 *  com.atlassian.webhooks.history.InvocationResult
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.DetailedInvocationRequest;
import com.atlassian.webhooks.history.DetailedInvocationResult;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.history.InvocationRequest;
import com.atlassian.webhooks.history.InvocationResult;
import com.atlassian.webhooks.internal.history.SimpleDetailedRequest;
import com.atlassian.webhooks.internal.history.SimpleDetailedResponse;
import com.atlassian.webhooks.internal.history.SimpleHistoricalInvocation;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import java.time.Instant;
import javax.annotation.Nonnull;

public class SimpleDetailedInvocation
extends SimpleHistoricalInvocation
implements DetailedInvocation {
    public SimpleDetailedInvocation(@Nonnull String id, @Nonnull WebhookEvent event, @Nonnull InvocationRequest request, @Nonnull InvocationResult result, @Nonnull Instant start, @Nonnull Instant finish) {
        super(id, event, start, finish, request, result);
    }

    public SimpleDetailedInvocation(@Nonnull WebhookInvocation invocation, @Nonnull InvocationOutcome resultKind, @Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull Instant start, @Nonnull Instant finish) {
        super(invocation.getId(), invocation.getEvent(), start, finish, (InvocationRequest)new SimpleDetailedRequest(invocation, request), (InvocationResult)new SimpleDetailedResponse(resultKind, invocation, response));
    }

    @Nonnull
    public DetailedInvocationRequest getRequest() {
        return (DetailedInvocationRequest)super.getRequest();
    }

    @Nonnull
    public DetailedInvocationResult getResult() {
        return (DetailedInvocationResult)super.getResult();
    }
}

