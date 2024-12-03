/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheckModuleDescriptorNotFoundException;
import java.util.Collection;
import java.util.Set;

public interface HealthCheckSupplier {
    public Collection<ExtendedHealthCheck> getHealthChecks();

    public Collection<ExtendedHealthCheck> getHealthChecksWithKeys(Set<String> var1) throws HealthCheckModuleDescriptorNotFoundException;

    public Collection<ExtendedHealthCheck> getHealthChecksWithTags(Set<String> var1);
}

