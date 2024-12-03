/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 *  io.micrometer.common.util.internal.logging.WarnThenDebugLogger
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.common.util.internal.logging.WarnThenDebugLogger;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class HighCardinalityTagsDetector
implements AutoCloseable {
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HighCardinalityTagsDetector.class);
    private static final WarnThenDebugLogger WARN_THEN_DEBUG_LOGGER = new WarnThenDebugLogger(HighCardinalityTagsDetector.class);
    private static final Duration DEFAULT_DELAY = Duration.ofMinutes(5L);
    private final MeterRegistry registry;
    private final long threshold;
    private final Consumer<String> meterNameConsumer;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Duration delay;

    public HighCardinalityTagsDetector(MeterRegistry registry) {
        this(registry, HighCardinalityTagsDetector.calculateThreshold(), DEFAULT_DELAY);
    }

    public HighCardinalityTagsDetector(MeterRegistry registry, long threshold, Duration delay) {
        this(registry, threshold, delay, null);
    }

    public HighCardinalityTagsDetector(MeterRegistry registry, long threshold, Duration delay, @Nullable Consumer<String> meterNameConsumer) {
        this.registry = registry;
        this.threshold = threshold;
        this.delay = delay;
        this.meterNameConsumer = meterNameConsumer != null ? meterNameConsumer : this::logWarning;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("high-cardinality-tags-detector"));
    }

    public void start() {
        LOGGER.info(String.format("Starting %s with threshold: %d and delay: %s", this.getClass().getSimpleName(), this.threshold, this.delay));
        this.scheduledExecutorService.scheduleWithFixedDelay(this::detectHighCardinalityTags, 0L, this.delay.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        LOGGER.info("Stopping " + this.getClass().getSimpleName());
        this.scheduledExecutorService.shutdown();
    }

    @Override
    public void close() {
        this.shutdown();
    }

    private void detectHighCardinalityTags() {
        try {
            this.findFirst().ifPresent(this.meterNameConsumer);
        }
        catch (Exception exception) {
            LOGGER.warn("Something went wrong during high cardinality tag detection", (Throwable)exception);
        }
    }

    public Optional<String> findFirst() {
        HashMap<String, Long> meterNameFrequencies = new HashMap<String, Long>();
        for (Meter meter : this.registry.getMeters()) {
            String name = meter.getId().getName();
            if (!meterNameFrequencies.containsKey(name)) {
                meterNameFrequencies.put(name, 1L);
                continue;
            }
            Long frequency = (Long)meterNameFrequencies.get(name);
            if (frequency < this.threshold) {
                meterNameFrequencies.put(name, frequency + 1L);
                continue;
            }
            return Optional.of(name);
        }
        return Optional.empty();
    }

    private void logWarning(String name) {
        WARN_THEN_DEBUG_LOGGER.log(() -> String.format("It seems %s has high cardinality tags (threshold: %d meters).\nCheck your configuration for the instrumentation of %s to find and fix the cause of the high cardinality (see: https://micrometer.io/docs/concepts#_tag_values).\nIf the cardinality is expected and acceptable, raise the threshold for this %s.", name, this.threshold, name, this.getClass().getSimpleName()));
    }

    private static long calculateThreshold() {
        long allowance = Runtime.getRuntime().maxMemory() / 1024L / 1024L / 10L;
        return Math.max(1000L, Math.min(allowance * 2000L, 2000000L));
    }
}

