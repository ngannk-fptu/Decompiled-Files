/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.time.Duration;

public interface GarbageCollectionMonitorConfiguration
extends MonitorConfiguration {
    public double getWarningThreshold();

    public double getErrorThreshold();

    public ScheduleInterval garbageCollectionPollerScheduleInterval();

    public Duration slidingWindowSize();

    public boolean shouldIncludeTopThreadMemoryAllocationsInDetails();
}

