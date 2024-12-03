/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Critical
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.pipeline;

import com.atlassian.analytics.api.annotations.Critical;
import com.atlassian.analytics.client.listener.SafeSalRequestProvider;
import com.atlassian.analytics.client.logger.AnalyticsLogger;
import com.atlassian.analytics.client.pipeline.AnalyticsPipeline;
import com.atlassian.analytics.client.pipeline.PipelineExecutionService;
import com.atlassian.analytics.client.pipeline.predicate.CanHandleEventPredicate;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessor;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.client.report.EventReporter;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.event.ProcessedEvent;
import com.atlassian.analytics.event.RawEvent;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAnalyticsPipeline
implements AnalyticsPipeline {
    private static final Logger log = LoggerFactory.getLogger(DefaultAnalyticsPipeline.class);
    private final SessionIdProvider sessionIdProvider;
    private final HttpContext httpContext;
    private final EventSerializer eventSerializer;
    private final EventPreprocessor eventPreprocessor;
    private final AnalyticsLogger analyticsLogger;
    private final EventReporter eventReporter;
    private final PipelineExecutionService eventProcessor;
    private final CanHandleEventPredicate canHandleEventPredicate;

    public DefaultAnalyticsPipeline(EventReporter eventReporter, EventSerializer eventSerializer, EventPreprocessor eventPreprocessor, SessionIdProvider sessionIdProvider, AnalyticsLogger analyticsLogger, HttpContext httpContext, PipelineExecutionService pipelineExecutionService, CanHandleEventPredicate canHandleEventPredicate) {
        this.eventReporter = eventReporter;
        this.analyticsLogger = analyticsLogger;
        this.eventSerializer = eventSerializer;
        this.sessionIdProvider = sessionIdProvider;
        this.eventPreprocessor = eventPreprocessor;
        this.httpContext = httpContext;
        this.eventProcessor = pipelineExecutionService;
        this.canHandleEventPredicate = canHandleEventPredicate;
    }

    @Override
    public boolean canHandle(Object event) {
        return this.canHandleEventPredicate.canHandleEvent(event);
    }

    @Override
    public void process(Object event) {
        this.processEventWithTiming(event);
    }

    private void processEventWithTiming(Object event) {
        RequestInfo requestInfo = RequestInfo.fromRequest(new SafeSalRequestProvider(this.httpContext).getHttpRequest());
        String sessionId = this.sessionIdProvider.getSessionId();
        Supplier<RawEvent> eventSupplier = this.eventSerializer.toAnalyticsEvent(event, sessionId, requestInfo);
        if (event.getClass().isAnnotationPresent(Critical.class)) {
            log.warn("Processing a critical event: {}", event);
            this.createTask(eventSupplier, event).run();
        } else {
            this.eventProcessor.submit(this.createTask(eventSupplier, event));
        }
    }

    private Runnable createTask(Supplier<RawEvent> eventSupplier, Object event) {
        Objects.requireNonNull(eventSupplier);
        return () -> {
            String name;
            long start = System.nanoTime();
            RawEvent rawEvent = null;
            try {
                rawEvent = (RawEvent)eventSupplier.get();
                if (this.eventPreprocessor.canCollect(rawEvent)) {
                    ProcessedEvent processedEvent = this.eventPreprocessor.preprocess(rawEvent);
                    this.analyticsLogger.logEvent(processedEvent);
                    this.eventReporter.addEvent(rawEvent, Optional.of(processedEvent));
                } else {
                    this.eventReporter.addEvent(rawEvent, Optional.empty());
                }
                name = rawEvent == null ? event.getClass().getSimpleName() : rawEvent.getName();
            }
            catch (Exception e) {
                String name2;
                try {
                    if (!e.getClass().getName().endsWith("ServiceProxyDestroyedException")) {
                        log.error("Failed to send analytics event " + event, (Throwable)e);
                    }
                    name2 = rawEvent == null ? event.getClass().getSimpleName() : rawEvent.getName();
                }
                catch (Throwable throwable) {
                    String name3 = rawEvent == null ? event.getClass().getSimpleName() : rawEvent.getName();
                    log.debug("Sending event message {} took {} \u00b5s", (Object)name3, (Object)TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - start));
                    throw throwable;
                }
                log.debug("Sending event message {} took {} \u00b5s", (Object)name2, (Object)TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - start));
            }
            log.debug("Sending event message {} took {} \u00b5s", (Object)name, (Object)TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - start));
        };
    }
}

