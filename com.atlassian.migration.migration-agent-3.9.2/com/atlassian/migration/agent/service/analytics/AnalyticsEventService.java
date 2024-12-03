/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.analytics.events.EventType
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.analytics.events.EventType;
import com.atlassian.migration.agent.common.Sink;
import com.atlassian.migration.agent.entity.AnalyticsEvent;
import com.atlassian.migration.agent.entity.AnalyticsEventType;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextAwareExecutorService;
import com.atlassian.migration.agent.service.analytics.AnalyticsSenderService;
import com.atlassian.migration.agent.service.analytics.ProcessedAnalyticsEvents;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.store.impl.AnalyticsEventStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;

public class AnalyticsEventService
implements Sink<EventDto> {
    private static final Logger log = ContextLoggerFactory.getLogger(AnalyticsEventService.class);
    private final PluginTransactionTemplate ptx;
    private final AnalyticsEventStore analyticsEventStore;
    private final ExecutorService executorService;
    private final CloudSiteService cloudSiteService;
    private final AnalyticsSenderService analyticsSenderService;

    public AnalyticsEventService(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, CloudSiteService cloudSiteService, AnalyticsSenderService analyticsSenderService) {
        this(ptx, analyticsEventStore, new LoggingContextAwareExecutorService(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)AnalyticsEventService.class.getName()))), cloudSiteService, analyticsSenderService);
    }

    @VisibleForTesting
    AnalyticsEventService(PluginTransactionTemplate ptx, AnalyticsEventStore analyticsEventStore, ExecutorService executorService, CloudSiteService cloudSiteService, AnalyticsSenderService analyticsSenderService) {
        this.ptx = ptx;
        this.analyticsEventStore = analyticsEventStore;
        this.executorService = executorService;
        this.cloudSiteService = cloudSiteService;
        this.analyticsSenderService = analyticsSenderService;
    }

    @PreDestroy
    public void cleanup() {
        this.executorService.shutdownNow();
    }

    public void saveAnalyticsEvent(EventDto event) {
        this.storeAnalyticsEvent(event);
    }

    public void saveAnalyticsEventAsync(Supplier<EventDto> eventSupplier) {
        this.saveAnalyticsEvents(() -> Collections.singletonList(eventSupplier.get()));
    }

    public void saveAnalyticsEvents(Supplier<Collection<? extends EventDto>> eventSupplier) {
        this.executorService.execute(() -> {
            Collection analyticsEvents = (Collection)eventSupplier.get();
            analyticsEvents.forEach(this::storeAnalyticsEvent);
        });
    }

    public void saveAnalyticsEvents(List<AnalyticsEvent> analyticsEvents) {
        this.executorService.execute(() -> analyticsEvents.forEach(analyticsEvent -> this.ptx.write(() -> this.analyticsEventStore.createAnalyticsEvent((AnalyticsEvent)analyticsEvent))));
    }

    public void sendAnalyticsEvent(Supplier<EventDto> eventSupplier) {
        this.sendAnalyticsEvents(() -> Collections.singletonList(eventSupplier.get()));
    }

    public void sendAnalyticsEvents(Supplier<Collection<? extends EventDto>> eventSupplier) {
        this.sendOrSaveAnalyticsEvent(eventSupplier);
    }

    public void sendAnalyticsEventsAsync(Supplier<Collection<? extends EventDto>> eventSupplier) {
        this.executorService.execute(() -> this.sendAnalyticsEvents(eventSupplier));
    }

    private void sendOrSaveAnalyticsEvent(Supplier<Collection<? extends EventDto>> eventSupplier) {
        try {
            Optional<String> token = this.cloudSiteService.getNonFailingToken();
            Collection<? extends EventDto> analyticsEvents = eventSupplier.get();
            List<AnalyticsEvent> batch = analyticsEvents.stream().map(this::convertToEntity).collect(Collectors.toList());
            if (!token.isPresent()) {
                log.warn("Could not be sent in real time to migration-analytics service. They'll be locally preserved for a future retry.");
                this.saveAnalyticsEvents(eventSupplier);
            } else {
                ProcessedAnalyticsEvents sentAnalyticsEvents = this.analyticsSenderService.processAndSendAnalyticsEvents(token.get(), batch);
                List<AnalyticsEvent> unsuccessfullySentEvents = sentAnalyticsEvents.getUnsuccessfullySentEvents();
                if (!unsuccessfullySentEvents.isEmpty()) {
                    log.warn("Could not be sent in real time to migration-analytics service. They'll be locally preserved for a future retry.");
                    this.saveAnalyticsEvents(unsuccessfullySentEvents);
                }
            }
        }
        catch (Exception exception) {
            log.warn("Migration analytics actively refused events. They'll be locally preserved for a future retry.");
            this.saveAnalyticsEvents(eventSupplier);
        }
    }

    private void storeAnalyticsEvent(EventDto analyticsEvent) {
        this.ptx.write(() -> this.analyticsEventStore.createAnalyticsEvent(this.convertToEntity(analyticsEvent)));
    }

    private AnalyticsEvent convertToEntity(EventDto event) {
        AnalyticsEvent analyticsEvent = new AnalyticsEvent();
        analyticsEvent.setTimestamp(event.timestamp);
        analyticsEvent.setEventType(AnalyticsEventService.toInternalEventType(event.eventType));
        analyticsEvent.setEvent(Jsons.valueAsString(event));
        return analyticsEvent;
    }

    private static AnalyticsEventType toInternalEventType(EventType eventType) {
        switch (eventType) {
            case UI: {
                return AnalyticsEventType.UI;
            }
            case TRACK: {
                return AnalyticsEventType.TRACK;
            }
            case SCREEN: {
                return AnalyticsEventType.SCREEN;
            }
            case OPERATIONAL: {
                return AnalyticsEventType.OPERATIONAL;
            }
            case METRIC: {
                return AnalyticsEventType.METRIC;
            }
        }
        throw new IllegalArgumentException(String.format("EventType %s is not supported", eventType));
    }

    @Override
    public void put(EventDto data) {
        this.saveAnalyticsEvent(data);
    }
}

