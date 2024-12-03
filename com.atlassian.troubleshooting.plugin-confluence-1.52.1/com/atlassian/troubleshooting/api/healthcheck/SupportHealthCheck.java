/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;

public interface SupportHealthCheck {
    default public boolean isNodeSpecific() {
        return false;
    }

    public SupportHealthStatus check();
}

