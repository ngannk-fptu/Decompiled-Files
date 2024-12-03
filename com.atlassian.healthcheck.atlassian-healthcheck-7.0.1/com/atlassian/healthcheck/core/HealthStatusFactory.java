/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.DefaultHealthStatus;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.HealthStatusExtended;

public final class HealthStatusFactory {
    private final Application application;
    private String documentation;

    @Deprecated
    public HealthStatusFactory(String name, String description, Application application) {
        this(application, "");
    }

    public HealthStatusFactory(Application application, String documentation) {
        this.application = application;
        this.documentation = documentation;
    }

    public HealthStatus healthy() {
        return new DefaultHealthStatus(true, "", System.currentTimeMillis(), this.application, HealthStatusExtended.Severity.UNDEFINED, this.documentation);
    }

    public HealthStatus healthyWithWarning(String warning) {
        return new DefaultHealthStatus(true, warning, System.currentTimeMillis(), this.application, HealthStatusExtended.Severity.WARNING, this.documentation);
    }

    public HealthStatus healthyWithWarning(String warning, HealthStatusExtended.Severity severity) {
        return new DefaultHealthStatus(true, warning, System.currentTimeMillis(), this.application, severity, this.documentation);
    }

    public HealthStatus failed(String failureReason) {
        return new DefaultHealthStatus(false, failureReason, System.currentTimeMillis(), this.application, HealthStatusExtended.Severity.UNDEFINED, this.documentation);
    }

    public HealthStatus failedAtTime(String failureReason, long time) {
        return new DefaultHealthStatus(false, failureReason, time, this.application, HealthStatusExtended.Severity.UNDEFINED, this.documentation);
    }

    public HealthStatus failed(String failureReason, HealthStatusExtended.Severity severity) {
        return new DefaultHealthStatus(false, failureReason, System.currentTimeMillis(), this.application, severity, this.documentation);
    }
}

