/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.NodeZduInfo;
import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.admin.ZduStartEvent;
import com.atlassian.confluence.event.events.cluster.ZduFinalizationRequestEvent;
import com.atlassian.confluence.impl.cluster.CollectNodeZduInfo;
import com.atlassian.confluence.internal.ZduStatusEntity;
import com.atlassian.confluence.internal.persistence.ZduStatusDao;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class DefaultZduManager
implements ZduManager {
    private final ZduStatusDao zduStatusDao;
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;
    private final ClusterManager clusterManager;
    private final UpgradeFinalizationManager finalizationManager;
    private final SynchronizationManager synchronizationManager;

    public DefaultZduManager(ZduStatusDao zduStatusDao, ApplicationProperties applicationProperties, EventPublisher eventPublisher, ClusterManager clusterManager, UpgradeFinalizationManager finalizationManager, SynchronizationManager synchronizationManager) {
        this.zduStatusDao = zduStatusDao;
        this.applicationProperties = applicationProperties;
        this.eventPublisher = eventPublisher;
        this.clusterManager = clusterManager;
        this.finalizationManager = finalizationManager;
        this.synchronizationManager = synchronizationManager;
    }

    @Override
    public ZduStatus getUpgradeStatus() {
        return this.zduStatusDao.getStatus().map(this::toStatus).orElseGet(ZduStatus::disabled);
    }

    @Override
    public ZduStatus startUpgrade() {
        String originalClusterVersion = this.applicationProperties.getVersion();
        ZduStatusEntity status = new ZduStatusEntity(ZduStatus.State.ENABLED, originalClusterVersion, Integer.parseInt(GeneralUtil.getBuildNumber()));
        this.zduStatusDao.setStatus(status);
        this.eventPublisher.publish((Object)new ZduStartEvent(this));
        return this.toStatus(status);
    }

    @Override
    public void endUpgrade() {
        this.zduStatusDao.deleteStatus();
        this.requestFinalization();
    }

    @Override
    public void retryFinalization() {
        this.requestFinalization();
    }

    @Override
    public boolean isPendingDatabaseFinalization() {
        return this.finalizationManager.isPendingDatabaseFinalization();
    }

    @Override
    public Map<ClusterNodeInformation, CompletionStage<NodeZduInfo>> getNodesZduInfo() {
        if (!this.clusterManager.isClustered()) {
            return Collections.emptyMap();
        }
        return this.clusterManager.submitToAllNodes(new CollectNodeZduInfo(), "cluster-manager-executor").stream().collect(Collectors.toMap(e -> Objects.requireNonNull(e.getClusterNode()), e -> e.getCompletionStage()));
    }

    private void requestFinalization() {
        this.synchronizationManager.runOnSuccessfulCommit(() -> this.eventPublisher.publish((Object)new ZduFinalizationRequestEvent(this)));
    }

    private ZduStatus toStatus(ZduStatusEntity entity) {
        return new ZduStatus(entity.getState(), entity.getOriginalClusterVersion());
    }
}

