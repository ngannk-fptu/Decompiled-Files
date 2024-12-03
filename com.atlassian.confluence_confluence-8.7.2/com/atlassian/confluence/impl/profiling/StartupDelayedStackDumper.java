/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.profiling;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartupDelayedStackDumper {
    private static final Logger log = LoggerFactory.getLogger(StartupDelayedStackDumper.class);
    private final ScheduledExecutorService executorService;
    private final EventListenerRegistrar eventListenerRegistrar;
    private ScheduledFuture<?> taskFuture;

    public StartupDelayedStackDumper(ScheduledExecutorService executorService, EventListenerRegistrar eventListenerRegistrar) {
        this.executorService = executorService;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    private static Optional<Duration> getDelay() {
        return Optional.ofNullable(System.getProperty("confluence.startup.stackdump.delay")).map(Duration::parse);
    }

    @PostConstruct
    public void start() {
        StartupDelayedStackDumper.getDelay().ifPresent(this::scheduleStackDump);
    }

    private void scheduleStackDump(Duration delay) {
        log.warn("Full stack dump will be triggered if the application has not started within {}", (Object)delay);
        this.taskFuture = this.executorService.schedule(this::dumpStack, delay.toMillis(), TimeUnit.MILLISECONDS);
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void applicationStarted(ApplicationStartedEvent event) {
        log.warn("Application started, cancelling scheduled stack dump");
        this.stop();
    }

    private void dumpStack() {
        ThreadInfo[] threads;
        StringBuilder buffer = new StringBuilder();
        buffer.append("Stack dump triggered\n");
        for (ThreadInfo thread : threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)) {
            buffer.append(thread.toString());
        }
        log.warn(buffer.toString());
    }

    @PreDestroy
    public void stop() {
        if (this.taskFuture != null) {
            this.taskFuture.cancel(false);
        }
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

