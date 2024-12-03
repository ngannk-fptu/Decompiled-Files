/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.profiling.StrategiesRegistry
 *  com.atlassian.util.profiling.micrometer.MicrometerStrategy
 *  com.atlassian.util.profiling.micrometer.analytics.AnalyticsMeterRegistry
 *  com.atlassian.util.profiling.micrometer.analytics.AnalyticsRegistryConfig
 *  com.atlassian.util.profiling.strategy.MetricStrategy
 *  com.atlassian.util.profiling.strategy.ProfilerStrategy
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.composite.CompositeMeterRegistry
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.metrics;

import com.atlassian.confluence.impl.metrics.ConfluenceMicrometer;
import com.atlassian.confluence.util.profiling.ConfluenceProfilerStrategy;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.profiling.StrategiesRegistry;
import com.atlassian.util.profiling.micrometer.MicrometerStrategy;
import com.atlassian.util.profiling.micrometer.analytics.AnalyticsMeterRegistry;
import com.atlassian.util.profiling.micrometer.analytics.AnalyticsRegistryConfig;
import com.atlassian.util.profiling.strategy.MetricStrategy;
import com.atlassian.util.profiling.strategy.ProfilerStrategy;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlassianProfilingRegistrar {
    private static final Logger log = LoggerFactory.getLogger(AtlassianProfilingRegistrar.class);
    private final ConfluenceProfilerStrategy profilerStrategy;
    private final EventPublisher eventPublisher;
    private final MeterRegistry micrometerRegistry;
    private volatile MeterRegistry analyticsMeterRegistry;

    public AtlassianProfilingRegistrar(ConfluenceProfilerStrategy profilerStrategy, EventPublisher eventPublisher, MeterRegistry meterRegistry) {
        this.eventPublisher = eventPublisher;
        this.micrometerRegistry = meterRegistry;
        this.profilerStrategy = profilerStrategy;
    }

    @PostConstruct
    public void registerMicrometer() {
        if (ConfluenceMicrometer.isMicrometerEnabled()) {
            this.analyticsMeterRegistry = new AnalyticsMeterRegistry(AnalyticsRegistryConfig.DEFAULT, this.eventPublisher);
            log.info("Integrating Micrometer with Atlassian Profiling");
            CompositeMeterRegistry composite = new CompositeMeterRegistry();
            composite.add(this.micrometerRegistry);
            composite.add(this.analyticsMeterRegistry);
            StrategiesRegistry.addMetricStrategy((MetricStrategy)new MicrometerStrategy((MeterRegistry)composite));
        }
    }

    @PostConstruct
    public void registerProfilerStrategy() {
        StrategiesRegistry.addProfilerStrategy((ProfilerStrategy)this.profilerStrategy);
    }

    @PreDestroy
    void close() {
        if (this.analyticsMeterRegistry != null) {
            this.analyticsMeterRegistry.close();
        }
    }
}

