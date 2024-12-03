/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.PostConstruct
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.internal.health.HealthCheck;
import com.atlassian.confluence.internal.health.HealthCheckRegistry;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;

public class HealthCheckRegistrar {
    private final Collection<HealthCheck> healthChecks;
    private final HealthCheckRegistry healthCheckRegistry;

    public HealthCheckRegistrar(HealthCheckRegistry healthCheckRegistry, List<HealthCheck> healthChecks) {
        this.healthCheckRegistry = Objects.requireNonNull(healthCheckRegistry);
        this.healthChecks = ImmutableList.copyOf((Collection)Objects.requireNonNull(healthChecks));
    }

    @PostConstruct
    public void registerHealthChecks() {
        this.healthChecks.forEach(this.healthCheckRegistry::register);
        this.healthCheckRegistry.registrationComplete();
    }
}

