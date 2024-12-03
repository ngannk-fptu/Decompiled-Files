/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.http;

import com.atlassian.diagnostics.MonitorConfiguration;
import java.time.Duration;
import javax.annotation.Nonnull;

public interface HttpMonitorConfiguration
extends MonitorConfiguration {
    @Nonnull
    public Duration getMaximumHttpRequestTime();
}

