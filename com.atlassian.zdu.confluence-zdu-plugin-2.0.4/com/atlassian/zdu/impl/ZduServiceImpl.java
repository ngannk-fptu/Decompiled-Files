/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.zdu.NodeInfoAccessor;
import com.atlassian.zdu.api.ZduService;
import com.atlassian.zdu.event.BuildInfo;
import com.atlassian.zdu.event.ZduApprovedEvent;
import com.atlassian.zdu.event.ZduCancelledEvent;
import com.atlassian.zdu.event.ZduCompletedEvent;
import com.atlassian.zdu.event.ZduRetryEvent;
import com.atlassian.zdu.event.ZduStartedEvent;
import com.atlassian.zdu.exception.InvalidStateTransitionException;
import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.atlassian.zdu.internal.api.RollingUpgradeService;
import com.atlassian.zdu.persistence.ZduNodeRepository;
import com.atlassian.zdu.rest.dto.Cluster;
import com.atlassian.zdu.rest.dto.ClusterState;
import com.atlassian.zdu.rest.dto.ClusterStateResponse;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.atlassian.zdu.rest.dto.NodeState;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ZduServiceImpl
implements ZduService {
    private final ClusterManagerAdapter clusterManagerAdapter;
    private final RollingUpgradeService rollingUpgradeService;
    private final NodeInfoAccessor nodeInfoAccessor;
    private final ZduNodeRepository nodeRepository;
    private final EventPublisher eventPublisher;
    private final ApplicationProperties applicationProperties;

    public ZduServiceImpl(@Nonnull ClusterManagerAdapter clusterManagerAdapter, @Nonnull NodeInfoAccessor nodeInfoAccessor, @Nonnull RollingUpgradeService rollingUpgradeService, @Nonnull ZduNodeRepository nodeRepository, @Nonnull EventPublisher eventPublisher, @Nonnull ApplicationProperties applicationProperties) {
        this.clusterManagerAdapter = Objects.requireNonNull(clusterManagerAdapter);
        this.rollingUpgradeService = Objects.requireNonNull(rollingUpgradeService);
        this.nodeInfoAccessor = Objects.requireNonNull(nodeInfoAccessor);
        this.nodeRepository = Objects.requireNonNull(nodeRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
    }

    @Override
    public List<NodeInfoDTO> getNodes() {
        return this.nodeInfoAccessor.getNodes().stream().sorted(Comparator.comparing(NodeInfoDTO::getName).thenComparing(NodeInfoDTO::getIpAddress)).collect(Collectors.toList());
    }

    @Override
    public Optional<NodeInfoDTO> getNode(String nodeId) {
        return this.getNodes().stream().filter(node -> node.getId().equals(nodeId)).findAny();
    }

    @Override
    public ClusterState getState() {
        return this.getCluster().getState();
    }

    @Override
    public ClusterStateResponse getClusterStateResponse() {
        Cluster cluster = this.getCluster();
        return new ClusterStateResponse(cluster.getState(), cluster.getNodes().stream().filter(NodeInfoDTO::isLocal).findFirst().orElseThrow(() -> new RuntimeException("failed to find local node")));
    }

    private ClusterState calculateClusterState(List<NodeInfoDTO> nodes) {
        if (this.anyNodesMatchState(nodes, NodeState.RUNNING_FINALIZE_UPGRADE_TASKS)) {
            return ClusterState.RUNNING_UPGRADE_TASKS;
        }
        if (!this.rollingUpgradeService.isUpgradeModeEnabled()) {
            if (this.anyNodesMatchState(nodes, NodeState.UPGRADE_TASKS_FAILED) || this.clusterManagerAdapter.hasClusterFinalizationTasks()) {
                return ClusterState.UPGRADE_TASKS_FAILED;
            }
            return ClusterState.STABLE;
        }
        if (this.anyNodesMatchState(nodes, NodeState.ERROR)) {
            return ClusterState.MIXED;
        }
        Set versions = nodes.stream().filter(n -> n.getState() != NodeState.OFFLINE).map(NodeInfoDTO::getVersion).collect(Collectors.toSet());
        if (versions.size() > 1) {
            return ClusterState.MIXED;
        }
        String originVersion = this.rollingUpgradeService.getOriginalVersion().orElseThrow(() -> new IllegalStateException("in upgrade mode but original version not present"));
        if (versions.stream().allMatch(version -> Objects.equals(version, originVersion))) {
            return ClusterState.READY_TO_UPGRADE;
        }
        return ClusterState.READY_TO_RUN_UPGRADE_TASKS;
    }

    @Override
    public Cluster getCluster() {
        List<NodeInfoDTO> nodes = this.getNodes();
        ClusterState state = this.calculateClusterState(nodes);
        boolean hasFinalizationTasks = this.clusterManagerAdapter.hasClusterFinalizationTasks();
        return new Cluster(this.rollingUpgradeService.isUpgradeModeEnabled(), hasFinalizationTasks, state, this.rollingUpgradeService.getOriginalVersion().orElse(null), nodes);
    }

    private boolean anyNodesMatchState(List<NodeInfoDTO> nodes, NodeState nodeState) {
        return nodes.stream().anyMatch(n -> n.getState() == nodeState);
    }

    @Override
    public Cluster retryFinalization() {
        if (!this.getState().canRetry()) {
            throw new InvalidStateTransitionException("retry");
        }
        this.rollingUpgradeService.retryFinalization();
        Cluster cluster = this.getCluster();
        this.eventPublisher.publish((Object)new ZduRetryEvent(this.getNodeCount(cluster), this.getCurrentBuild()));
        return cluster;
    }

    @Override
    public Cluster startZdu() {
        if (!this.getState().canStart()) {
            throw new InvalidStateTransitionException("start");
        }
        this.rollingUpgradeService.enableUpgradeMode();
        this.nodeInfoAccessor.snapshotNodes();
        Cluster cluster = this.getCluster();
        this.eventPublisher.publish((Object)new ZduStartedEvent(this.getNodeCount(cluster), this.getCurrentBuild()));
        return cluster;
    }

    @Override
    public Cluster cancelZdu() {
        if (!this.getState().canCancel()) {
            throw new InvalidStateTransitionException("cancel");
        }
        this.nodeRepository.cleanAll();
        this.rollingUpgradeService.disableUpgradeMode();
        Cluster cluster = this.getCluster();
        this.eventPublisher.publish((Object)new ZduCancelledEvent(this.getNodeCount(cluster), this.getCurrentBuild()));
        return cluster;
    }

    @Override
    public Cluster finalizeZdu() {
        if (!this.getState().canFinalize()) {
            throw new InvalidStateTransitionException("finalize");
        }
        BuildInfo originalBuild = this.getOriginalBuild();
        this.nodeRepository.cleanAll();
        this.rollingUpgradeService.disableUpgradeMode();
        Cluster cluster = this.getCluster();
        if (cluster.hasFinalizationTasks()) {
            this.eventPublisher.publish((Object)new ZduApprovedEvent(this.getNodeCount(cluster), originalBuild, this.getCurrentBuild()));
        } else {
            this.eventPublisher.publish((Object)new ZduCompletedEvent(this.getNodeCount(cluster), originalBuild, this.getCurrentBuild()));
        }
        return cluster;
    }

    private long getNodeCount(Cluster cluster) {
        return cluster.getNodes().stream().filter(n -> n.getState() == NodeState.ACTIVE).count();
    }

    private BuildInfo getOriginalBuild() {
        return this.rollingUpgradeService.getOriginalVersion().map(BuildInfo::new).orElse(null);
    }

    private BuildInfo getCurrentBuild() {
        return new BuildInfo(this.applicationProperties.getVersion());
    }
}

