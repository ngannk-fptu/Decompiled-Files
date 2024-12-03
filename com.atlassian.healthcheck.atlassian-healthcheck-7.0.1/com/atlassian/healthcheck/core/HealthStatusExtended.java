/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.HealthStatus;

public interface HealthStatusExtended
extends HealthStatus {
    public String getDocumentation();

    public Severity getSeverity();

    public static enum Severity {
        UNDEFINED,
        MINOR,
        MAJOR,
        WARNING,
        CRITICAL;


        public boolean isMoreSevereThan(Severity severity) {
            return this.compareTo(severity) > 0;
        }
    }
}

