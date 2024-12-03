/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.webhooks.AbstractBulkWebhookRequest
 *  com.atlassian.webhooks.NoSuchWebhookException
 *  com.atlassian.webhooks.PingRequest
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookCallback
 *  com.atlassian.webhooks.WebhookCreateRequest
 *  com.atlassian.webhooks.WebhookDeleteRequest
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookFilter
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  com.atlassian.webhooks.WebhookPublishRequest
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.WebhookSearchRequest
 *  com.atlassian.webhooks.WebhookSearchRequest$Builder
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.WebhookStatistics
 *  com.atlassian.webhooks.WebhookUpdateRequest
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.atlassian.webhooks.WebhooksNotInitializedException
 *  com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent
 *  com.atlassian.webhooks.diagnostics.WebhookDiagnosticsResult
 *  com.atlassian.webhooks.event.WebhookCreatedEvent
 *  com.atlassian.webhooks.event.WebhookDeletedEvent
 *  com.atlassian.webhooks.event.WebhookModifiedEvent
 *  com.atlassian.webhooks.module.WebhookModuleDescriptor
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.webhooks.AbstractBulkWebhookRequest;
import com.atlassian.webhooks.NoSuchWebhookException;
import com.atlassian.webhooks.PingRequest;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookCreateRequest;
import com.atlassian.webhooks.WebhookDeleteRequest;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookFilter;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import com.atlassian.webhooks.WebhookPublishRequest;
import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.WebhookSearchRequest;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.WebhookStatistics;
import com.atlassian.webhooks.WebhookUpdateRequest;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.WebhooksNotInitializedException;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsEvent;
import com.atlassian.webhooks.diagnostics.WebhookDiagnosticsResult;
import com.atlassian.webhooks.event.WebhookCreatedEvent;
import com.atlassian.webhooks.event.WebhookDeletedEvent;
import com.atlassian.webhooks.event.WebhookModifiedEvent;
import com.atlassian.webhooks.internal.SimpleWebhooksStatistics;
import com.atlassian.webhooks.internal.Validator;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.WebhookPayloadManager;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.concurrent.BackPressureBlockingQueue;
import com.atlassian.webhooks.internal.dao.WebhookDao;
import com.atlassian.webhooks.internal.dao.ao.AoWebhook;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookConfigurationEntry;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookEvent;
import com.atlassian.webhooks.internal.model.SimpleWebhook;
import com.atlassian.webhooks.internal.model.SimpleWebhookScope;
import com.atlassian.webhooks.internal.model.UnknownWebhookEvent;
import com.atlassian.webhooks.internal.publish.DefaultWebhookInvocation;
import com.atlassian.webhooks.internal.publish.InternalWebhookInvocation;
import com.atlassian.webhooks.internal.publish.WebhookDispatcher;
import com.atlassian.webhooks.module.WebhookModuleDescriptor;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebhookService
implements WebhookService,
WebhooksLifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebhookService.class);
    private final WebhookDao dao;
    private final WebhookDispatcher dispatcher;
    private final EventPublisher eventPublisher;
    private final WebhookHostAccessor hostAccessor;
    private final PluginAccessor pluginAccessor;
    private final TransactionTemplate txTemplate;
    private final Validator validator;
    private final WebhookPayloadManager webhookPayloadManager;
    private volatile ThreadPoolExecutor publishExecutor;
    private volatile SimpleWebhooksStatistics statistics;

    public DefaultWebhookService(WebhookDao dao, WebhookDispatcher dispatcher, EventPublisher eventPublisher, WebhookHostAccessor hostAccessor, PluginAccessor pluginAccessor, TransactionTemplate txTemplate, Validator validator, WebhookPayloadManager webhookPayloadManager) {
        this.dao = dao;
        this.dispatcher = dispatcher;
        this.hostAccessor = hostAccessor;
        this.eventPublisher = eventPublisher;
        this.pluginAccessor = pluginAccessor;
        this.txTemplate = txTemplate;
        this.validator = validator;
        this.webhookPayloadManager = webhookPayloadManager;
    }

    @Nonnull
    public Webhook create(@Nonnull WebhookCreateRequest request) {
        Objects.requireNonNull(request, "request");
        this.validator.validate(request);
        return (Webhook)this.txTemplate.execute(() -> {
            AoWebhook aoWebhook = this.dao.create(request);
            Webhook webhook = this.convert(aoWebhook);
            this.eventPublisher.publish((Object)new WebhookCreatedEvent((Object)this, webhook));
            return webhook;
        });
    }

    public boolean delete(int id) {
        return (Boolean)this.txTemplate.execute(() -> {
            AoWebhook aoWebhook = this.dao.getById(id);
            if (aoWebhook == null) {
                return false;
            }
            Webhook webhook = this.convert(aoWebhook);
            this.dao.delete(new AoWebhook[]{aoWebhook});
            this.eventPublisher.publish((Object)new WebhookDeletedEvent((Object)this, webhook));
            return true;
        });
    }

    public int delete(@Nonnull WebhookDeleteRequest request) {
        Objects.requireNonNull(request, "request");
        WebhookSearchRequest search = WebhookSearchRequest.builder((AbstractBulkWebhookRequest)request).build();
        return (Integer)this.txTemplate.execute(() -> {
            AoWebhook[] searchResults;
            int deleted = 0;
            do {
                searchResults = this.dao.search(search);
                this.dao.delete(searchResults);
                Arrays.stream(searchResults).forEach(aoWebhook -> {
                    Webhook webhook = this.convert((AoWebhook)aoWebhook);
                    this.eventPublisher.publish((Object)new WebhookDeletedEvent((Object)this, webhook));
                });
                deleted += searchResults.length;
            } while (searchResults.length >= search.getLimit());
            return deleted;
        });
    }

    @Nonnull
    public Optional<Webhook> findById(int id) {
        return (Optional)this.txTemplate.execute(() -> Optional.ofNullable(this.dao.getById(id)).map(this::convert));
    }

    @Nonnull
    public Optional<WebhookEvent> getEvent(@Nonnull String eventId) {
        WebhookEvent event = this.hostAccessor.getEvent(eventId);
        if (event instanceof UnknownWebhookEvent) {
            return Optional.empty();
        }
        return Optional.of(event);
    }

    @Nonnull
    public List<WebhookEvent> getEvents() {
        return this.hostAccessor.getEvents();
    }

    @Nonnull
    public Optional<WebhookStatistics> getStatistics() {
        return Optional.ofNullable(this.statistics);
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        ThreadPoolExecutor executor;
        if (configuration.isStatisticsEnabled()) {
            this.statistics = new SimpleWebhooksStatistics();
        }
        if ((executor = this.publishExecutor) != null) {
            executor.shutdown();
        }
        this.publishExecutor = new ThreadPoolExecutor(5, 5, 5L, TimeUnit.SECONDS, new BackPressureBlockingQueue<Runnable>(configuration.getDispatchQueueSize()), ThreadFactories.namedThreadFactory((String)"webhook-dispatcher", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    }

    @Override
    public void onStop() {
        ThreadPoolExecutor executor = this.publishExecutor;
        this.publishExecutor = null;
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Nonnull
    public Future<WebhookDiagnosticsResult> ping(@Nonnull PingRequest request) {
        Objects.requireNonNull(request, "request");
        this.validator.validate(request);
        Webhook webhook = SimpleWebhook.builder().event((WebhookEvent)WebhookDiagnosticsEvent.PING, new WebhookEvent[0]).url(request.getUrl()).scope(request.getScope()).build();
        WebhookPublishRequest publishRequest = WebhookPublishRequest.builder((Webhook)webhook, (WebhookEvent)WebhookDiagnosticsEvent.PING, null).build();
        InternalWebhookInvocation invocation = this.createSingleInvocationFor(webhook, publishRequest);
        this.enrich(invocation);
        return this.dispatchForResult(invocation);
    }

    public void publish(@Nonnull WebhookPublishRequest request) {
        Objects.requireNonNull(request, "request");
        this.validator.validate(request);
        String debugIdentifier = this.getDebugString();
        log.trace("Adding to webhook service dispatch queue with queue id [{}]", (Object)debugIdentifier);
        this.getExecutorOrThrow().execute(() -> {
            try {
                log.trace("Webhook has started execution for debug queue id [{}]", (Object)debugIdentifier);
                this.incrementPublishCount();
                List<InternalWebhookInvocation> webhookInvocations = this.createInvocationsFor(request);
                Collection<WebhookFilter> filters = this.hostAccessor.getFilters();
                webhookInvocations.stream().filter(invocation -> {
                    boolean filterResult = filters.stream().allMatch(filter -> {
                        boolean individualFilter = filter.filter((WebhookInvocation)invocation);
                        if (log.isTraceEnabled()) {
                            log.trace("Filter [{}] has completed with result [{}] for invocation [{}]", new Object[]{filter.getClass().getSimpleName(), individualFilter, invocation.getId()});
                        }
                        return individualFilter;
                    });
                    log.debug("The overall result of the filter was [{}] for invocation [{}]", (Object)filterResult, (Object)invocation.getId());
                    return filterResult;
                }).forEach(filteredInvocation -> {
                    this.enrich((WebhookInvocation)filteredInvocation);
                    this.dispatcher.dispatch((InternalWebhookInvocation)filteredInvocation);
                });
            }
            catch (Exception e) {
                log.info("An error occurred while attempting to publish webhooks for queue id [{}]", (Object)debugIdentifier, (Object)(log.isDebugEnabled() ? e : null));
            }
        });
    }

    private void enrich(WebhookInvocation invocation) {
        for (WebhookRequestEnricher enricher : this.hostAccessor.getEnrichers()) {
            try {
                enricher.enrich(invocation);
            }
            catch (Exception e) {
                log.info("Webhook enricher [{}] has failed with an error for invocation [{}]", new Object[]{enricher.getClass().getSimpleName(), invocation.getId(), log.isDebugEnabled() ? e : null});
            }
        }
    }

    @Nonnull
    public List<Webhook> search(@Nonnull WebhookSearchRequest request) {
        Objects.requireNonNull(request, "request");
        return (List)this.txTemplate.execute(() -> Arrays.stream(this.dao.search(request)).map(this::convert).collect(Collectors.toList()));
    }

    public void setStatisticsEnabled(boolean enabled) {
        if (enabled) {
            if (this.statistics == null) {
                this.statistics = new SimpleWebhooksStatistics();
            }
        } else {
            this.statistics = null;
        }
    }

    @Nonnull
    public Webhook update(int id, @Nonnull WebhookUpdateRequest request) {
        Objects.requireNonNull(request, "request");
        this.validator.validate(request);
        return (Webhook)this.txTemplate.execute(() -> {
            AoWebhook aoCurrent = this.dao.getById(id);
            if (aoCurrent == null) {
                throw new NoSuchWebhookException("Webhook with ID " + id + " does not exist");
            }
            AoWebhook aoUpdated = this.dao.update(id, request);
            if (aoUpdated == null) {
                throw new NoSuchWebhookException("Webhook with ID " + id + " does not exist");
            }
            Webhook current = this.convert(aoCurrent);
            Webhook updated = this.convert(aoUpdated);
            this.eventPublisher.publish((Object)new WebhookModifiedEvent((Object)this, current, updated));
            return updated;
        });
    }

    @VisibleForTesting
    protected Executor getExecutorOrThrow() {
        ThreadPoolExecutor executor = this.publishExecutor;
        if (executor != null) {
            return executor;
        }
        throw new WebhooksNotInitializedException("The webhooks plugin hasn't been initialized yet. Webhook will not be published.");
    }

    private Webhook convert(AoWebhook aoWebhook) {
        return SimpleWebhook.builder().id(aoWebhook.getID()).active(aoWebhook.isActive()).configuration(this.getContext(aoWebhook)).event(this.getEvents(aoWebhook.getEvents())).name(aoWebhook.getName()).scope(this.getScope(aoWebhook)).url(aoWebhook.getUrl()).build();
    }

    private List<InternalWebhookInvocation> createInvocationsFor(WebhookPublishRequest request) {
        if (request.getWebhook().isPresent()) {
            return Collections.singletonList(this.createSingleInvocationFor((Webhook)request.getWebhook().get(), request));
        }
        WebhookEvent event = request.getEvent();
        HashSet<WebhookScope> scopes = new HashSet<WebhookScope>(request.getScopes());
        scopes.add(WebhookScope.GLOBAL);
        ArrayList<InternalWebhookInvocation> invocations = new ArrayList<InternalWebhookInvocation>();
        Stream<Webhook> pluginWebhooks = this.pluginAccessor.getEnabledModuleDescriptorsByClass(WebhookModuleDescriptor.class).stream().map(ModuleDescriptor::getModule).filter(webhook -> webhook.getEvents().stream().anyMatch(e -> e.getId().equals(event.getId())) && scopes.contains(webhook.getScope()) && webhook.isActive());
        Stream subscribedWebhooks = this.search(((WebhookSearchRequest.Builder)((WebhookSearchRequest.Builder)((WebhookSearchRequest.Builder)WebhookSearchRequest.builder().active(true)).event(event, new WebhookEvent[0])).scope(scopes)).build()).stream();
        Stream.concat(pluginWebhooks, subscribedWebhooks).map(hook -> this.createSingleInvocationFor((Webhook)hook, request)).forEach(invocations::add);
        return invocations;
    }

    private InternalWebhookInvocation createSingleInvocationFor(Webhook hook, WebhookPublishRequest request) {
        DefaultWebhookInvocation invocation = new DefaultWebhookInvocation(hook, request);
        log.trace("A new webhook invocation has been created for webhook [{}], invocation [{}]", (Object)hook.getId(), (Object)invocation.getId());
        WebhookPayloadBuilder payloadBuilder = invocation.getRequestBuilder().asPayloadBuilder();
        this.webhookPayloadManager.setPayload(invocation, payloadBuilder);
        this.maybeRegisterStatisticsCallback(invocation);
        return invocation;
    }

    private Future<WebhookDiagnosticsResult> dispatchForResult(InternalWebhookInvocation invocation) {
        final CompletableFuture<WebhookDiagnosticsResult> result = new CompletableFuture<WebhookDiagnosticsResult>();
        invocation.registerCallback(new WebhookCallback(){

            public void onError(WebhookHttpRequest request, @Nonnull Throwable error, @Nonnull WebhookInvocation webhook) {
                result.complete(WebhookDiagnosticsResult.build((WebhookHttpRequest)request, (Throwable)error));
            }

            public void onFailure(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation webhook) {
                result.complete(WebhookDiagnosticsResult.build((WebhookHttpRequest)request, (WebhookHttpResponse)response));
            }

            public void onSuccess(@Nonnull WebhookHttpRequest request, @Nonnull WebhookHttpResponse response, @Nonnull WebhookInvocation webhook) {
                result.complete(WebhookDiagnosticsResult.build((WebhookHttpRequest)request, (WebhookHttpResponse)response));
            }
        });
        this.dispatcher.dispatch(invocation);
        return result;
    }

    private Map<String, String> getContext(AoWebhook aoWebhook) {
        return Arrays.stream(aoWebhook.getConfiguration()).collect(Collectors.toMap(AoWebhookConfigurationEntry::getKey, AoWebhookConfigurationEntry::getValue));
    }

    private String getDebugString() {
        if (log.isTraceEnabled()) {
            return UUID.randomUUID().toString();
        }
        return "";
    }

    private List<WebhookEvent> getEvents(AoWebhookEvent[] events) {
        return Arrays.stream(events).map(AoWebhookEvent::getEventId).map(this.hostAccessor::getEvent).collect(Collectors.toList());
    }

    private WebhookScope getScope(AoWebhook webhook) {
        return new SimpleWebhookScope(webhook.getScopeType(), webhook.getScopeId());
    }

    private void incrementPublishCount() {
        SimpleWebhooksStatistics stats = this.statistics;
        if (stats != null) {
            stats.onPublish();
        }
    }

    private void maybeRegisterStatisticsCallback(InternalWebhookInvocation invocation) {
        SimpleWebhooksStatistics stats = this.statistics;
        if (stats != null) {
            invocation.registerCallback(stats.asCallback());
        }
    }
}

