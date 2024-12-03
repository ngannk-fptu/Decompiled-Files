/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.healthcheck.core.HealthStatusExtended;

public class DefaultHealthStatus
implements HealthStatusExtended {
    private static final String DEFAULT_DOCUMENTATION = "";
    private final boolean isHealthy;
    private final String failureReason;
    private final Application application;
    private final long time;
    private final HealthStatusExtended.Severity severity;
    private final String documentation;

    @Deprecated
    public DefaultHealthStatus(HealthCheck healthCheck) {
        this(healthCheck, true, null);
    }

    @Deprecated
    public DefaultHealthStatus(HealthCheck healthCheck, boolean isHealthy, String failureReason) {
        this(healthCheck, isHealthy, failureReason, System.currentTimeMillis());
    }

    @Deprecated
    public DefaultHealthStatus(HealthCheck healthCheck, boolean isHealthy, String failureReason, long time) {
        this(isHealthy, failureReason, time, Application.Unknown, HealthStatusExtended.Severity.UNDEFINED, DEFAULT_DOCUMENTATION);
    }

    public DefaultHealthStatus(boolean isHealthy, String failureReason, long time, Application application, HealthStatusExtended.Severity severity, String documentation) {
        this.isHealthy = isHealthy;
        this.failureReason = failureReason;
        this.time = time;
        this.application = application;
        this.severity = severity;
        this.documentation = documentation;
    }

    @Deprecated
    public DefaultHealthStatus(String name, String description, Application application) {
        this(name, description, true, null, application);
    }

    @Deprecated
    public DefaultHealthStatus(String name, String description, boolean isHealthy, String failureReason, Application application) {
        this(name, description, application, isHealthy, failureReason, System.currentTimeMillis());
    }

    @Deprecated
    public DefaultHealthStatus(String name, String description, Application application, boolean isHealthy, String failureReason, long time) {
        this.isHealthy = isHealthy;
        this.failureReason = failureReason;
        this.application = application;
        this.time = time;
        this.severity = HealthStatusExtended.Severity.UNDEFINED;
        this.documentation = DEFAULT_DOCUMENTATION;
    }

    @Override
    @Deprecated
    public String getName() {
        return DEFAULT_DOCUMENTATION;
    }

    @Override
    @Deprecated
    public String getDescription() {
        return DEFAULT_DOCUMENTATION;
    }

    @Override
    public boolean isHealthy() {
        return this.isHealthy;
    }

    @Override
    public String failureReason() {
        return this.failureReason;
    }

    @Override
    public Application getApplication() {
        return this.application;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getDocumentation() {
        return this.documentation;
    }

    @Override
    public HealthStatusExtended.Severity getSeverity() {
        return this.severity;
    }
}

