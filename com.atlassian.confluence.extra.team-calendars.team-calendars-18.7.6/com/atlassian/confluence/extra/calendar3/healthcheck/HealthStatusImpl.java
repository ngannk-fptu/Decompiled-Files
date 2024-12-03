/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.core.Application
 *  com.atlassian.healthcheck.core.HealthStatus
 */
package com.atlassian.confluence.extra.calendar3.healthcheck;

import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthStatus;

public class HealthStatusImpl
implements HealthStatus {
    private final String name;
    private final String description;
    private final boolean healthy;
    private final String failureReason;
    private final Application application;
    private final long time;

    public HealthStatusImpl(String name, String description, boolean healthy, String failureReason, Application application) {
        this.name = name;
        this.description = description;
        this.healthy = healthy;
        this.failureReason = failureReason;
        this.application = application;
        this.time = System.currentTimeMillis();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isHealthy() {
        return this.healthy;
    }

    public String failureReason() {
        return this.failureReason;
    }

    public Application getApplication() {
        return this.application;
    }

    public long getTime() {
        return this.time;
    }
}

