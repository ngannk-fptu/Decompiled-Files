/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.AbstractLifeCycle
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Filter$Result
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.async.AsyncLoggerConfig
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.LoggerConfig
 *  org.apache.logging.log4j.core.filter.AbstractFilter
 *  org.apache.logging.log4j.core.filter.CompositeFilter
 */
package io.micrometer.core.instrument.binder.logging;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.filter.CompositeFilter;

@NonNullApi
@NonNullFields
public class Log4j2Metrics
implements MeterBinder,
AutoCloseable {
    private static final String METER_NAME = "log4j2.events";
    private final Iterable<Tag> tags;
    private final LoggerContext loggerContext;
    private List<MetricsFilter> metricsFilters = new ArrayList<MetricsFilter>();

    public Log4j2Metrics() {
        this(Collections.emptyList());
    }

    public Log4j2Metrics(Iterable<Tag> tags) {
        this(tags, (LoggerContext)LogManager.getContext((boolean)false));
    }

    public Log4j2Metrics(Iterable<Tag> tags, LoggerContext loggerContext) {
        this.tags = tags;
        this.loggerContext = loggerContext;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Configuration configuration = this.loggerContext.getConfiguration();
        LoggerConfig rootLoggerConfig = configuration.getRootLogger();
        rootLoggerConfig.addFilter((Filter)this.createMetricsFilterAndStart(registry, rootLoggerConfig));
        this.loggerContext.getConfiguration().getLoggers().values().stream().filter(loggerConfig -> !loggerConfig.isAdditive()).forEach(loggerConfig -> {
            if (loggerConfig == rootLoggerConfig) {
                return;
            }
            Filter logFilter = loggerConfig.getFilter();
            if (logFilter instanceof CompositeFilter && Arrays.stream(((CompositeFilter)logFilter).getFiltersArray()).anyMatch(innerFilter -> innerFilter instanceof MetricsFilter)) {
                return;
            }
            if (logFilter instanceof MetricsFilter) {
                return;
            }
            loggerConfig.addFilter((Filter)this.createMetricsFilterAndStart(registry, (LoggerConfig)loggerConfig));
        });
        this.loggerContext.updateLoggers(configuration);
    }

    private MetricsFilter createMetricsFilterAndStart(MeterRegistry registry, LoggerConfig loggerConfig) {
        MetricsFilter metricsFilter = new MetricsFilter(registry, this.tags, loggerConfig instanceof AsyncLoggerConfig);
        metricsFilter.start();
        this.metricsFilters.add(metricsFilter);
        return metricsFilter;
    }

    @Override
    public void close() {
        if (!this.metricsFilters.isEmpty()) {
            Configuration configuration = this.loggerContext.getConfiguration();
            LoggerConfig rootLoggerConfig = configuration.getRootLogger();
            this.metricsFilters.forEach(arg_0 -> ((LoggerConfig)rootLoggerConfig).removeFilter(arg_0));
            this.loggerContext.getConfiguration().getLoggers().values().stream().filter(loggerConfig -> !loggerConfig.isAdditive()).forEach(loggerConfig -> {
                if (loggerConfig != rootLoggerConfig) {
                    this.metricsFilters.forEach(arg_0 -> ((LoggerConfig)loggerConfig).removeFilter(arg_0));
                }
            });
            this.loggerContext.updateLoggers(configuration);
            this.metricsFilters.forEach(AbstractLifeCycle::stop);
        }
    }

    @NonNullApi
    @NonNullFields
    class MetricsFilter
    extends AbstractFilter {
        private final Counter fatalCounter;
        private final Counter errorCounter;
        private final Counter warnCounter;
        private final Counter infoCounter;
        private final Counter debugCounter;
        private final Counter traceCounter;
        private final boolean isAsyncLogger;

        MetricsFilter(MeterRegistry registry, Iterable<Tag> tags, boolean isAsyncLogger) {
            this.isAsyncLogger = isAsyncLogger;
            this.fatalCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "fatal").description("Number of fatal level log events").baseUnit("events").register(registry);
            this.errorCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "error").description("Number of error level log events").baseUnit("events").register(registry);
            this.warnCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "warn").description("Number of warn level log events").baseUnit("events").register(registry);
            this.infoCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "info").description("Number of info level log events").baseUnit("events").register(registry);
            this.debugCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "debug").description("Number of debug level log events").baseUnit("events").register(registry);
            this.traceCounter = Counter.builder(Log4j2Metrics.METER_NAME).tags(tags).tags("level", "trace").description("Number of trace level log events").baseUnit("events").register(registry);
        }

        public Filter.Result filter(LogEvent event) {
            if (!this.isAsyncLogger || this.isAsyncLoggerAndEndOfBatch(event)) {
                this.incrementCounter(event);
            }
            return Filter.Result.NEUTRAL;
        }

        private boolean isAsyncLoggerAndEndOfBatch(LogEvent event) {
            return this.isAsyncLogger && event.isEndOfBatch();
        }

        private void incrementCounter(LogEvent event) {
            switch (event.getLevel().getStandardLevel()) {
                case FATAL: {
                    this.fatalCounter.increment();
                    break;
                }
                case ERROR: {
                    this.errorCounter.increment();
                    break;
                }
                case WARN: {
                    this.warnCounter.increment();
                    break;
                }
                case INFO: {
                    this.infoCounter.increment();
                    break;
                }
                case DEBUG: {
                    this.debugCounter.increment();
                    break;
                }
                case TRACE: {
                    this.traceCounter.increment();
                    break;
                }
            }
        }
    }
}

