/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.ClusterMonitoring
 *  com.atlassian.cluster.monitoring.spi.model.MonitoringError
 *  com.atlassian.cluster.monitoring.spi.model.NodeIdentifier
 *  com.atlassian.cluster.monitoring.spi.model.NodeInformation
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Preconditions
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IExecutorService
 *  com.hazelcast.core.LifecycleListener
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MembershipListener
 *  io.atlassian.fugue.Either
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast.monitoring;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.ClusterMonitoring;
import com.atlassian.cluster.monitoring.spi.model.MonitoringError;
import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import com.atlassian.cluster.monitoring.spi.model.NodeInformation;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.confluence.cluster.hazelcast.HazelcastUtils;
import com.atlassian.confluence.cluster.hazelcast.monitoring.HazelcastLifecycleListener;
import com.atlassian.confluence.cluster.hazelcast.monitoring.HazelcastMembershipListener;
import com.atlassian.confluence.cluster.hazelcast.monitoring.RemoteModuleCallable;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Preconditions;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import io.atlassian.fugue.Either;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class HazelcastClusterMonitoring
extends InitializingMonitor
implements ClusterMonitoring {
    static final int MEMBER_ADDED_ID = 1001;
    static final int MEMBER_REMOVED_ID = 1002;
    private static final Logger log = LoggerFactory.getLogger(HazelcastClusterMonitoring.class);
    private static final long TIMEOUT_DURATION = 5L;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final String MONITORING_EXECUTOR_KEY = "cluster-monitoring-executor";
    private static final String MONITOR_ID = "HAZELCAST";
    private final HazelcastInstance hazelcastInstance;
    private final IExecutorService executor;
    private final EventPublisher eventPublisher;
    private volatile MonitoringService monitoringService;

    public HazelcastClusterMonitoring(Supplier<HazelcastInstance> instanceSupplier, EventPublisher eventPublisher) {
        this.hazelcastInstance = (HazelcastInstance)Preconditions.checkNotNull((Object)((HazelcastInstance)((Supplier)Preconditions.checkNotNull(instanceSupplier)).get()));
        this.executor = (IExecutorService)Preconditions.checkNotNull((Object)this.hazelcastInstance.getExecutorService(MONITORING_EXECUTOR_KEY));
        this.eventPublisher = eventPublisher;
    }

    @PreDestroy
    public void preDestroy() {
        if (this.monitoringService != null) {
            this.monitoringService.destroyMonitor(MONITOR_ID);
        }
        log.debug("{} monitor has been destroyed", (Object)MONITOR_ID);
    }

    public Either<MonitoringError, NodeIdentifier> getCurrentNode() {
        Member member = this.hazelcastInstance.getCluster().getLocalMember();
        return Either.right((Object)HazelcastUtils.extractNodeId().apply(member));
    }

    public Either<MonitoringError, List<NodeInformation>> getNodes() {
        Set members = this.hazelcastInstance.getCluster().getMembers();
        List nodeInfos = members.stream().map(HazelcastUtils.extractNodeInfo()).sorted().collect(Collectors.toList());
        return Either.right(nodeInfos);
    }

    public @NonNull Either<MonitoringError, Table> getData(ModuleCompleteKey key, NodeIdentifier nodeId) {
        try {
            Future future = this.executor.submit((Callable)new RemoteModuleCallable(key), HazelcastUtils.getMemberSelector(nodeId));
            return Either.right((Object)((Table)future.get(5L, TIMEOUT_UNIT)));
        }
        catch (Exception e) {
            log.warn("Exception happened when receiving response from node {}", (Object)nodeId, (Object)e);
            MonitoringError error = new MonitoringError(e.getMessage());
            return Either.left((Object)error);
        }
    }

    public boolean isAvailable() {
        return true;
    }

    public boolean isDataCenterLicensed() {
        return true;
    }

    public boolean enableClustering() {
        return false;
    }

    public boolean isClusterSetupEnabled() {
        return false;
    }

    public void init(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.hazelcast.name", () -> true);
        this.defineIssue("diagnostics.hazelcast.issue", 1001, Severity.INFO);
        this.defineIssue("diagnostics.hazelcast.issue", 1002, Severity.WARNING);
        this.hazelcastInstance.getCluster().addMembershipListener((MembershipListener)new HazelcastMembershipListener(this.monitor, this.eventPublisher));
        this.hazelcastInstance.getLifecycleService().addLifecycleListener((LifecycleListener)new HazelcastLifecycleListener(this.monitor));
        log.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }
}

