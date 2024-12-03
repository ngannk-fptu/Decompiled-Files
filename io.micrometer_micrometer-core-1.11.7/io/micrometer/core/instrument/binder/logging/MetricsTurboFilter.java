/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.Level
 *  ch.qos.logback.classic.Logger
 *  ch.qos.logback.classic.turbo.TurboFilter
 *  ch.qos.logback.core.spi.FilterReply
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.slf4j.Marker
 */
package io.micrometer.core.instrument.binder.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import org.slf4j.Marker;

@NonNullApi
@NonNullFields
class MetricsTurboFilter
extends TurboFilter {
    private static final String METER_NAME = "logback.events";
    private static final String METER_DESCRIPTION = "Number of log events that were enabled by the effective log level";
    private final Counter errorCounter;
    private final Counter warnCounter;
    private final Counter infoCounter;
    private final Counter debugCounter;
    private final Counter traceCounter;

    MetricsTurboFilter(MeterRegistry registry, Iterable<Tag> tags) {
        this.errorCounter = Counter.builder(METER_NAME).tags(tags).tags("level", "error").description(METER_DESCRIPTION).baseUnit("events").register(registry);
        this.warnCounter = Counter.builder(METER_NAME).tags(tags).tags("level", "warn").description(METER_DESCRIPTION).baseUnit("events").register(registry);
        this.infoCounter = Counter.builder(METER_NAME).tags(tags).tags("level", "info").description(METER_DESCRIPTION).baseUnit("events").register(registry);
        this.debugCounter = Counter.builder(METER_NAME).tags(tags).tags("level", "debug").description(METER_DESCRIPTION).baseUnit("events").register(registry);
        this.traceCounter = Counter.builder(METER_NAME).tags(tags).tags("level", "trace").description(METER_DESCRIPTION).baseUnit("events").register(registry);
    }

    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (format == null || !level.isGreaterOrEqual(logger.getEffectiveLevel())) {
            return FilterReply.NEUTRAL;
        }
        Boolean ignored = LogbackMetrics.ignoreMetrics.get();
        if (ignored != null && ignored.booleanValue()) {
            return FilterReply.NEUTRAL;
        }
        LogbackMetrics.ignoreMetrics(() -> this.recordMetrics(level));
        return FilterReply.NEUTRAL;
    }

    private void recordMetrics(Level level) {
        switch (level.toInt()) {
            case 40000: {
                this.errorCounter.increment();
                break;
            }
            case 30000: {
                this.warnCounter.increment();
                break;
            }
            case 20000: {
                this.infoCounter.increment();
                break;
            }
            case 10000: {
                this.debugCounter.increment();
                break;
            }
            case 5000: {
                this.traceCounter.increment();
            }
        }
    }
}

