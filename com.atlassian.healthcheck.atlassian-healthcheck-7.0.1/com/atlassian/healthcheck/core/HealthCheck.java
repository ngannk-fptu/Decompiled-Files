/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.HealthStatus;

public interface HealthCheck {
    public HealthStatus check();
}

