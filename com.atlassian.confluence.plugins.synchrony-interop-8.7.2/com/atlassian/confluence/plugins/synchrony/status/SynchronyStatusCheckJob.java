/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.status;

import com.atlassian.confluence.plugins.synchrony.api.SynchronyMonitor;
import com.atlassian.confluence.plugins.synchrony.api.SynchronyProcessManager;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.status.SynchronyStatusCache;
import com.atlassian.confluence.plugins.synchrony.status.SynchronyStatusEventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="synchronyStatusCheckJob")
public final class SynchronyStatusCheckJob
implements JobRunner {
    @VisibleForTesting
    static final String FAIL_MESSAGE = "Did not put the Synchrony Status into the cache.";
    private static final Logger log = LoggerFactory.getLogger(SynchronyStatusCheckJob.class);
    private final SynchronyMonitor synchronyMonitor;
    private final SynchronyStatusCache synchronyStatusCache;
    private final SynchronyStatusEventPublisher synchronyStatusEventPublisher;
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private final SynchronyProcessManager synchronyProcessManager;

    @Autowired
    public SynchronyStatusCheckJob(SynchronyMonitor synchronyMonitor, SynchronyProcessManager synchronyProcessManager, SynchronyStatusCache cache, SynchronyStatusEventPublisher synchronyStatusEventPublisher, @ComponentImport(value="synchronyConfigurationManager") SynchronyConfigurationManager synchronyConfigurationManager) {
        this.synchronyMonitor = Objects.requireNonNull(synchronyMonitor);
        this.synchronyStatusCache = Objects.requireNonNull(cache);
        this.synchronyStatusEventPublisher = Objects.requireNonNull(synchronyStatusEventPublisher);
        this.synchronyConfigurationManager = Objects.requireNonNull(synchronyConfigurationManager);
        this.synchronyProcessManager = Objects.requireNonNull(synchronyProcessManager);
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.debug("Checking Synchrony Status...");
        if (!this.synchronyConfigurationManager.isSharedDraftsEnabled()) {
            log.debug("Collaborative editing is disabled. Not updating the synchrony status in the cache.");
            return JobRunnerResponse.success();
        }
        if (this.synchronyProcessManager.isSynchronyStartingUp()) {
            log.debug("Synchrony is still starting up. Not updating the synchrony status in the cache.");
            return JobRunnerResponse.success();
        }
        try {
            Optional<Boolean> realCacheValue = this.synchronyStatusCache.getStatus();
            boolean wasPreviouslyRunning = this.synchronyStatusCache.isSynchronyRunning();
            boolean isRunningNow = this.synchronyMonitor.isSynchronyUp();
            log.debug("Synchrony is running: " + isRunningNow);
            if (realCacheValue.isPresent()) {
                this.synchronyStatusEventPublisher.decideEvents(isRunningNow, wasPreviouslyRunning);
            }
            this.synchronyStatusCache.setStatus(isRunningNow);
            return JobRunnerResponse.success();
        }
        catch (RuntimeException e) {
            log.error(FAIL_MESSAGE, (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }
}

