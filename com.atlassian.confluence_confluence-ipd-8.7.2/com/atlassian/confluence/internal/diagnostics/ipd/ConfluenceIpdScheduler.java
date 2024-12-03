/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStartedEvent
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistryLogger
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd;

import com.atlassian.config.lifecycle.events.ApplicationStartedEvent;
import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistryLogger;
import com.atlassian.diagnostics.ipd.internal.spi.IpdJobRunner;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceIpdScheduler {
    private static final Logger regularLogger = LoggerFactory.getLogger(ConfluenceIpdScheduler.class);
    private static final int DEFAULT_INTERVAL_SECONDS = 60;
    private static final int COLLECTOR_DELAY = 30;
    private static final int EMITTER_DELAY = 0;
    @VisibleForTesting
    private static final String OBJECTS_NAME_PREFIX = "com.atlassian.confluence";
    private static final int TERMINATION_TIMEOUT = 60;
    public static final String IPD_THREAD_NAME = "ipd-worker";
    private final IpdMainRegistry ipdMainRegistry;
    private final ScheduledExecutorService scheduledExecutorService;
    private final IpdMainRegistryLogger ipdMainRegistryLogger;
    private final IpdJobRunner ipdJobRunner;
    private final DarkFeatureManager darkFeatureManager;
    private final EventPublisher eventPublisher;

    public ConfluenceIpdScheduler(IpdMainRegistryLogger ipdMainRegistryLogger, IpdJobRunner ipdJobRunner, DarkFeatureManager darkFeatureManager, IpdMainRegistry ipdMainRegistry, EventPublisher eventPublisher) {
        this(ipdMainRegistryLogger, ipdJobRunner, darkFeatureManager, eventPublisher, ipdMainRegistry, Executors.newSingleThreadScheduledExecutor(ThreadFactories.namedThreadFactory((String)IPD_THREAD_NAME, (ThreadFactories.Type)ThreadFactories.Type.DAEMON)));
    }

    @VisibleForTesting
    ConfluenceIpdScheduler(IpdMainRegistryLogger ipdMainRegistryLogger, IpdJobRunner ipdJobRunner, DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher, IpdMainRegistry ipdMainRegistry, ScheduledExecutorService scheduledExecutorService) {
        this.ipdMainRegistryLogger = Objects.requireNonNull(ipdMainRegistryLogger);
        this.ipdJobRunner = Objects.requireNonNull(ipdJobRunner);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.ipdMainRegistry = Objects.requireNonNull(ipdMainRegistry);
        this.scheduledExecutorService = Objects.requireNonNull(scheduledExecutorService);
    }

    @PostConstruct
    public void postConstruct() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationStarted(ApplicationStartedEvent applicationStartedEvent) {
        regularLogger.info("Scheduling {} with poll interval of {} seconds", (Object)"IPDMONITORING", (Object)60);
        this.scheduledExecutorService.scheduleAtFixedRate(this::ipdEmitterIteration, 0L, 60L, TimeUnit.SECONDS);
        this.scheduledExecutorService.scheduleAtFixedRate(this::ipdCollectorIteration, 30L, 60L, TimeUnit.SECONDS);
    }

    @EventListener
    public void onApplicationStopping(ApplicationStoppingEvent applicationStoppingEvent) {
        this.scheduledExecutorService.shutdown();
        regularLogger.info("Shutdown IPD scheduler");
        try {
            if (!this.scheduledExecutorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                this.scheduledExecutorService.shutdownNow();
                if (!this.scheduledExecutorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                    regularLogger.debug("Failed to terminate IPD scheduler");
                }
            }
        }
        catch (InterruptedException ie) {
            this.scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @VisibleForTesting
    void ipdEmitterIteration() {
        try {
            this.ipdMainRegistry.unregisterAllDisabledMetrics();
            this.ipdJobRunner.runJobs();
        }
        catch (Exception ex) {
            regularLogger.error("Unable to complete IPD emitting iteration.", (Throwable)ex);
        }
    }

    @VisibleForTesting
    void ipdCollectorIteration() {
        try {
            this.ipdMainRegistryLogger.logRegisteredMetrics(this.isExtraLoggingInfoEnabled());
        }
        catch (Exception ex) {
            regularLogger.error("Unable to complete IPD data logging iteration.", (Throwable)ex);
        }
    }

    private boolean isExtraLoggingInfoEnabled() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.in.product.diagnostics.extended.logging").orElse(false);
    }
}

