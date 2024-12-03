/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent;
import com.atlassian.webhooks.internal.history.InternalInvocationHistoryService;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookInvocationHistorian
implements WebhookRequestEnricher,
WebhookCallback {
    private static final Logger log = LoggerFactory.getLogger(WebhookInvocationHistorian.class);
    private final InternalInvocationHistoryService historyService;
    private final ConcurrentMap<String, Instant> startTimes = Maps.newConcurrentMap();

    public WebhookInvocationHistorian(@Nonnull InternalInvocationHistoryService historyService) {
        this.historyService = historyService;
    }

    public void enrich(@Nonnull WebhookInvocation invocation) {
        if (invocation.getEvent() instanceof WebhookDiagnosticsEvent || invocation.getWebhook().getId() < 0) {
            return;
        }
        this.startTimes.put(invocation.getId(), Instant.now());
        invocation.registerCallback((WebhookCallback)this);
    }

    public int getWeight() {
        return 0;
    }

    public void onError(@Nonnull WebhookHttpRequest webhookHttpRequest, @Nonnull Throwable throwable, @Nonnull WebhookInvocation webhookInvocation) {
        Instant finishTime = Instant.now();
        Instant startTime = this.getAndClearStartTimeFor(webhookInvocation, finishTime);
        this.historyService.logInvocationError(webhookHttpRequest, throwable, webhookInvocation, startTime, finishTime);
    }

    public void onFailure(@Nonnull WebhookHttpRequest webhookHttpRequest, @Nonnull WebhookHttpResponse webhookHttpResponse, @Nonnull WebhookInvocation webhookInvocation) {
        Instant finishTime = Instant.now();
        Instant startTime = this.getAndClearStartTimeFor(webhookInvocation, finishTime);
        this.historyService.logInvocationFailure(webhookHttpRequest, webhookHttpResponse, webhookInvocation, startTime, finishTime);
    }

    public void onSuccess(@Nonnull WebhookHttpRequest webhookHttpRequest, @Nonnull WebhookHttpResponse webhookHttpResponse, @Nonnull WebhookInvocation webhookInvocation) {
        Instant finishTime = Instant.now();
        Instant startTime = this.getAndClearStartTimeFor(webhookInvocation, finishTime);
        this.historyService.logInvocationSuccess(webhookHttpRequest, webhookHttpResponse, webhookInvocation, startTime, finishTime);
    }

    private Instant getAndClearStartTimeFor(@Nonnull WebhookInvocation webhookInvocation, Instant defaultStartTime) {
        Instant startTime = (Instant)this.startTimes.remove(webhookInvocation.getId());
        if (startTime == null) {
            log.debug("Unable to determine the starting time for webhook invocation {}. Assuming start and finish times equal", (Object)webhookInvocation.getId());
            startTime = defaultStartTime;
        }
        return startTime;
    }
}

