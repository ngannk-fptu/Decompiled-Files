/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.webhooks.NoSuchWebhookException
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.atlassian.webhooks.event.WebhookDeletedEvent
 *  com.atlassian.webhooks.history.DetailedInvocation
 *  com.atlassian.webhooks.history.HistoricalInvocation
 *  com.atlassian.webhooks.history.HistoricalInvocationRequest
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationHistory
 *  com.atlassian.webhooks.history.InvocationHistoryByEventRequest
 *  com.atlassian.webhooks.history.InvocationHistoryRequest
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.atlassian.webhooks.history.InvocationRequest
 *  com.atlassian.webhooks.history.InvocationResult
 *  com.atlassian.webhooks.request.Method
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.webhooks.NoSuchWebhookException;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.event.WebhookDeletedEvent;
import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.HistoricalInvocation;
import com.atlassian.webhooks.history.HistoricalInvocationRequest;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationHistory;
import com.atlassian.webhooks.history.InvocationHistoryByEventRequest;
import com.atlassian.webhooks.history.InvocationHistoryRequest;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.history.InvocationRequest;
import com.atlassian.webhooks.history.InvocationResult;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.dao.InvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.ao.AoHistoricalInvocation;
import com.atlassian.webhooks.internal.history.InternalInvocationHistoryService;
import com.atlassian.webhooks.internal.history.SimpleDetailedError;
import com.atlassian.webhooks.internal.history.SimpleDetailedInvocation;
import com.atlassian.webhooks.internal.history.SimpleDetailedRequest;
import com.atlassian.webhooks.internal.history.SimpleDetailedResponse;
import com.atlassian.webhooks.internal.history.SimpleHistoricalInvocation;
import com.atlassian.webhooks.internal.history.SimpleInvocationCounts;
import com.atlassian.webhooks.internal.history.SimpleInvocationHistory;
import com.atlassian.webhooks.internal.history.SimpleInvocationRequest;
import com.atlassian.webhooks.internal.history.SimpleInvocationResult;
import com.atlassian.webhooks.internal.model.UnknownWebhookEvent;
import com.atlassian.webhooks.request.Method;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultInvocationHistoryService
implements InternalInvocationHistoryService,
WebhooksLifecycleAware {
    static final int COUNTS_DAYS = 30;
    private static final Logger log = LoggerFactory.getLogger(DefaultInvocationHistoryService.class);
    private static final Duration COUNTS_DURATION = Duration.of(30L, ChronoUnit.DAYS);
    private static final InvocationCounts NO_INVOCATIONS = new SimpleInvocationCounts(COUNTS_DURATION, 0, 0, 0);
    private static final InvocationHistory EMPTY_HISTORY = new SimpleInvocationHistory(NO_INVOCATIONS, null, null, null);
    private static final JobId JOB_ID = JobId.of((String)"webhooks.history.daily.cleanup.job");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)"webhooks.history.daily.cleanup.runner");
    private static final int MAX_ATTEMPTS = 3;
    private final InvocationHistoryDao dao;
    private final EventListenerRegistrar eventListenerRegistrar;
    private final SchedulerService schedulerService;
    private final TransactionTemplate txTemplate;
    private final WebhookService webhookService;

    public DefaultInvocationHistoryService(@Qualifier(value="asyncInvocationHistoryDao") InvocationHistoryDao dao, EventListenerRegistrar eventListenerRegistrar, SchedulerService schedulerService, TransactionTemplate txTemplate, WebhookService webhookService) {
        this.dao = dao;
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.schedulerService = schedulerService;
        this.txTemplate = txTemplate;
        this.webhookService = webhookService;
    }

    @Nonnull
    public InvocationHistory get(@Nonnull InvocationHistoryRequest request) {
        int webhookId = request.getWebhookId();
        String eventId = request.getEventId().orElse(null);
        return this.getInvocationHistory(webhookId, eventId, null);
    }

    @Nonnull
    public Map<WebhookEvent, InvocationHistory> getByEvent(@Nonnull InvocationHistoryByEventRequest request) {
        return (Map)this.txTemplate.execute(() -> {
            int webhookId = request.getWebhookId();
            Webhook webhook = this.getWebhookOrThrow(webhookId);
            Set<String> eventIds = webhook.getEvents().stream().map(WebhookEvent::getId).collect(Collectors.toSet());
            Multimap<String, AoHistoricalInvocation> latestByEvent = this.dao.getLatestInvocationsByEvent(webhookId, eventIds);
            Map<String, InvocationCounts> countsByEvent = this.dao.getCountsByEvent(webhookId, latestByEvent.keySet(), 30);
            return webhook.getEvents().stream().collect(Collectors.toMap(Function.identity(), ev -> this.toInvocationHistory(latestByEvent.get((Object)ev.getId()), (InvocationCounts)countsByEvent.get(ev.getId()))));
        });
    }

    @Nonnull
    public Map<Integer, InvocationHistory> getByWebhook(Collection<Integer> webhookIds) {
        return this.getByWebhookForDays(webhookIds, 30);
    }

    @Nonnull
    public Map<Integer, InvocationHistory> getByWebhookForDays(Collection<Integer> webhookIds, int days) {
        return webhookIds.isEmpty() ? Collections.emptyMap() : (Map)this.txTemplate.execute(() -> {
            Multimap<Integer, AoHistoricalInvocation> latestByWebhookId = this.dao.getLatestInvocationsByWebhook(webhookIds);
            Map<Integer, InvocationCounts> countsByWebhook = this.dao.getCountsByWebhook(webhookIds, days);
            return webhookIds.stream().collect(Collectors.toMap(Function.identity(), id -> this.toInvocationHistory(latestByWebhookId.get(id), (InvocationCounts)countsByWebhook.get(id))));
        });
    }

    @Nonnull
    public Optional<DetailedInvocation> getLatestInvocation(@Nonnull HistoricalInvocationRequest request) {
        return (Optional)this.txTemplate.execute(() -> {
            String eventId;
            int webhookId = request.getWebhookId();
            AoHistoricalInvocation invocation = this.dao.getLatestInvocation(webhookId, eventId = (String)request.getEventId().orElse(null), request.getOutcomes());
            if (invocation != null) {
                return Optional.of(this.toDetailedInvocation(invocation));
            }
            this.getWebhookOrThrow(webhookId);
            return Optional.empty();
        });
    }

    @Override
    public void logInvocationError(@Nonnull WebhookHttpRequest request, @Nonnull Throwable error, @Nonnull WebhookInvocation invocation, @Nonnull Instant start, @Nonnull Instant finish) {
        SimpleDetailedInvocation detailedInvocation = new SimpleDetailedInvocation(invocation.getId(), invocation.getEvent(), (InvocationRequest)new SimpleDetailedRequest(invocation, request), this.getErrorInvocation(request, error), start, finish);
        this.saveInvocation(invocation.getWebhook().getId(), detailedInvocation, 1, 0, 0);
    }

    @Override
    public void logInvocationFailure(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation invocation, @Nonnull Instant start, @Nonnull Instant finish) {
        this.saveInvocation(invocation.getWebhook().getId(), new SimpleDetailedInvocation(invocation, InvocationOutcome.FAILURE, request, response, start, finish), 0, 1, 0);
    }

    @Override
    public void logInvocationSuccess(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation invocation, @Nonnull Instant start, @Nonnull Instant finish) {
        this.saveInvocation(invocation.getWebhook().getId(), new SimpleDetailedInvocation(invocation, InvocationOutcome.SUCCESS, request, response, start, finish), 0, 0, 1);
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        this.eventListenerRegistrar.register((Object)this);
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new DailyCleanupJobRunner());
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forCronExpression((String)"0 22 4 1/1 * ? *"));
        try {
            this.schedulerService.scheduleJob(JOB_ID, config);
        }
        catch (SchedulerServiceException e) {
            log.error("Could not schedule audit log cleanup job", (Throwable)e);
        }
    }

    @Override
    public void onStop() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onWebhookDeleted(@Nonnull WebhookDeletedEvent event) {
        Webhook webhook = event.getWebhook();
        this.dao.deleteForWebhook(webhook.getId());
        if (log.isDebugEnabled()) {
            log.debug("[{}{}] Deleted all invocation history for deleted webhook to '{}' (id={})", new Object[]{webhook.getScope().getType(), webhook.getScope().getId().map(id -> ":" + id).orElse(""), webhook.getUrl(), webhook.getId()});
        }
    }

    private InvocationRequest createRequest(AoHistoricalInvocation invocation) {
        return new SimpleDetailedRequest(invocation.getRequestBody(), this.dao.decodeHeaders(invocation.getId(), invocation.getRequestHeaders()), Method.valueOf((String)invocation.getRequestMethod()), invocation.getRequestUrl());
    }

    private InvocationHistory getInvocationHistory(int webhookId, String eventId, Collection<InvocationOutcome> outcomes) {
        List<AoHistoricalInvocation> invocations = this.dao.getLatestInvocations(webhookId, eventId, outcomes);
        if (invocations.isEmpty()) {
            this.getWebhookOrThrow(webhookId);
            return EMPTY_HISTORY;
        }
        InvocationCounts counts = this.dao.getCounts(webhookId, eventId, 30);
        return this.toInvocationHistory(invocations, counts);
    }

    private Webhook getWebhookOrThrow(int webhookId) {
        return (Webhook)this.webhookService.findById(webhookId).orElseThrow(() -> new NoSuchWebhookException("Webhook with ID " + webhookId + " not found"));
    }

    private InvocationResult getErrorInvocation(WebhookHttpRequest request, Throwable error) {
        return new SimpleDetailedError(error.getClass().getName(), this.getErrorMessage(request.getUrl(), error));
    }

    private String getErrorMessage(String url, Throwable error) {
        if (error.getCause() instanceof UnknownHostException) {
            return String.format("Unknown host specified in the webhook URL: %s", url);
        }
        if (error.getCause() instanceof SocketException) {
            return "Unable to connect to the URL specified within the timeout, please check the host and port are correct and that the URL is accessible from the server running this request.";
        }
        if (error.getCause() instanceof SSLException) {
            return "An SSL error occurred, please check that the provided resource uses https.";
        }
        log.warn("Request to {} resulted in an error: {}", (Object)url, (Object)error.getLocalizedMessage());
        return "The request to the specified URL failed. For more details, please ask the Administrator to check the server logs.";
    }

    private HistoricalInvocation getInvocation(Collection<AoHistoricalInvocation> invocations, InvocationOutcome outcome) {
        HistoricalInvocation result = null;
        for (AoHistoricalInvocation invocation : invocations) {
            if (invocation.getOutcome() != outcome || result != null && !result.getFinish().isBefore(Instant.ofEpochMilli(invocation.getFinish()))) continue;
            result = this.toInvocation(invocation);
        }
        return result;
    }

    private void saveInvocation(int webhookId, DetailedInvocation invocation, int errors, int failures, int successes) {
        try {
            this.txTemplate.execute(() -> {
                this.dao.saveInvocation(webhookId, invocation);
                return null;
            });
        }
        catch (RuntimeException e) {
            log.warn("Failed to record history for webhook {}/{} and event {}", new Object[]{webhookId, invocation.getRequest().getUrl(), invocation.getEvent().getId(), e});
        }
        for (int attempt = 1; attempt <= 3; ++attempt) {
            try {
                this.txTemplate.execute(() -> {
                    this.dao.addCounts(webhookId, invocation.getEvent().getId(), new Date(invocation.getFinish().toEpochMilli()), errors, failures, successes);
                    return null;
                });
                break;
            }
            catch (RuntimeException e) {
                if (attempt == 3) {
                    throw e;
                }
                log.debug("Update of invocation counts for {}:{} failed. Retrying", new Object[]{webhookId, invocation.getEvent().getId(), e});
                continue;
            }
        }
    }

    private DetailedInvocation toDetailedInvocation(AoHistoricalInvocation invocation) {
        Object result = invocation.getOutcome() == InvocationOutcome.ERROR ? new SimpleDetailedError(invocation.getErrorContent(), invocation.getResultDescription()) : new SimpleDetailedResponse(invocation.getResponseBody(), invocation.getResultDescription(), this.dao.decodeHeaders(invocation.getId(), invocation.getResponseHeaders()), invocation.getOutcome(), invocation.getStatusCode());
        return new SimpleDetailedInvocation(invocation.getRequestId(), this.toEvent(invocation.getEventId()), this.createRequest(invocation), (InvocationResult)result, Instant.ofEpochMilli(invocation.getStart()), Instant.ofEpochMilli(invocation.getFinish()));
    }

    private WebhookEvent toEvent(String eventId) {
        return this.webhookService.getEvent(eventId).orElseGet(() -> new UnknownWebhookEvent(eventId));
    }

    private HistoricalInvocation toInvocation(AoHistoricalInvocation invocation) {
        return new SimpleHistoricalInvocation(invocation.getRequestId(), this.toEvent(invocation.getEventId()), Instant.ofEpochMilli(invocation.getStart()), Instant.ofEpochMilli(invocation.getFinish()), new SimpleInvocationRequest(Method.valueOf((String)invocation.getRequestMethod()), invocation.getRequestUrl()), new SimpleInvocationResult(invocation.getResultDescription(), invocation.getOutcome()));
    }

    private InvocationHistory toInvocationHistory(Collection<AoHistoricalInvocation> invocations, InvocationCounts counts) {
        return new SimpleInvocationHistory((InvocationCounts)MoreObjects.firstNonNull((Object)counts, (Object)NO_INVOCATIONS), this.getInvocation(invocations, InvocationOutcome.ERROR), this.getInvocation(invocations, InvocationOutcome.FAILURE), this.getInvocation(invocations, InvocationOutcome.SUCCESS));
    }

    private class DailyCleanupJobRunner
    implements JobRunner {
        private DailyCleanupJobRunner() {
        }

        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            int rowsDeleted = (Integer)DefaultInvocationHistoryService.this.txTemplate.execute(() -> DefaultInvocationHistoryService.this.dao.deleteDailyCountsOlderThan(30));
            log.debug("Deleted {} rows of webhooks daily invocation counts", (Object)rowsDeleted);
            return JobRunnerResponse.success();
        }
    }
}

