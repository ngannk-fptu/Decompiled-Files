/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core.impl;

import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheckModuleDescriptorNotFoundException;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.impl.Pair;
import java.util.Collection;
import java.util.Set;

public interface HealthCheckManager {
    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecks();

    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecksWithKeys(Set<String> var1) throws HealthCheckModuleDescriptorNotFoundException;

    public Collection<Pair<ExtendedHealthCheck, HealthStatus>> performChecksWithTags(Set<String> var1);

    public Collection<ExtendedHealthCheck> getHealthChecks();

    public Collection<ExtendedHealthCheck> getHealthChecksWithKeys(Set<String> var1) throws HealthCheckModuleDescriptorNotFoundException;

    public Collection<ExtendedHealthCheck> getHealthChecksWithTags(Set<String> var1);
}

