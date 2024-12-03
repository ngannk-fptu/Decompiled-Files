/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.config.DataPipelinePluginSystemProperties;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DataExportJobExecutor
implements Executor,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DataExportJobExecutor.class);
    private ExecutorService executorService;
    private AtomicReference<Future<?>> currentResult = new AtomicReference();
    private final int waitForCompletionTimeoutSeconds;
    private final int terminateTimeoutSeconds;

    public DataExportJobExecutor(DataPipelinePluginSystemProperties properties) {
        this(properties.getExportExecutorWaitTimeoutSeconds(), properties.getExportExecutorTerminateTimeoutSeconds());
    }

    @VisibleForTesting
    DataExportJobExecutor(int waitForCompletionTimeoutSeconds, int terminateTimeoutSeconds) {
        this.waitForCompletionTimeoutSeconds = waitForCompletionTimeoutSeconds;
        this.terminateTimeoutSeconds = terminateTimeoutSeconds;
        this.executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("data-pipeline-export-executor-%d").build());
    }

    @Override
    public synchronized void execute(@Nonnull Runnable command) {
        Objects.requireNonNull(command);
        this.waitForCurrentTaskToComplete();
        this.currentResult.set(this.executorService.submit(command));
    }

    public void destroy() throws InterruptedException {
        this.executorService.shutdown();
        if (!this.executorService.isTerminated()) {
            this.executorService.awaitTermination(this.terminateTimeoutSeconds, TimeUnit.SECONDS);
            if (!this.executorService.isTerminated()) {
                log.warn("Fail to terminate current export task after {} seconds", (Object)this.terminateTimeoutSeconds);
            }
        }
    }

    void waitForCurrentTaskToComplete() {
        if (this.currentResult.get() != null && !this.currentResult.get().isDone() && !this.currentResult.get().isCancelled()) {
            try {
                this.currentResult.get().get(this.waitForCompletionTimeoutSeconds, TimeUnit.SECONDS);
            }
            catch (TimeoutException e) {
                throw new DataExportJobExecutorException("Timeout waiting for current export to complete.", e);
            }
            catch (ExecutionException e) {
                throw new DataExportJobExecutorException("Execution failed", e);
            }
            catch (InterruptedException e) {
                log.error("InterruptedException occurs while waiting for current task to finish.", (Throwable)e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static class DataExportJobExecutorException
    extends RuntimeException {
        public DataExportJobExecutorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

