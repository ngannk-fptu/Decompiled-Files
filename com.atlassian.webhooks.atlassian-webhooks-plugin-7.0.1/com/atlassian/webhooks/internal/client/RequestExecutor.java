/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.client;

import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;

public interface RequestExecutor {
    @Nonnull
    public CompletableFuture<WebhookHttpResponse> execute(@Nonnull WebhookHttpRequest var1);
}

