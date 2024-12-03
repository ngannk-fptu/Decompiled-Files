/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookFilter
 *  com.atlassian.webhooks.WebhookPayloadProvider
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookFilter;
import com.atlassian.webhooks.WebhookPayloadProvider;
import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.WebhooksConfiguration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nonnull;

public interface WebhookHostAccessor {
    @Nonnull
    public Optional<WebhooksConfiguration> getConfiguration();

    @Nonnull
    public Collection<WebhookRequestEnricher> getEnrichers();

    @Nonnull
    public WebhookEvent getEvent(@Nonnull String var1);

    @Nonnull
    public List<WebhookEvent> getEvents();

    @Nonnull
    public ScheduledExecutorService getExecutorService();

    @Nonnull
    public Collection<WebhookFilter> getFilters();

    @Nonnull
    public Collection<WebhookPayloadProvider> getPayloadProviders();
}

