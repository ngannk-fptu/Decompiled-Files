/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckFilter;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckSupplier;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckProcessFactory;
import com.atlassian.troubleshooting.healthcheck.concurrent.SupportHealthCheckProcess;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSupportHealthCheckManager
implements SupportHealthCheckManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSupportHealthCheckManager.class);
    private final Cache<UUID, SupportHealthCheckProcess> processes = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS).build();
    private final SupportHealthCheckSupplier healthCheckSupplier;
    private final HealthCheckProcessFactory processFactory;

    @Autowired
    public DefaultSupportHealthCheckManager(@Nonnull HealthCheckProcessFactory processFactory, @Nonnull SupportHealthCheckSupplier healthCheckSupplier) {
        this.processFactory = Objects.requireNonNull(processFactory);
        this.healthCheckSupplier = Objects.requireNonNull(healthCheckSupplier);
    }

    @Override
    public Collection<ExtendedSupportHealthCheck> getHealthChecks(HealthCheckFilter filter) {
        return this.healthCheckSupplier.getHealthChecks(filter);
    }

    @Override
    @Nonnull
    public Collection<ExtendedSupportHealthCheck> getAllHealthChecks() {
        return this.getHealthChecks(HealthCheckFilter.ALL);
    }

    @Override
    @Nonnull
    public List<HealthCheckStatus> runHealthChecks(@Nonnull Collection<ExtendedSupportHealthCheck> healthChecks) {
        Objects.requireNonNull(healthChecks);
        return this.processFactory.createProcess(healthChecks).runSync();
    }

    @Override
    @Nonnull
    public List<HealthCheckStatus> runAllHealthChecks() {
        return this.runHealthChecks(this.getAllHealthChecks());
    }

    @Override
    @Nonnull
    public UUID runAllHealthChecksInBackground() {
        try {
            UUID uuid = UUID.randomUUID();
            SupportHealthCheckProcess process = this.processFactory.createProcess(this.getAllHealthChecks());
            process.runAsync();
            this.processes.put((Object)uuid, (Object)process);
            return uuid;
        }
        catch (Exception e) {
            LOGGER.error("Unable to start the health check process", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<HealthCheckStatus>> getHealthCheckResults(UUID uuid) {
        return Optional.ofNullable(this.processes.getIfPresent((Object)uuid)).map(SupportHealthCheckProcess::getCompletedStatuses);
    }

    @Override
    public Optional<ExtendedSupportHealthCheck> getHealthCheck(String healthCheckKey) {
        return this.healthCheckSupplier.getHealthCheck(healthCheckKey);
    }
}

