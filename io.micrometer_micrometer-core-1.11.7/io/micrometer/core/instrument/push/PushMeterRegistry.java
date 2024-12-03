/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.push;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class PushMeterRegistry
extends MeterRegistry {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PushMeterRegistry.class);
    private static final double PERCENT_RANGE_OF_RANDOM_PUBLISHING_OFFSET = 0.8;
    private final PushRegistryConfig config;
    private final Semaphore publishingSemaphore = new Semaphore(1);
    private long lastScheduledPublishStartTime = 0L;
    @Nullable
    private ScheduledExecutorService scheduledExecutorService;

    protected PushMeterRegistry(PushRegistryConfig config, Clock clock) {
        super(clock);
        config.requireValid();
        this.config = config;
    }

    protected abstract void publish();

    void publishSafelyOrSkipIfInProgress() {
        if (this.publishingSemaphore.tryAcquire()) {
            this.lastScheduledPublishStartTime = this.clock.wallTime();
            try {
                this.publish();
            }
            catch (Throwable e) {
                logger.warn("Unexpected exception thrown while publishing metrics for " + this.getClass().getSimpleName(), e);
            }
            finally {
                this.publishingSemaphore.release();
            }
        } else {
            logger.warn("Publishing is already in progress. Skipping duplicate call to publish().");
        }
    }

    protected boolean isPublishing() {
        return this.publishingSemaphore.availablePermits() == 0;
    }

    protected long getLastScheduledPublishStartTime() {
        return this.lastScheduledPublishStartTime;
    }

    @Deprecated
    public final void start() {
        this.start(Executors.defaultThreadFactory());
    }

    public void start(ThreadFactory threadFactory) {
        if (this.scheduledExecutorService != null) {
            this.stop();
        }
        if (this.config.enabled()) {
            logger.info("publishing metrics for " + this.getClass().getSimpleName() + " every " + TimeUtils.format(this.config.step()));
            this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
            long stepMillis = this.config.step().toMillis();
            long initialDelayMillis = this.calculateInitialDelay();
            this.scheduledExecutorService.scheduleAtFixedRate(this::publishSafelyOrSkipIfInProgress, initialDelayMillis, stepMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (this.scheduledExecutorService != null) {
            this.scheduledExecutorService.shutdown();
            this.scheduledExecutorService = null;
        }
    }

    @Override
    public void close() {
        this.stop();
        if (this.config.enabled() && !this.isClosed()) {
            this.publishSafelyOrSkipIfInProgress();
            this.waitForInProgressScheduledPublish();
        }
        super.close();
    }

    protected void waitForInProgressScheduledPublish() {
        try {
            this.publishingSemaphore.acquire();
            this.publishingSemaphore.release();
        }
        catch (InterruptedException e) {
            logger.warn("Interrupted while waiting for publish on close to finish", (Throwable)e);
        }
    }

    long calculateInitialDelay() {
        long stepMillis = this.config.step().toMillis();
        Random random = new Random();
        long randomOffsetWithinStep = Math.max(0L, (long)((double)stepMillis * random.nextDouble() * 0.8) - 2L);
        long offsetToStartOfNextStep = stepMillis - this.clock.wallTime() % stepMillis;
        return offsetToStartOfNextStep + 2L + randomOffsetWithinStep;
    }
}

