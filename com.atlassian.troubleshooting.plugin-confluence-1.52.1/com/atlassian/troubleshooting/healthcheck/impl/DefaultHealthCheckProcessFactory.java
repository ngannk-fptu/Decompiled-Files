/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.api.healthcheck.ExecutorServiceFactory;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckProcessFactory;
import com.atlassian.troubleshooting.healthcheck.concurrent.SupportHealthCheckProcess;
import com.atlassian.troubleshooting.healthcheck.concurrent.SupportHealthCheckTask;
import com.atlassian.troubleshooting.healthcheck.impl.HealthCheckTimeoutListener;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPersistenceService;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultHealthCheckProcessFactory
implements HealthCheckProcessFactory,
InitializingBean,
DisposableBean {
    private static final int CHECK_THREAD_COUNT = Integer.getInteger("atlassian.healthcheck.thread-count", 8);
    private final HealthCheckTimeoutListener timeoutListener;
    private final HealthStatusPersistenceService healthStatusPersistenceService;
    private final ExecutorServiceFactory executorServiceFactory;
    private final ClusterService clusterService;
    private ExecutorService tasksExecutor;
    private ExecutorService tasksWatchdogExecutor;

    @Autowired
    public DefaultHealthCheckProcessFactory(HealthCheckTimeoutListener timeoutListener, @Nonnull HealthStatusPersistenceService healthStatusPersistenceService, @Nonnull ExecutorServiceFactory executorServiceFactory, @Nonnull ClusterService clusterService) {
        this.timeoutListener = timeoutListener;
        this.healthStatusPersistenceService = Objects.requireNonNull(healthStatusPersistenceService);
        this.executorServiceFactory = Objects.requireNonNull(executorServiceFactory);
        this.clusterService = clusterService;
    }

    public void afterPropertiesSet() throws Exception {
        this.tasksExecutor = this.executorServiceFactory.newFixedSizeThreadPool(CHECK_THREAD_COUNT, "HealthCheck");
        this.tasksWatchdogExecutor = this.executorServiceFactory.newFixedSizeThreadPool(CHECK_THREAD_COUNT, "HealthCheckWatchdog");
    }

    public void destroy() throws Exception {
        this.tasksExecutor.shutdownNow();
        this.tasksWatchdogExecutor.shutdownNow();
    }

    @Override
    @Nonnull
    public SupportHealthCheckProcess createProcess(@Nonnull Collection<ExtendedSupportHealthCheck> healthChecks) {
        List<SupportHealthCheckTask> tasks = healthChecks.stream().map(healthCheck -> new SupportHealthCheckTask((ExtendedSupportHealthCheck)healthCheck, this.tasksWatchdogExecutor, this.timeoutListener, this.clusterService)).collect(Collectors.toList());
        return new SupportHealthCheckProcess(this.tasksExecutor, tasks, this.healthStatusPersistenceService::storeFailedStatuses);
    }
}

