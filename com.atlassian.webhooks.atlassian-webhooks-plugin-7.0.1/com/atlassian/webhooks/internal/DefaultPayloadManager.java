/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.WebhookPayloadManager;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPayloadManager
implements WebhookPayloadManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultPayloadManager.class);
    private final WebhookHostAccessor hostAccessor;

    public DefaultPayloadManager(WebhookHostAccessor hostAccessor) {
        this.hostAccessor = hostAccessor;
    }

    @Override
    public void setPayload(@Nonnull WebhookInvocation invocation, @Nonnull WebhookPayloadBuilder builder) {
        this.hostAccessor.getPayloadProviders().stream().filter(marshaller -> {
            boolean supports = marshaller.supports(invocation);
            if (log.isTraceEnabled()) {
                log.trace("Results of marshaller [{}] supporting is [{}] for invocation [{}]", new Object[]{marshaller.getClass().getSimpleName(), supports, invocation.getId()});
            }
            return supports;
        }).findFirst().ifPresent(payloadProvider -> {
            if (log.isDebugEnabled()) {
                log.debug("Webhook payload has been set by [{}] for invocation [{}]", (Object)payloadProvider.getClass().getSimpleName(), (Object)invocation.getId());
            }
            payloadProvider.setPayload(invocation, builder);
        });
    }
}

