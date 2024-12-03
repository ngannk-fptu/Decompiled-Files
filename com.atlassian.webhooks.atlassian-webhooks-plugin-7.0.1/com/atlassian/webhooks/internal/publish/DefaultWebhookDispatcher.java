/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.DispatchFailedException
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.atlassian.webhooks.WebhooksNotInitializedException
 *  com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.publish;

import com.atlassian.webhooks.DispatchFailedException;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.WebhooksNotInitializedException;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.client.RequestExecutor;
import com.atlassian.webhooks.internal.client.request.RawRequest;
import com.atlassian.webhooks.internal.publish.InternalWebhookInvocation;
import com.atlassian.webhooks.internal.publish.WebhookDispatcher;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebhookDispatcher
implements WebhookDispatcher,
WebhooksLifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebhookDispatcher.class);
    private final ConcurrentMap<Integer, WebhookCircuitBreaker> circuitBreakers;
    private final Clock clock;
    private final RequestExecutor requestExecutor;
    private Semaphore dispatchTickets;
    private long ticketTimeoutMillis;
    private volatile WebhooksConfiguration config;
    private volatile long lastRejectedTimestamp;

    DefaultWebhookDispatcher(Clock clock, RequestExecutor requestExecutor) {
        this.clock = clock;
        this.requestExecutor = requestExecutor;
        this.circuitBreakers = new ConcurrentHashMap<Integer, WebhookCircuitBreaker>();
        this.config = WebhooksConfiguration.DEFAULT;
    }

    public DefaultWebhookDispatcher(RequestExecutor requestExecutor) {
        this(Clock.systemDefaultZone(), requestExecutor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispatch(@Nonnull InternalWebhookInvocation invocation) {
        long delay;
        log.debug("Starting dispatch work for webhook invocation [{}]", (Object)invocation.getId());
        RawRequest request = invocation.getRequestBuilder().build();
        Ticket ticket = this.acquireTicket(invocation);
        if (ticket == null) {
            this.onSkipped(invocation, request, "Too many webhook dispatches already in flight");
            return;
        }
        WebhookCircuitBreaker circuitBreaker = (WebhookCircuitBreaker)this.circuitBreakers.get(invocation.getWebhook().getId());
        if (circuitBreaker != null && (delay = circuitBreaker.getMillisToNextAttempt()) > 0L) {
            ticket.close();
            this.onSkipped(invocation, request, "Webhook failed too many times. Skipping this webhook for the next " + delay + "ms");
            return;
        }
        try {
            this.requestExecutor.execute(request).handleAsync((webhookHttpResponse, throwable) -> {
                try {
                    int statusCode = throwable == null ? webhookHttpResponse.getStatusCode() : -1;
                    log.debug("Request has completed for webhook invocation [{}]. Status code = {}", (Object)invocation.getId(), (Object)statusCode);
                    if (throwable != null) {
                        if (throwable instanceof WebhooksNotInitializedException) {
                            this.onSkipped(invocation, request, throwable.getLocalizedMessage());
                        } else {
                            this.onError(invocation, request, (Throwable)throwable);
                        }
                    } else if (statusCode >= 200 && statusCode < 300) {
                        this.onSuccess(invocation, request, (WebhookHttpResponse)webhookHttpResponse);
                    } else {
                        this.onFailure(invocation, request, (WebhookHttpResponse)webhookHttpResponse);
                    }
                }
                finally {
                    ticket.close();
                }
                return null;
            });
        }
        catch (Throwable t) {
            try {
                this.onError(invocation, request, t);
            }
            finally {
                ticket.close();
            }
            throw Throwables.propagate((Throwable)t);
        }
    }

    @Override
    public int getInFlightCount() {
        if (this.dispatchTickets == null) {
            return -1;
        }
        return this.config.getMaxInFlightDispatches() - this.dispatchTickets.availablePermits();
    }

    @Override
    public long getLastRejectedTimestamp() {
        return this.lastRejectedTimestamp;
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        this.config = configuration;
        this.dispatchTickets = new Semaphore(configuration.getMaxInFlightDispatches());
        this.ticketTimeoutMillis = configuration.getDispatchTimeout().toMillis();
    }

    @Override
    public void onStop() {
        this.config = WebhooksConfiguration.DEFAULT;
    }

    @VisibleForTesting
    int getAvailableTickets() {
        return this.dispatchTickets.availablePermits();
    }

    private static Consumer<WebhookCallback> safely(Consumer<WebhookCallback> consumer) {
        return callback -> {
            try {
                consumer.accept((WebhookCallback)callback);
            }
            catch (RuntimeException e) {
                log.warn("Webhook callback failed", (Throwable)e);
            }
        };
    }

    private Ticket acquireTicket(InternalWebhookInvocation invocation) {
        try {
            if (this.dispatchTickets == null) {
                log.warn("A ticket was acquired before the webhooks plugin was started, this dispatch will be allowed");
                return new DummyTicket();
            }
            if (!this.dispatchTickets.tryAcquire(this.ticketTimeoutMillis, TimeUnit.MILLISECONDS)) {
                log.warn("Could not dispatch {} webhook to {}; a maximum of {} dispatches are already in flight", new Object[]{invocation.getEvent().getId(), invocation.getWebhook().getUrl(), this.config.getMaxInFlightDispatches()});
                return null;
            }
            return new DispatchTicket();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private long calculateNextAttemptTimestamp(int failureCount) {
        int significantFailures = failureCount - this.config.getBackoffTriggerCount();
        if (significantFailures >= 0) {
            return this.clock.millis() + Math.min(this.config.getBackoffMaxDelay().toMillis(), Math.round((double)this.config.getBackoffInitialDelay().toMillis() * Math.pow(this.config.getBackoffExponent(), significantFailures)));
        }
        return 0L;
    }

    private void onError(InternalWebhookInvocation invocation, WebhookHttpRequest request, Throwable throwable) {
        log.info("Webhook invocation [{}] to [{}] failed with an error", new Object[]{invocation.getId(), invocation.getWebhook().getUrl(), log.isDebugEnabled() ? throwable : null});
        this.updateCircuitBreakerWithFailure(invocation);
        invocation.getCallbacks().forEach(DefaultWebhookDispatcher.safely(callback -> {
            if (log.isTraceEnabled()) {
                log.trace("Call back to [{}] from webhook invocation [{}] in error", (Object)callback.getClass().getSimpleName(), (Object)invocation.getId());
            }
            callback.onError(request, throwable, (WebhookInvocation)invocation);
        }));
    }

    private void onFailure(InternalWebhookInvocation invocation, WebhookHttpRequest request, WebhookHttpResponse response) {
        this.updateCircuitBreakerWithFailure(invocation);
        invocation.getCallbacks().forEach(DefaultWebhookDispatcher.safely(callback -> {
            if (log.isTraceEnabled()) {
                log.trace("Call back to [{}] from failed webhook invocation [{}]", (Object)callback.getClass().getSimpleName(), (Object)invocation.getId());
            }
            callback.onFailure(request, response, (WebhookInvocation)invocation);
        }));
    }

    private void onSkipped(InternalWebhookInvocation invocation, WebhookHttpRequest request, String message) {
        this.lastRejectedTimestamp = this.clock.millis();
        DispatchFailedException exception = new DispatchFailedException((WebhookInvocation)invocation, message);
        log.debug("Skipping webhook invocation [{}] to {} ({})", new Object[]{invocation.getId(), invocation.getWebhook().getUrl(), message});
        invocation.getCallbacks().forEach(DefaultWebhookDispatcher.safely(callback -> callback.onError(request, (Throwable)exception, (WebhookInvocation)invocation)));
    }

    private void onSuccess(InternalWebhookInvocation invocation, WebhookHttpRequest request, WebhookHttpResponse response) {
        this.circuitBreakers.remove(invocation.getWebhook().getId());
        invocation.getCallbacks().forEach(DefaultWebhookDispatcher.safely(callback -> {
            if (log.isTraceEnabled()) {
                log.trace("Call back to [{}] from successful webhook invocation [{}]", (Object)callback.getClass().getSimpleName(), (Object)invocation.getId());
            }
            callback.onSuccess(request, response, (WebhookInvocation)invocation);
        }));
    }

    private void updateCircuitBreakerWithFailure(InternalWebhookInvocation invocation) {
        if (!(invocation.getEvent() instanceof WebhookDiagnosticsEvent)) {
            this.circuitBreakers.computeIfAbsent(invocation.getWebhook().getId(), id -> new WebhookCircuitBreaker()).onFailure();
        }
    }

    private static class DummyTicket
    implements Ticket {
        private DummyTicket() {
        }

        @Override
        public void close() {
        }
    }

    private class DispatchTicket
    implements Ticket {
        private DispatchTicket() {
        }

        @Override
        public void close() {
            DefaultWebhookDispatcher.this.dispatchTickets.release();
        }
    }

    private static interface Ticket
    extends AutoCloseable {
        @Override
        public void close();
    }

    private class WebhookCircuitBreaker {
        private int failureCount;
        private long nextAttemptTimestamp;

        private WebhookCircuitBreaker() {
        }

        synchronized long getMillisToNextAttempt() {
            return Math.max(0L, this.nextAttemptTimestamp - DefaultWebhookDispatcher.this.clock.millis());
        }

        synchronized void onFailure() {
            this.nextAttemptTimestamp = DefaultWebhookDispatcher.this.calculateNextAttemptTimestamp(++this.failureCount);
        }
    }
}

