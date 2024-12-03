/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.cluster.NodeZduInfo
 *  com.atlassian.confluence.cluster.UpgradeFinalizationRun$Error
 *  com.atlassian.confluence.cluster.ZduManager
 *  com.atlassian.confluence.cluster.ZduStatus$State
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.zdu.confluence.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.UpgradeFinalizationRun;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.atlassian.zdu.internal.api.NodeInfo;
import com.atlassian.zdu.internal.api.UpgradeTaskError;
import com.atlassian.zdu.rest.dto.FinalizationUpgradeTaskErrorDTO;
import com.atlassian.zdu.rest.dto.NodeFinalizationInfoDTO;
import com.atlassian.zdu.rest.dto.NodeInfoDTO;
import com.atlassian.zdu.rest.dto.NodeState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceClusterManagerAdapter
implements ClusterManagerAdapter {
    static final Logger log = LoggerFactory.getLogger(ConfluenceClusterManagerAdapter.class);
    private final ClusterManager clusterManager;
    private final ZduManager zduManager;

    public ConfluenceClusterManagerAdapter(@Nonnull ClusterManager clusterManager, @Nonnull ZduManager zduManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.zduManager = Objects.requireNonNull(zduManager);
    }

    @Override
    public List<NodeInfo> getNodes() {
        ClusterNodeInformation thisNode = this.clusterManager.getThisNodeInformation();
        return ((Stream)this.zduManager.getNodesZduInfo().entrySet().stream().parallel()).map(e -> {
            ClusterNodeInformation node = (ClusterNodeInformation)e.getKey();
            NodeInfoDTO.Builder builder = ConfluenceClusterManagerAdapter.buildNodeInfo(node);
            try {
                NodeZduInfo info = (NodeZduInfo)((CompletionStage)e.getValue()).toCompletableFuture().get(5L, TimeUnit.SECONDS);
                builder.state(this.determineNodeState(info)).buildNumber(info.getBuildNumber()).version(info.getVersion()).local(Objects.equals(thisNode, node)).tasksTotal(info.getLongRunningTaskCount()).activeUserCount(info.getActiveUserCount()).finalization(info.getFinalizationRun().map(run -> NodeFinalizationInfoDTO.builder().lastRequested(new Date(run.getRequestTimestamp())).runsClusterWideTasks(run.runsClusterWideTasks()).errors(run.getErrors().stream().map(ConfluenceClusterManagerAdapter::convertToUpgradeTaskError).collect(Collectors.toList())).build()).orElse(null));
            }
            catch (Exception ex) {
                log.warn("Failed to retrieve status information from cluster node {}. This can happen temporarily if the node is currently being started/terminated for upgrade. If the problem persists, check the application logs on that node for more details.", e.getKey());
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isClustered() {
        return this.clusterManager.isClustered();
    }

    @Override
    public boolean hasClusterFinalizationTasks() {
        return this.zduManager.isPendingDatabaseFinalization();
    }

    private static NodeInfoDTO.Builder buildNodeInfo(ClusterNodeInformation node) {
        return NodeInfoDTO.builder().id(node.getAnonymizedNodeIdentifier()).name(node.humanReadableNodeName().orElse(null)).ipAddress(node.getLocalSocketAddress().getHostString()).portNumber(node.getLocalSocketAddress().getPort());
    }

    private NodeState determineNodeState(NodeZduInfo info) {
        boolean isUpgradeModeEnabled = this.zduManager.getUpgradeStatus().getState() == ZduStatus.State.ENABLED;
        NodeState state = NodeState.ACTIVE;
        switch (info.getApplicationState()) {
            case FIRST_RUN: 
            case ERROR: {
                state = NodeState.ERROR;
                break;
            }
            case STARTING: {
                state = NodeState.STARTING;
                break;
            }
            case STOPPING: {
                state = NodeState.TERMINATING;
                break;
            }
            case RUNNING: {
                if (isUpgradeModeEnabled) {
                    state = NodeState.ACTIVE;
                    break;
                }
                boolean hasPendingTasks = info.isPendingLocalFinalization() || this.hasClusterFinalizationTasks();
                state = info.getFinalizationRun().filter(r -> r.isCompleted()).map(r -> r.isFailed() ? NodeState.UPGRADE_TASKS_FAILED : NodeState.ACTIVE).orElse(hasPendingTasks ? NodeState.RUNNING_FINALIZE_UPGRADE_TASKS : NodeState.ACTIVE);
                break;
            }
            default: {
                log.warn("Unrecognized Application state: [{}]", (Object)info.getApplicationState());
            }
        }
        return state;
    }

    @VisibleForTesting
    static UpgradeTaskError convertToUpgradeTaskError(UpgradeFinalizationRun.Error error) {
        return new FinalizationUpgradeTaskErrorDTO(error.getUpgradeTaskName(), error.getExceptionMessage(), error.isClusterWideTask(), new ArrayList<String>(error.getUpgradeErrors()));
    }
}

