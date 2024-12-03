/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.cluster;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import javax.annotation.Nonnull;

public interface ClusterLockMonitorConfiguration
extends MonitorConfiguration {
    public int lockWaitTimeThreshold();

    public int queueSizeThreshold();

    @Nonnull
    public ScheduleInterval clusterLockPollerScheduleInterval();
}

