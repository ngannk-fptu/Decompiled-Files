/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.concurrent;

import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.impl.HealthCheckTimeoutListener;
import com.atlassian.troubleshooting.healthcheck.util.SupportHealthCheckUtils;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportHealthCheckTask {
    @VisibleForTesting
    static final String FAILED_TO_RUN_KB_ARTICLE_LINK = "https://confluence.atlassian.com/x/lU1OOg";
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportHealthCheckTask.class);
    private final ExtendedSupportHealthCheck healthCheck;
    private final HealthCheckTimeoutListener timeoutListener;
    private final ClusterService clusterService;
    private final ExecutorService tasksWatchdogExecutor;
    private volatile SupportHealthStatus healthStatus;

    public SupportHealthCheckTask(@Nonnull ExtendedSupportHealthCheck healthCheck, @Nonnull ExecutorService tasksWatchdogExecutor, HealthCheckTimeoutListener timeoutListener, ClusterService clusterService) {
        this.healthCheck = Objects.requireNonNull(healthCheck);
        this.tasksWatchdogExecutor = Objects.requireNonNull(tasksWatchdogExecutor);
        this.timeoutListener = timeoutListener;
        this.clusterService = clusterService;
    }

    public Future<HealthCheckStatus> runAsync(@Nonnull ExecutorService executor) throws RejectedExecutionException {
        Objects.requireNonNull(executor);
        Future<SupportHealthStatus> taskFuture = executor.submit(this.healthCheck::check);
        LOGGER.debug("Scheduled health check {} into executor.", (Object)this.healthCheck.getName());
        return this.tasksWatchdogExecutor.submit(() -> SupportHealthCheckUtils.asHealthCheckStatus(this.healthCheck, this.getFutureWithTimeout(taskFuture)));
    }

    public Optional<HealthCheckStatus> getStatus() {
        return Optional.ofNullable(this.healthStatus).map(status -> SupportHealthCheckUtils.asHealthCheckStatus(this.healthCheck, status));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SupportHealthStatus getFutureWithTimeout(Future<SupportHealthStatus> future) {
        try {
            SupportHealthStatus supportHealthStatus = this.healthStatus = future.get(this.healthCheck.getTimeOut(), TimeUnit.MILLISECONDS);
            return supportHealthStatus;
        }
        catch (CancellationException | TimeoutException e) {
            if (this.timeoutListener != null) {
                this.timeoutListener.accept(this.healthCheck);
            }
            SupportHealthStatus supportHealthStatus = this.healthStatus = this.healthCheckTimeout();
            return supportHealthStatus;
        }
        catch (Exception e) {
            SupportHealthStatus supportHealthStatus = this.healthStatus = this.healthCheckError(e);
            return supportHealthStatus;
        }
        finally {
            future.cancel(true);
        }
    }

    private SupportHealthStatus healthCheckTimeout() {
        LOGGER.warn("Health check {} was unable to complete within the timeout of {}.", (Object)this.healthCheck.getName(), (Object)this.healthCheck.getTimeOut());
        return this.failedStatus("The health check was unable to complete within the timeout of " + this.healthCheck.getTimeOut() + "ms.", SupportHealthStatus.Severity.UNDEFINED, "");
    }

    private SupportHealthStatus healthCheckError(Exception e) {
        LOGGER.warn("Unable to complete execution of health check {} due to an exception", (Object)this.healthCheck.getName(), (Object)e);
        return this.failedStatus("Exception during health check invocation " + e.getMessage(), SupportHealthStatus.Severity.MAJOR, FAILED_TO_RUN_KB_ARTICLE_LINK);
    }

    private DefaultSupportHealthStatus failedStatus(String failureReason, SupportHealthStatus.Severity severity, String documentationLink) {
        String nodeId = this.healthCheck.isNodeSpecific() ? (String)this.clusterService.getCurrentNodeId().orElse(null) : null;
        return new DefaultSupportHealthStatus(false, failureReason, System.currentTimeMillis(), Application.Unknown, nodeId, severity, documentationLink);
    }
}

