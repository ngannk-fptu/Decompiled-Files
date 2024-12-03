/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.history.InvocationHistoryService
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.history.InvocationHistoryService;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import java.time.Instant;
import javax.annotation.Nonnull;

public interface InternalInvocationHistoryService
extends InvocationHistoryService {
    public void logInvocationError(@Nonnull WebhookHttpRequest var1, @Nonnull Throwable var2, @Nonnull WebhookInvocation var3, @Nonnull Instant var4, @Nonnull Instant var5);

    public void logInvocationFailure(@Nonnull WebhookHttpRequest var1, @Nonnull WebhookHttpResponse var2, @Nonnull WebhookInvocation var3, @Nonnull Instant var4, @Nonnull Instant var5);

    public void logInvocationSuccess(@Nonnull WebhookHttpRequest var1, @Nonnull WebhookHttpResponse var2, @Nonnull WebhookInvocation var3, @Nonnull Instant var4, @Nonnull Instant var5);
}

