/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookRequestEnricher;
import javax.annotation.Nonnull;

public class BuiltInWebhookEnricher
implements WebhookRequestEnricher {
    public void enrich(@Nonnull WebhookInvocation invocation) {
        invocation.getRequestBuilder().header("X-Event-Key", invocation.getEvent().getId()).header("X-Request-Id", invocation.getId());
    }

    public int getWeight() {
        return 0;
    }
}

