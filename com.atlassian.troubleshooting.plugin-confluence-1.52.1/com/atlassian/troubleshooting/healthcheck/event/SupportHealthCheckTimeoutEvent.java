/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.healthcheck.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="healthcheck.run.timed.out")
public class SupportHealthCheckTimeoutEvent {
    private final String applicationName;
    private final String healthCheckName;
    private final int timeout;
    private final String productVersion;

    public SupportHealthCheckTimeoutEvent(String applicationName, String healthCheckName, int timeout, String productVersion) {
        this.applicationName = applicationName;
        this.healthCheckName = healthCheckName;
        this.timeout = timeout;
        this.productVersion = productVersion;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getHealthCheckName() {
        return this.healthCheckName;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getProductVersion() {
        return this.productVersion;
    }
}

