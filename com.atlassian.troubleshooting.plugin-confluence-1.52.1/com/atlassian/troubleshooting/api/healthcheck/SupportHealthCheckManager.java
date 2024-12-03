/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckFilter;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface SupportHealthCheckManager {
    @Deprecated
    public Collection<ExtendedSupportHealthCheck> getHealthChecks(HealthCheckFilter var1);

    @Nonnull
    public Collection<ExtendedSupportHealthCheck> getAllHealthChecks();

    @Nonnull
    public List<HealthCheckStatus> runHealthChecks(@Nonnull Collection<ExtendedSupportHealthCheck> var1);

    @Nonnull
    public List<HealthCheckStatus> runAllHealthChecks();

    @Nonnull
    public UUID runAllHealthChecksInBackground() throws RuntimeException;

    public Optional<List<HealthCheckStatus>> getHealthCheckResults(UUID var1);

    public Optional<ExtendedSupportHealthCheck> getHealthCheck(String var1);
}

