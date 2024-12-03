/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.servlet.ServletRequest
 */
package com.atlassian.confluence.internal.diagnostics.ipd.http;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.internal.diagnostics.ipd.http.IpdHttpMonitoringService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletRequest;

public class DefaultIpdHttpMonitoringService
implements IpdHttpMonitoringService {
    private final ConcurrentLinkedQueue<Instant> requestTimestampQueue;
    private final Duration requestTTL;
    private final AtomicReference<Instant> lastClean;
    private final Clock clock;

    public DefaultIpdHttpMonitoringService(Clock clock) {
        this.clock = clock;
        this.requestTTL = Duration.ofMinutes(1L);
        this.lastClean = new AtomicReference<Instant>(clock.instant());
        this.requestTimestampQueue = new ConcurrentLinkedQueue();
    }

    @VisibleForTesting
    DefaultIpdHttpMonitoringService(Clock clock, Duration requestTTL, AtomicReference<Instant> lastClean, ConcurrentLinkedQueue<Instant> requestTimestampQueue) {
        this.clock = clock;
        this.requestTTL = requestTTL;
        this.lastClean = lastClean;
        this.requestTimestampQueue = requestTimestampQueue;
    }

    @Override
    public void registerHttpRequest(ServletRequest servletRequest) {
        this.countRequest();
    }

    @Override
    public long numberOfRecentRequests(Long milliseconds) {
        return this.requestTimestampQueue.stream().filter(instant -> instant.plus((long)milliseconds, ChronoUnit.MILLIS).isAfter(this.clock.instant())).count();
    }

    private void countRequest() {
        this.requestTimestampQueue.add(this.clock.instant());
        if (this.shouldClean()) {
            this.cleanQueue();
        }
    }

    private void cleanQueue() {
        this.requestTimestampQueue.removeIf(instant -> Duration.between(this.clock.instant(), instant.plus(this.requestTTL)).isNegative());
        this.lastClean.set(this.clock.instant());
    }

    private boolean shouldClean() {
        return this.lastClean.get().plus(this.requestTTL).isBefore(this.clock.instant());
    }
}

