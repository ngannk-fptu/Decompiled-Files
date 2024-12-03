/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.atlassian.webhooks.history.DetailedInvocation
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.dao.InvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.PendingInvocationData;
import com.atlassian.webhooks.internal.dao.WebhookAndEvent;
import com.atlassian.webhooks.internal.dao.ao.AoHistoricalInvocation;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class AsyncInvocationHistoryDao
implements InvocationHistoryDao,
WebhooksLifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(AsyncInvocationHistoryDao.class);
    private static final int MAX_ATTEMPTS = 3;
    private final InvocationHistoryDao dao;
    private final WebhookHostAccessor hostAccessor;
    private final ConcurrentMap<WebhookAndEvent, PendingInvocationData> pendingByWebhookAndEvent;
    private final TransactionTemplate txTemplate;
    private long flushIntervalSeconds;
    private volatile boolean active;
    private volatile Future<?> future;

    public AsyncInvocationHistoryDao(@Qualifier(value="aoInvocationHistoryDao") InvocationHistoryDao dao, WebhookHostAccessor hostAccessor, TransactionTemplate txTemplate) {
        this.dao = dao;
        this.hostAccessor = hostAccessor;
        this.txTemplate = txTemplate;
        this.flushIntervalSeconds = 30L;
        this.pendingByWebhookAndEvent = new ConcurrentHashMap<WebhookAndEvent, PendingInvocationData>();
    }

    @Override
    public void addCounts(int webhookId, String eventId, Date date, int errors, int failures, int successes) {
        if (this.active && System.currentTimeMillis() - date.getTime() < TimeUnit.MINUTES.toMillis(5L)) {
            boolean updated = false;
            while (!updated) {
                PendingInvocationData pending = this.getPending(webhookId, eventId);
                updated = pending.addCounts(errors, failures, successes);
            }
        } else {
            this.dao.addCounts(webhookId, eventId, date, errors, failures, successes);
        }
    }

    @Override
    public int deleteDailyCountsOlderThan(int days) {
        return this.dao.deleteDailyCountsOlderThan(days);
    }

    @Override
    public void deleteForWebhook(int webhookId) {
        this.dao.deleteForWebhook(webhookId);
    }

    @Override
    @Nonnull
    public Map<String, String> decodeHeaders(String id, String headersString) {
        return this.dao.decodeHeaders(id, headersString);
    }

    @Override
    @Nonnull
    public InvocationCounts getCounts(int webhookId, String eventId, int days) {
        this.flush(webhookId, eventId);
        return this.dao.getCounts(webhookId, eventId, days);
    }

    @Override
    @Nonnull
    public Map<String, InvocationCounts> getCountsByEvent(int webhookId, @Nonnull Collection<String> eventIds, int days) {
        for (String eventId : eventIds) {
            this.flush(webhookId, eventId);
        }
        return this.dao.getCountsByEvent(webhookId, eventIds, days);
    }

    @Override
    @Nonnull
    public Map<Integer, InvocationCounts> getCountsByWebhook(@Nonnull Collection<Integer> webhookIds, int days) {
        this.pendingByWebhookAndEvent.keySet().stream().filter(key -> webhookIds.contains(key.getWebhookId())).forEach(key -> this.flush(key.getWebhookId(), key.getEventId()));
        return this.dao.getCountsByWebhook(webhookIds, days);
    }

    @Override
    public AoHistoricalInvocation getLatestInvocation(int webhookId, String eventId, Collection<InvocationOutcome> outcomes) {
        this.flush(webhookId, eventId);
        return this.dao.getLatestInvocation(webhookId, eventId, outcomes);
    }

    @Override
    @Nonnull
    public List<AoHistoricalInvocation> getLatestInvocations(int webhookId, String eventId, Collection<InvocationOutcome> outcomes) {
        this.flush(webhookId, eventId);
        return this.dao.getLatestInvocations(webhookId, eventId, outcomes);
    }

    @Override
    @Nonnull
    public Multimap<String, AoHistoricalInvocation> getLatestInvocationsByEvent(int webhookId, @Nonnull Collection<String> eventIds) {
        for (String eventId : eventIds) {
            this.flush(webhookId, eventId);
        }
        return this.dao.getLatestInvocationsByEvent(webhookId, eventIds);
    }

    @Override
    @Nonnull
    public Multimap<Integer, AoHistoricalInvocation> getLatestInvocationsByWebhook(@Nonnull Collection<Integer> webhookIds) {
        this.pendingByWebhookAndEvent.keySet().stream().filter(key -> webhookIds.contains(key.getWebhookId())).forEach(key -> this.flush(key.getWebhookId(), key.getEventId()));
        return this.dao.getLatestInvocationsByWebhook(webhookIds);
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        this.active = true;
        this.flushIntervalSeconds = configuration.getStatisticsFlushInterval().getSeconds();
        this.scheduleFlush();
    }

    @Override
    public void onStop() {
        if (this.future != null) {
            Future<?> f = this.future;
            this.future = null;
            f.cancel(false);
        }
        this.flush();
        this.active = false;
    }

    @Override
    public void saveInvocation(int webhookId, @Nonnull DetailedInvocation invocation) {
        boolean updated = false;
        while (!updated) {
            if (!this.active) {
                this.dao.saveInvocation(webhookId, invocation);
                return;
            }
            updated = this.getPending(webhookId, invocation.getEvent().getId()).onInvocation(invocation);
        }
    }

    @VisibleForTesting
    void flush() {
        if (this.pendingByWebhookAndEvent.isEmpty()) {
            return;
        }
        HashMap<WebhookAndEvent, PendingInvocationData> data = new HashMap<WebhookAndEvent, PendingInvocationData>();
        data.putAll(this.pendingByWebhookAndEvent);
        data.keySet().forEach(this.pendingByWebhookAndEvent::remove);
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            WebhookAndEvent key2 = (WebhookAndEvent)entry.getKey();
            try {
                this.flushPending(key2.getWebhookId(), key2.getEventId(), (PendingInvocationData)entry.getValue());
                it.remove();
            }
            catch (RuntimeException e) {
                log.warn("Failed to write webhook invocation data for {}:{} to the database", new Object[]{key2.getWebhookId(), key2.getEventId(), e});
            }
        }
        data.forEach((key, value) -> this.returnPending(key.getWebhookId(), key.getEventId(), (PendingInvocationData)value));
    }

    private void flush(int webhookId, String eventId) {
        WebhookAndEvent key = new WebhookAndEvent(webhookId, eventId);
        PendingInvocationData pending = (PendingInvocationData)this.pendingByWebhookAndEvent.remove(key);
        if (pending != null) {
            try {
                this.flushPending(webhookId, eventId, pending);
            }
            catch (RuntimeException e) {
                log.warn("Failed to flush webhook invocation data for {}:{} to the database", new Object[]{webhookId, eventId, e});
                this.returnPending(webhookId, eventId, pending);
            }
        }
    }

    private void flushAndReschedule() {
        try {
            this.flush();
        }
        finally {
            this.scheduleFlush();
        }
    }

    private void flushPending(int webhookId, String eventId, PendingInvocationData pending) {
        log.trace("Flushing webhook invocation data for {}:{} to the database", (Object)webhookId, (Object)eventId);
        pending.freeze();
        this.txTemplate.execute(() -> {
            this.maybeFlush(webhookId, pending.getLatestError());
            this.maybeFlush(webhookId, pending.getLatestFailure());
            this.maybeFlush(webhookId, pending.getLatestSuccess());
            return null;
        });
        for (int attempt = 1; attempt <= 3; ++attempt) {
            try {
                this.txTemplate.execute(() -> {
                    this.dao.addCounts(webhookId, eventId, new Date(), pending.getErrorCount(), pending.getFailureCount(), pending.getSuccessCount());
                    return null;
                });
                break;
            }
            catch (RuntimeException e) {
                if (attempt == 3) {
                    throw e;
                }
                log.debug("Update of invocation counts for {}:{} failed. Retrying", new Object[]{webhookId, eventId, e});
                continue;
            }
        }
    }

    private PendingInvocationData getPending(int webhookId, String eventId) {
        return this.pendingByWebhookAndEvent.computeIfAbsent(new WebhookAndEvent(webhookId, eventId), key -> new PendingInvocationData());
    }

    private void maybeFlush(int webhookId, DetailedInvocation invocation) {
        if (invocation != null) {
            this.dao.saveInvocation(webhookId, invocation);
        }
    }

    private void returnPending(int webhookId, String eventId, PendingInvocationData pending) {
        boolean updated = false;
        while (!updated) {
            updated = this.getPending(webhookId, eventId).addAll(pending);
        }
    }

    private void scheduleFlush() {
        if (this.active) {
            this.future = this.hostAccessor.getExecutorService().schedule(this::flushAndReschedule, this.flushIntervalSeconds, TimeUnit.SECONDS);
        }
    }
}

