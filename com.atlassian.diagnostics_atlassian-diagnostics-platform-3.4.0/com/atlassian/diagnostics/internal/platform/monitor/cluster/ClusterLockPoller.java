/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.core.ManagedClusterLock
 *  com.atlassian.beehive.core.ManagedClusterLockService
 *  com.atlassian.beehive.core.stats.StatisticsKey
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 */
package com.atlassian.diagnostics.internal.platform.monitor.cluster;

import com.atlassian.beehive.core.ManagedClusterLock;
import com.atlassian.beehive.core.ManagedClusterLockService;
import com.atlassian.beehive.core.stats.StatisticsKey;
import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.cluster.ClusterLockMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.cluster.ClusterLockMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClusterLockPoller
extends DiagnosticPoller<ClusterLockMonitorConfiguration> {
    private final ClusterLockMonitor clusterLockMonitor;
    private final ManagedClusterLockService clusterLockService;
    private final DiagnosticsConfiguration diagnosticsConfiguration;

    public ClusterLockPoller(ClusterLockMonitor clusterLockMonitor, ManagedClusterLockService clusterLockService, ClusterLockMonitorConfiguration clusterLockMonitorConfiguration, DiagnosticsConfiguration diagnosticsConfiguration) {
        super(ClusterLockPoller.class.getName(), clusterLockMonitorConfiguration);
        this.clusterLockMonitor = clusterLockMonitor;
        this.clusterLockService = clusterLockService;
        this.diagnosticsConfiguration = diagnosticsConfiguration;
    }

    @Override
    protected void execute() {
        Collection locks = this.clusterLockService.getAllKnownClusterLocks().stream().filter(this.isLockedOnThisNode()).collect(Collectors.toList());
        this.raiseAlertIfLongWaitForLock(locks);
        this.raiseAlertIfQueueSizeIsHigh(locks);
    }

    private Predicate<ManagedClusterLock> isLockedOnThisNode() {
        return managedClusterLock -> this.diagnosticsConfiguration.getNodeName().equals(managedClusterLock.getClusterLockStatus().getLockedByNode()) && managedClusterLock.isLocked();
    }

    private void raiseAlertIfLongWaitForLock(Collection<ManagedClusterLock> clusterLocks) {
        List<ManagedClusterLock> locksWithLongHoldTime = clusterLocks.stream().filter(this.isWaitTimeAboveThreshold()).collect(Collectors.toList());
        if (!locksWithLongHoldTime.isEmpty()) {
            this.clusterLockMonitor.raiseAlertForLocksWithLongWait(Instant.now(), locksWithLongHoldTime);
        }
    }

    private Predicate<ManagedClusterLock> isWaitTimeAboveThreshold() {
        return managedClusterLock -> {
            Long averageWaitTimeForLock = managedClusterLock.getStatistics().getOrDefault(StatisticsKey.AVERAGE_WAIT_TIME_MILLIS, 0L);
            Long queueSize = managedClusterLock.getStatistics().getOrDefault(StatisticsKey.WAIT_QUEUE_LENGTH, 0L);
            return averageWaitTimeForLock * queueSize >= (long)((ClusterLockMonitorConfiguration)this.monitorConfiguration).lockWaitTimeThreshold();
        };
    }

    private void raiseAlertIfQueueSizeIsHigh(Collection<? extends ManagedClusterLock> clusterLocks) {
        List<ManagedClusterLock> locksWithLongWaits = clusterLocks.stream().filter(this.isQueueSizeAboveThreshold()).collect(Collectors.toList());
        if (!locksWithLongWaits.isEmpty()) {
            this.clusterLockMonitor.raiseAlertForLargeQueue(Instant.now(), locksWithLongWaits);
        }
    }

    private Predicate<ManagedClusterLock> isQueueSizeAboveThreshold() {
        return clusterLock -> clusterLock.getStatistics().getOrDefault(StatisticsKey.WAIT_QUEUE_LENGTH, 0L) >= (long)((ClusterLockMonitorConfiguration)this.monitorConfiguration).queueSizeThreshold();
    }
}

