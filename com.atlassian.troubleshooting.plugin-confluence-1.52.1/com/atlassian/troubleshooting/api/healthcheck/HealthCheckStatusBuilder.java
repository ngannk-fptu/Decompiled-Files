/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import java.io.Serializable;

public interface HealthCheckStatusBuilder {
    public SupportHealthStatus ok(SupportHealthCheck var1, String var2, Serializable ... var3);

    public SupportHealthStatus warning(SupportHealthCheck var1, String var2, Serializable ... var3);

    public SupportHealthStatus major(SupportHealthCheck var1, String var2, Serializable ... var3);

    public SupportHealthStatus critical(SupportHealthCheck var1, String var2, Serializable ... var3);

    public SupportHealthStatus disabled(SupportHealthCheck var1, String var2, Serializable ... var3);
}

