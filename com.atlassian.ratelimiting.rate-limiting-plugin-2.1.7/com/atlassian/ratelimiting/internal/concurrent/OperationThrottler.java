/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.concurrent;

import com.atlassian.ratelimiting.internal.concurrent.LockGuard;
import com.google.common.annotations.VisibleForTesting;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationThrottler<T> {
    private static final Logger log = LoggerFactory.getLogger(OperationThrottler.class);
    private static final int MAP_SIZE_CLEANUP_THRESHOLD = 1500;
    private final Clock clock;
    private final Duration minimumTimeBetweenRuns;
    private final ConcurrentMap<T, Instant> operationThrottleExpiryTimes;
    private final ReentrantLock lock;

    public OperationThrottler(Duration minimumTimeBetweenRuns) {
        this(Clock.systemDefaultZone(), minimumTimeBetweenRuns);
    }

    public OperationThrottler(Clock clock, Duration minimumTimeBetweenRuns) {
        this.clock = clock;
        this.minimumTimeBetweenRuns = minimumTimeBetweenRuns;
        this.operationThrottleExpiryTimes = new ConcurrentHashMap<T, Instant>();
        this.lock = new ReentrantLock();
    }

    public void tryRun(T key, Runnable operation) {
        Instant now = this.clock.instant();
        this.operationThrottleExpiryTimes.compute(key, (k, v) -> this.computeExpiryTimeAndRunOperation((T)k, (Instant)v, now, operation));
        this.cleanUpIfRequired(now);
    }

    private Instant computeExpiryTimeAndRunOperation(T key, Instant expiryTime, Instant now, Runnable operation) {
        if (Objects.isNull(expiryTime) || expiryTime.isBefore(now)) {
            log.trace("[key={}] Running operation", key);
            operation.run();
            return now.plus(this.minimumTimeBetweenRuns);
        }
        log.trace("[key={}] Skipping operation because it already ran in the past {} ms", key, (Object)this.minimumTimeBetweenRuns.toMillis());
        return expiryTime;
    }

    private void cleanUpIfRequired(Instant currentInstant) {
        if (1500 < this.operationThrottleExpiryTimes.size()) {
            log.trace("Cleaning Operation Throttler cache");
            try (LockGuard guard = LockGuard.tryLock(this.lock);){
                if (guard != null) {
                    this.operationThrottleExpiryTimes.entrySet().removeIf(entry -> ((Instant)entry.getValue()).plus(this.minimumTimeBetweenRuns).isBefore(currentInstant));
                }
            }
            log.trace("Operation Throttler cache cleaned, {} items remain", (Object)this.operationThrottleExpiryTimes.size());
        }
    }

    @VisibleForTesting
    int getMapSize() {
        return this.operationThrottleExpiryTimes.size();
    }
}

