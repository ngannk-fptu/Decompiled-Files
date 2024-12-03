/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.WebhookStatistics
 */
package com.atlassian.webhooks.internal.jmx;

import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.WebhookStatistics;
import com.atlassian.webhooks.internal.jmx.WebhooksMXBean;
import com.atlassian.webhooks.internal.publish.WebhookDispatcher;
import java.util.Optional;

public class WebhooksMXBeanAdapter
implements WebhooksMXBean {
    private final WebhookDispatcher dispatcher;
    private final WebhookService webhookService;

    WebhooksMXBeanAdapter(WebhookDispatcher dispatcher, WebhookService webhookService) {
        this.dispatcher = dispatcher;
        this.webhookService = webhookService;
    }

    @Override
    public long getDispatchCount() {
        return this.getStatistics().map(WebhookStatistics::getDispatchedCount).orElse(-1L);
    }

    @Override
    public long getDispatchErrorCount() {
        return this.getStatistics().map(WebhookStatistics::getDispatchErrorCount).orElse(-1L);
    }

    @Override
    public long getDispatchFailureCount() {
        return this.getStatistics().map(WebhookStatistics::getDispatchFailureCount).orElse(-1L);
    }

    @Override
    public long getDispatchInFlightCount() {
        return this.dispatcher.getInFlightCount();
    }

    @Override
    public long getDispatchLastRejectedTimestamp() {
        return this.dispatcher.getLastRejectedTimestamp();
    }

    @Override
    public long getDispatchRejectedCount() {
        return this.getStatistics().map(WebhookStatistics::getDispatchRejectedCount).orElse(-1L);
    }

    @Override
    public long getDispatchSuccessCount() {
        return this.getStatistics().map(WebhookStatistics::getDispatchSuccessCount).orElse(-1L);
    }

    @Override
    public long getPublishCount() {
        return this.getStatistics().map(WebhookStatistics::getPublishedCount).orElse(-1L);
    }

    private Optional<WebhookStatistics> getStatistics() {
        return this.webhookService.getStatistics();
    }
}

