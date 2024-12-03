/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.Level
 *  ch.qos.logback.classic.Logger
 *  ch.qos.logback.classic.LoggerContext
 *  ch.qos.logback.classic.spi.LoggerContextListener
 *  ch.qos.logback.classic.turbo.TurboFilter
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.slf4j.LoggerFactory
 */
package io.micrometer.core.instrument.binder.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.turbo.TurboFilter;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.logging.MetricsTurboFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

@NonNullApi
@NonNullFields
public class LogbackMetrics
implements MeterBinder,
AutoCloseable {
    static ThreadLocal<Boolean> ignoreMetrics = new ThreadLocal();
    private final Iterable<Tag> tags;
    private final LoggerContext loggerContext;
    private final Map<MeterRegistry, MetricsTurboFilter> metricsTurboFilters = new HashMap<MeterRegistry, MetricsTurboFilter>();

    public LogbackMetrics() {
        this(Collections.emptyList());
    }

    public LogbackMetrics(Iterable<Tag> tags) {
        this(tags, (LoggerContext)LoggerFactory.getILoggerFactory());
    }

    public LogbackMetrics(Iterable<Tag> tags, LoggerContext context) {
        this.tags = tags;
        this.loggerContext = context;
        this.loggerContext.addListener(new LoggerContextListener(){

            public boolean isResetResistant() {
                return true;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void onReset(LoggerContext context) {
                Map map = LogbackMetrics.this.metricsTurboFilters;
                synchronized (map) {
                    for (MetricsTurboFilter metricsTurboFilter : LogbackMetrics.this.metricsTurboFilters.values()) {
                        LogbackMetrics.this.loggerContext.addTurboFilter((TurboFilter)metricsTurboFilter);
                    }
                }
            }

            public void onStart(LoggerContext context) {
            }

            public void onStop(LoggerContext context) {
            }

            public void onLevelChange(Logger logger, Level level) {
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void bindTo(MeterRegistry registry) {
        MetricsTurboFilter filter = new MetricsTurboFilter(registry, this.tags);
        Map<MeterRegistry, MetricsTurboFilter> map = this.metricsTurboFilters;
        synchronized (map) {
            this.metricsTurboFilters.put(registry, filter);
            this.loggerContext.addTurboFilter((TurboFilter)filter);
        }
    }

    public static void ignoreMetrics(Runnable r) {
        ignoreMetrics.set(true);
        try {
            r.run();
        }
        finally {
            ignoreMetrics.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        Map<MeterRegistry, MetricsTurboFilter> map = this.metricsTurboFilters;
        synchronized (map) {
            for (MetricsTurboFilter metricsTurboFilter : this.metricsTurboFilters.values()) {
                this.loggerContext.getTurboFilterList().remove((Object)metricsTurboFilter);
            }
        }
    }

    static {
        LoggerFactory.getILoggerFactory();
    }
}

