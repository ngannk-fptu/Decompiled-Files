/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.PingRequest;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCreateRequest;
import com.atlassian.webhooks.WebhookDeleteRequest;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookPublishRequest;
import com.atlassian.webhooks.WebhookSearchRequest;
import com.atlassian.webhooks.WebhookStatistics;
import com.atlassian.webhooks.WebhookUpdateRequest;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsResult;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import javax.annotation.Nonnull;

public interface WebhookService {
    @Nonnull
    public Webhook create(@Nonnull WebhookCreateRequest var1);

    public boolean delete(int var1);

    public int delete(@Nonnull WebhookDeleteRequest var1);

    @Nonnull
    public Optional<Webhook> findById(int var1);

    @Nonnull
    public Optional<WebhookEvent> getEvent(@Nonnull String var1);

    @Nonnull
    public List<WebhookEvent> getEvents();

    @Nonnull
    public Optional<WebhookStatistics> getStatistics();

    @Nonnull
    public Future<WebhookDiagnosticsResult> ping(@Nonnull PingRequest var1);

    public void publish(@Nonnull WebhookPublishRequest var1);

    @Nonnull
    public List<Webhook> search(@Nonnull WebhookSearchRequest var1);

    public void setStatisticsEnabled(boolean var1);

    @Nonnull
    public Webhook update(int var1, @Nonnull WebhookUpdateRequest var2);
}

