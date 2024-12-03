/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.HistoricalInvocationRequest;
import com.atlassian.webhooks.history.InvocationHistory;
import com.atlassian.webhooks.history.InvocationHistoryByEventRequest;
import com.atlassian.webhooks.history.InvocationHistoryRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface InvocationHistoryService {
    @Nonnull
    public InvocationHistory get(@Nonnull InvocationHistoryRequest var1);

    @Nonnull
    public Map<WebhookEvent, InvocationHistory> getByEvent(@Nonnull InvocationHistoryByEventRequest var1);

    @Nonnull
    public Map<Integer, InvocationHistory> getByWebhook(Collection<Integer> var1);

    @Nonnull
    public Map<Integer, InvocationHistory> getByWebhookForDays(Collection<Integer> var1, int var2);

    @Nonnull
    public Optional<DetailedInvocation> getLatestInvocation(@Nonnull HistoricalInvocationRequest var1);
}

