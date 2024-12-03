/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.healthcheck.core.impl;

import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.DefaultHealthStatus;
import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheckModuleDescriptorNotFoundException;
import com.atlassian.healthcheck.core.HealthCheckSupplier;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.HealthStatusExtended;
import com.atlassian.healthcheck.core.impl.HealthCheckManager;
import com.atlassian.healthcheck.core.impl.HostStateMonitor;
import com.atlassian.healthcheck.core.impl.Pair;
import com.atlassian.healthcheck.core.thread.HealthCheckCallable;
import com.atlassian.healthcheck.core.thread.HealthCheckThreadFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class DefaultHealthCheckManager
implements HealthCheckManager,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultHealthCheckManager.class);
    private static final int CHECK_THREAD_COUNT = Integer.getInteger("atlassian.healthcheck.thread-count", 8);
    private final HostStateMonitor hostStateMonitor;
    private final HealthCheckSupplier healthCheckSupplier;
    private ExecutorService executorService;

    public static ExecutorService createDefaultExecutorService() {
        return Executors.newFixedThreadPool(CHECK_THREAD_COUNT, new HealthCheckThreadFactory());
    }

    public DefaultHealthCheckManager(HealthCheckSupplier healthCheckSupplier, HostStateMonitor hostStateMonitor) {
        this(healthCheckSupplier, hostStateMonitor, DefaultHealthCheckManager.createDefaultExecutorService());
    }

    public DefaultHealthCheckManager(HealthCheckSupplier healthCheckSupplier, HostStateMonitor hostStateMonitor, ExecutorService executorService) {
        this.healthCheckSupplier = healthCheckSupplier;
        this.hostStateMonitor = hostStateMonitor;
        this.executorService = executorService;
    }

    public void destroy() throws Exception {
        this.executorService.shutdownNow();
    }

    @Override
    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecks() {
        return this.runChecksIfHostReady(this.getHealthChecks());
    }

    @Override
    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecksWithKeys(Set<String> completeKeys) throws HealthCheckModuleDescriptorNotFoundException {
        return this.runChecksIfHostReady(this.getHealthChecksWithKeys(completeKeys));
    }

    @Override
    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecksWithTags(Set<String> tagsToInclude) {
        return this.runChecksIfHostReady(this.getHealthChecksWithTags(tagsToInclude));
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecks() {
        return this.healthCheckSupplier.getHealthChecks();
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecksWithKeys(Set<String> completeKeys) throws HealthCheckModuleDescriptorNotFoundException {
        return this.healthCheckSupplier.getHealthChecksWithKeys(completeKeys);
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecksWithTags(Set<String> tagsToInclude) {
        return this.healthCheckSupplier.getHealthChecksWithTags(tagsToInclude);
    }

    private Collection<Pair<ExtendedHealthCheck, HealthStatus>> runChecksIfHostReady(Collection<ExtendedHealthCheck> healthChecks) {
        HostStateCheck hostApplicationHealthCheck = new HostStateCheck(this.hostStateMonitor.isHostAppReady());
        ArrayList<Pair<ExtendedHealthCheck, HealthStatus>> allChecks = new ArrayList<Pair<ExtendedHealthCheck, HealthStatus>>();
        allChecks.add(new Pair<HostStateCheck, HealthStatus>(hostApplicationHealthCheck, hostApplicationHealthCheck.check()));
        if (hostApplicationHealthCheck.check().isHealthy()) {
            allChecks.addAll(this.runChecks(healthChecks));
        }
        return allChecks;
    }

    private Collection<Pair<ExtendedHealthCheck, HealthStatus>> runChecks(Collection<ExtendedHealthCheck> healthChecks) {
        ArrayList<Pair<Future<HealthStatus>, ExtendedHealthCheck>> futurePairs = new ArrayList<Pair<Future<HealthStatus>, ExtendedHealthCheck>>(healthChecks.size());
        for (ExtendedHealthCheck check : healthChecks) {
            Future<HealthStatus> future = this.executorService.submit(new HealthCheckCallable(check));
            futurePairs.add(new Pair<Future<HealthStatus>, ExtendedHealthCheck>(future, check));
        }
        ArrayList<Pair<ExtendedHealthCheck, HealthStatus>> statuses = new ArrayList<Pair<ExtendedHealthCheck, HealthStatus>>();
        for (Pair pair : futurePairs) {
            ExtendedHealthCheck healthCheck = (ExtendedHealthCheck)pair.getRight();
            Future future = (Future)pair.getLeft();
            try {
                statuses.add(new Pair(healthCheck, future.get(healthCheck.getTimeOut(), TimeUnit.MILLISECONDS)));
            }
            catch (InterruptedException e) {
                statuses.add(new Pair<ExtendedHealthCheck, DefaultHealthStatus>(healthCheck, new DefaultHealthStatus(healthCheck, false, "InterruptedException: " + e.getMessage())));
                log.info(e.getMessage(), (Throwable)e);
            }
            catch (ExecutionException e) {
                statuses.add(new Pair<ExtendedHealthCheck, DefaultHealthStatus>(healthCheck, new DefaultHealthStatus(healthCheck, false, "ExecutionException: " + e.getMessage())));
                log.info(e.getMessage(), (Throwable)e);
            }
            catch (TimeoutException e) {
                statuses.add(new Pair<ExtendedHealthCheck, DefaultHealthStatus>(healthCheck, new DefaultHealthStatus(healthCheck, false, "TimeoutException after " + healthCheck.getTimeOut() + "ms")));
                future.cancel(true);
                log.info(e.getMessage(), (Throwable)e);
            }
        }
        return statuses;
    }

    static class HostStateCheck
    implements ExtendedHealthCheck {
        static final String SICK_REASON = "The Host Application has not started yet";
        static final String HEALTHY_REASON = "The Host Application has already started";
        static final String DESCRIPTION = "HealthCheck for Host Application Status";
        static final String KEY = "appStatusHealthCheck";
        static final String NAME = "Host Application Status Check";
        static final String DOCUMENTATION = "https://ecosystem.atlassian.net/browse/AHC-47";
        final long timestamp = new Date().getTime();
        HealthStatus status;

        public HostStateCheck(boolean isHealthy) {
            this.status = new DefaultHealthStatus(isHealthy, isHealthy ? HEALTHY_REASON : SICK_REASON, this.timestamp, Application.Unknown, HealthStatusExtended.Severity.MINOR, DOCUMENTATION);
        }

        @Override
        public int getTimeOut() {
            return 0;
        }

        @Override
        public String getKey() {
            return KEY;
        }

        @Override
        public String getTag() {
            return null;
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public String getDescription() {
            return DESCRIPTION;
        }

        @Override
        public HealthStatus check() {
            return this.status;
        }
    }
}

