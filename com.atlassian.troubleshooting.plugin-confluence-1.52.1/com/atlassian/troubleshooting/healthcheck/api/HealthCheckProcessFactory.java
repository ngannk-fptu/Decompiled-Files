/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.api;

import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.concurrent.SupportHealthCheckProcess;
import java.util.Collection;
import javax.annotation.Nonnull;

public interface HealthCheckProcessFactory {
    @Nonnull
    public SupportHealthCheckProcess createProcess(@Nonnull Collection<ExtendedSupportHealthCheck> var1);
}

