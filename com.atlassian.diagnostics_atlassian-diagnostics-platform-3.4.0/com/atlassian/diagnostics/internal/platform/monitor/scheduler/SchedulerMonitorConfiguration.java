/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.time.Duration;

public interface SchedulerMonitorConfiguration
extends MonitorConfiguration {
    public ScheduleInterval schedulerPollerScheduleInterval();

    public Duration highUtilizationTimeWindow();
}

