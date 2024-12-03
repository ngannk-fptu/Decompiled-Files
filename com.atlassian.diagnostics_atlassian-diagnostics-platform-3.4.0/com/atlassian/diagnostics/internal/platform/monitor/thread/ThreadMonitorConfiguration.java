/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.thread;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import javax.annotation.Nonnull;

public interface ThreadMonitorConfiguration
extends MonitorConfiguration {
    public long maxThreadMemoryUsageInBytes();

    public int maxStackTraceDepth();

    @Nonnull
    public ScheduleInterval threadPollerScheduleInterval();
}

