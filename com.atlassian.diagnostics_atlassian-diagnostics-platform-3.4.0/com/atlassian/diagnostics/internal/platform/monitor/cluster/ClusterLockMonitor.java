/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.core.ManagedClusterLock
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.cluster;

import com.atlassian.beehive.core.ManagedClusterLock;
import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.cluster.ClusterLockMonitorConfiguration;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ClusterLockMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.clusterlock.issue";
    private static final int LONG_LOCK_WAIT = 2001;
    private static final int LARGE_LOCK_QUEUE = 2002;
    private final ClusterLockMonitorConfiguration clusterLockMonitorConfiguration;

    public ClusterLockMonitor(ClusterLockMonitorConfiguration clusterLockMonitorConfiguration) {
        this.clusterLockMonitorConfiguration = clusterLockMonitorConfiguration;
    }

    public void init(@Nonnull MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("CLUSTERLOCK", "diagnostics.clusterlock.name", (MonitorConfiguration)this.clusterLockMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
        this.defineIssue(KEY_PREFIX, 2002, Severity.WARNING);
    }

    public void raiseAlertForLocksWithLongWait(@Nonnull Instant timestamp, @Nonnull Collection<ManagedClusterLock> locksWithLongWait) {
        this.alert(2001, builder -> builder.timestamp(timestamp).details(() -> this.lockAlertDetails(locksWithLongWait)));
    }

    public void raiseAlertForLargeQueue(@Nonnull Instant timestamp, @Nonnull Collection<ManagedClusterLock> locksWithLargeQueue) {
        this.alert(2002, builder -> builder.timestamp(timestamp).details(() -> this.lockAlertDetails(locksWithLargeQueue)));
    }

    private Map<Object, Object> lockAlertDetails(@Nonnull Collection<ManagedClusterLock> longHeldLockDiagnostics) {
        List locks = longHeldLockDiagnostics.stream().map(lock -> {
            ImmutableMap.Builder alertBuilder = ImmutableMap.builder().put((Object)"lockedByNode", (Object)lock.getClusterLockStatus().getLockedByNode()).put((Object)"lockName", (Object)lock.getClusterLockStatus().getLockName()).put((Object)"lastUpdateTimeInMillis", (Object)lock.getClusterLockStatus().getUpdateTime());
            lock.getStatistics().forEach((key, value) -> alertBuilder.put((Object)key.getLabel(), value));
            return alertBuilder.build();
        }).collect(Collectors.toList());
        return ImmutableMap.builder().put((Object)"locks", locks).build();
    }
}

