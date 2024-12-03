/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.Application;

public interface HealthStatus {
    @Deprecated
    public String getName();

    @Deprecated
    public String getDescription();

    public boolean isHealthy();

    public String failureReason();

    public Application getApplication();

    public long getTime();
}

