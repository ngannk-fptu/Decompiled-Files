/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface WebhookInvocation {
    @Nonnull
    public String getId();

    @Nonnull
    public WebhookEvent getEvent();

    @Nonnull
    public Optional<Object> getPayload();

    @Nonnull
    public WebhookHttpRequest.Builder getRequestBuilder();

    @Nonnull
    public Webhook getWebhook();

    public void registerCallback(@Nonnull WebhookCallback var1);
}

