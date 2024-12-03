/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.concurrent;

import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.concurrent.SupportHealthCheckTask;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportHealthCheckProcess {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportHealthCheckProcess.class);
    private final Collection<SupportHealthCheckTask> tasks;
    private final ExecutorService executor;
    private final Consumer<List<HealthCheckStatus>> completionListener;

    public SupportHealthCheckProcess(@Nonnull ExecutorService executor, @Nonnull Collection<SupportHealthCheckTask> tasks, Consumer<List<HealthCheckStatus>> completionListener) {
        this.executor = Objects.requireNonNull(executor);
        this.tasks = Objects.requireNonNull(tasks);
        this.completionListener = completionListener;
    }

    private static Optional<HealthCheckStatus> awaitSafely(Future<HealthCheckStatus> future) {
        try {
            return Optional.of(future.get());
        }
        catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error waiting for the health check future to complete", (Throwable)e);
            return Optional.empty();
        }
    }

    @Nonnull
    public List<HealthCheckStatus> getCompletedStatuses() {
        return this.tasks.stream().map(SupportHealthCheckTask::getStatus).filter(Optional::isPresent).map(Optional::get).peek(status -> {
            if (!status.isHealthy()) {
                String failureMessage = String.format("Health check '%s' failed with severity '%s': '%s'", status.getName(), status.getSeverity().stringValue(), status.getFailureReason());
                if (status.getSeverity() == SupportHealthStatus.Severity.MAJOR || status.getSeverity() == SupportHealthStatus.Severity.CRITICAL) {
                    LOGGER.error(failureMessage);
                } else if (status.getSeverity() == SupportHealthStatus.Severity.DISABLED) {
                    LOGGER.debug(failureMessage);
                } else {
                    LOGGER.warn(failureMessage);
                }
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Health check {} passed: {}", (Object)status.getName(), (Object)status.getFailureReason());
            }
        }).collect(Collectors.toList());
    }

    public Future<List<HealthCheckStatus>> runAsync() {
        return this.executor.submit(this::runSync);
    }

    public List<HealthCheckStatus> runSync() {
        List futures = this.tasks.stream().map(t -> t.runAsync(this.executor)).collect(Collectors.toList());
        List results = futures.stream().map(SupportHealthCheckProcess::awaitSafely).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Optional.ofNullable(this.completionListener).ifPresent(l -> l.accept(results));
        return this.getCompletedStatuses();
    }
}

