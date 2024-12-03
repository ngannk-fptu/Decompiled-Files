/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.atlassian.diagnostics.internal.concurrent.Gate
 *  com.atlassian.event.spi.ListenerInvoker
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.event;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.concurrent.Gate;
import com.atlassian.diagnostics.internal.platform.monitor.DurationUtils;
import com.atlassian.diagnostics.internal.platform.monitor.event.EventDroppedDetails;
import com.atlassian.diagnostics.internal.platform.monitor.event.EventSystemMonitorConfig;
import com.atlassian.event.spi.ListenerInvoker;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class EventSystemMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.event.issue";
    static final int ID_EVENT_DROPPED = 1001;
    static final int ID_SLOW_LISTENER = 2001;
    private final EventSystemMonitorConfig config;
    private final Object lock;
    private final Set<Thread> invokerThreads;
    private final ThreadDumpProducer threadDumpProducer;
    private final Gate threadDumpGate;
    private final Clock clock;

    EventSystemMonitor(@Nonnull Clock clock, @Nonnull EventSystemMonitorConfig config, @Nonnull ThreadDumpProducer threadDumpProducer) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.config = Objects.requireNonNull(config, "config");
        this.threadDumpGate = new Gate(clock, config.getEventDroppedAlertThreadDumpCoolDown());
        this.threadDumpProducer = Objects.requireNonNull(threadDumpProducer, "threadDumpProducer");
        this.lock = new Object();
        this.invokerThreads = Sets.newConcurrentHashSet();
    }

    public EventSystemMonitor(@Nonnull EventSystemMonitorConfig config, @Nonnull ThreadDumpProducer threadDumpProducer) {
        this(Clock.systemDefaultZone(), config, threadDumpProducer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init(MonitoringService monitoringService) {
        Object object = this.lock;
        synchronized (object) {
            this.monitor = monitoringService.createMonitor("EVENT", "diagnostics.event.name");
            this.defineIssues();
        }
    }

    protected void defineIssues() {
        this.defineIssue(KEY_PREFIX, 1001, Severity.ERROR, EventDroppedDetails.class);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
    }

    void alertEventDropped(@Nonnull Instant timestamp, int queueLength, @Nonnull Class<?> eventClass) {
        Objects.requireNonNull(timestamp, "timestamp");
        this.alert(1001, builder -> builder.timestamp(timestamp).details(() -> {
            EventDroppedDetails.Builder detailsBuilder = new EventDroppedDetails.Builder(eventClass.getName(), queueLength);
            this.threadDumpGate.ifAccessible(() -> {
                detailsBuilder.threadDumps(this.threadDumpProducer.produce(this.getEventPoolThreads()));
                return null;
            });
            return detailsBuilder.build();
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void invokeMonitored(@Nonnull AlertTrigger trigger, @Nonnull ListenerInvoker delegate, @Nonnull Object event) {
        Instant start = this.clock.instant();
        this.invokerThreads.add(Thread.currentThread());
        try {
            delegate.invoke(event);
        }
        finally {
            this.invokerThreads.remove(Thread.currentThread());
            Duration duration = Duration.between(start, this.clock.instant());
            if (DurationUtils.durationOf(duration).isGreaterThanOrEqualTo(this.config.getSlowListenerAlertDuration(trigger))) {
                this.alert(2001, builder -> builder.timestamp(start).trigger(trigger).details(() -> ImmutableMap.of((Object)"timeMillis", (Object)duration.toMillis(), (Object)"eventType", (Object)event.getClass().getName())));
            }
        }
    }

    private Set<Thread> getEventPoolThreads() {
        return this.config.getEventThreadGroup().map(threadGroup -> this.invokerThreads.stream().filter(thread -> thread.getThreadGroup().equals(threadGroup)).collect(Collectors.toSet())).orElse(this.invokerThreads);
    }
}

