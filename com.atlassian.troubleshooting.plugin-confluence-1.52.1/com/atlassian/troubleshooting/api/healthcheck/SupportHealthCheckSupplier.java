/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckFilter;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.exception.SupportHealthCheckModuleDescriptorNotFoundException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

public interface SupportHealthCheckSupplier {
    public Collection<ExtendedSupportHealthCheck> getHealthChecks(HealthCheckFilter var1);

    public Optional<ExtendedSupportHealthCheck> getHealthCheck(String var1);

    @Deprecated
    public Collection<ExtendedSupportHealthCheck> getHealthChecks();

    @Deprecated
    public Collection<ExtendedSupportHealthCheck> getHealthChecksWithKeys(Set<String> var1) throws SupportHealthCheckModuleDescriptorNotFoundException;

    @Deprecated
    public Collection<ExtendedSupportHealthCheck> getHealthChecksWithTags(Set<String> var1);

    public Optional<ExtendedSupportHealthCheck> byInstance(@Nonnull SupportHealthCheck var1);

    public Optional<String> getHelpPathKey(@Nonnull SupportHealthCheck var1);
}

