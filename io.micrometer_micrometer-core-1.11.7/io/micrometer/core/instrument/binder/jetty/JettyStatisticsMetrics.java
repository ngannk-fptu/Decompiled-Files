/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.eclipse.jetty.server.handler.StatisticsHandler
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import org.eclipse.jetty.server.handler.StatisticsHandler;

@Deprecated
@NonNullApi
@NonNullFields
public class JettyStatisticsMetrics
implements MeterBinder {
    private final StatisticsHandler statisticsHandler;
    private Iterable<Tag> tags;

    public JettyStatisticsMetrics(StatisticsHandler statisticsHandler, Iterable<Tag> tags) {
        this.tags = tags;
        this.statisticsHandler = statisticsHandler;
    }

    public static void monitor(MeterRegistry meterRegistry, StatisticsHandler statisticsHandler, String ... tags) {
        JettyStatisticsMetrics.monitor(meterRegistry, statisticsHandler, Tags.of(tags));
    }

    public static void monitor(MeterRegistry meterRegistry, StatisticsHandler statisticsHandler, Iterable<Tag> tags) {
        new JettyStatisticsMetrics(statisticsHandler, tags).bindTo(meterRegistry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.bindTimer(registry, "jetty.requests", "Request duration", StatisticsHandler::getRequests, StatisticsHandler::getRequestTimeTotal);
        this.bindTimer(registry, "jetty.dispatched", "Dispatch duration", StatisticsHandler::getDispatched, StatisticsHandler::getDispatchedTimeTotal);
        this.bindCounter(registry, "jetty.async.requests", "Total number of async requests", StatisticsHandler::getAsyncRequests);
        this.bindCounter(registry, "jetty.async.dispatches", "Total number of requests that have been asynchronously dispatched", StatisticsHandler::getAsyncDispatches);
        this.bindCounter(registry, "jetty.async.expires", "Total number of async requests that have expired", StatisticsHandler::getExpires);
        FunctionCounter.builder("jetty.responses.size", this.statisticsHandler, StatisticsHandler::getResponsesBytesTotal).description("Total number of bytes across all responses").baseUnit("bytes").tags(this.tags).register(registry);
        this.bindGauge(registry, "jetty.requests.active", "Number of requests currently active", StatisticsHandler::getRequestsActive);
        this.bindGauge(registry, "jetty.dispatched.active", "Number of dispatches currently active", StatisticsHandler::getDispatchedActive);
        this.bindGauge(registry, "jetty.dispatched.active.max", "Maximum number of active dispatches being handled", StatisticsHandler::getDispatchedActiveMax);
        this.bindTimeGauge(registry, "jetty.dispatched.time.max", "Maximum time spent in dispatch handling", StatisticsHandler::getDispatchedTimeMax);
        this.bindGauge(registry, "jetty.async.requests.waiting", "Currently waiting async requests", StatisticsHandler::getAsyncRequestsWaiting);
        this.bindGauge(registry, "jetty.async.requests.waiting.max", "Maximum number of waiting async requests", StatisticsHandler::getAsyncRequestsWaitingMax);
        this.bindTimeGauge(registry, "jetty.request.time.max", "Maximum time spent handling requests", StatisticsHandler::getRequestTimeMax);
        this.bindTimeGauge(registry, "jetty.stats", "Time stats have been collected for", StatisticsHandler::getStatsOnMs);
        this.bindStatusCounters(registry);
    }

    private void bindStatusCounters(MeterRegistry registry) {
        this.buildStatusCounter(registry, "1xx", StatisticsHandler::getResponses1xx);
        this.buildStatusCounter(registry, "2xx", StatisticsHandler::getResponses2xx);
        this.buildStatusCounter(registry, "3xx", StatisticsHandler::getResponses3xx);
        this.buildStatusCounter(registry, "4xx", StatisticsHandler::getResponses4xx);
        this.buildStatusCounter(registry, "5xx", StatisticsHandler::getResponses5xx);
    }

    private void bindGauge(MeterRegistry registry, String name, String description, ToDoubleFunction<StatisticsHandler> valueFunction) {
        Gauge.builder(name, this.statisticsHandler, valueFunction).tags(this.tags).description(description).register(registry);
    }

    private void bindTimer(MeterRegistry registry, String name, String desc, ToLongFunction<StatisticsHandler> countFunc, ToDoubleFunction<StatisticsHandler> consumer) {
        FunctionTimer.builder(name, this.statisticsHandler, countFunc, consumer, TimeUnit.MILLISECONDS).tags(this.tags).description(desc).register(registry);
    }

    private void bindTimeGauge(MeterRegistry registry, String name, String desc, ToDoubleFunction<StatisticsHandler> consumer) {
        TimeGauge.builder(name, this.statisticsHandler, TimeUnit.MILLISECONDS, consumer).tags(this.tags).description(desc).register(registry);
    }

    private void bindCounter(MeterRegistry registry, String name, String desc, ToDoubleFunction<StatisticsHandler> consumer) {
        FunctionCounter.builder(name, this.statisticsHandler, consumer).tags(this.tags).description(desc).register(registry);
    }

    private void buildStatusCounter(MeterRegistry registry, String status, ToDoubleFunction<StatisticsHandler> consumer) {
        FunctionCounter.builder("jetty.responses", this.statisticsHandler, consumer).tags(this.tags).description("Number of requests with response status").tags("status", status).register(registry);
    }
}

