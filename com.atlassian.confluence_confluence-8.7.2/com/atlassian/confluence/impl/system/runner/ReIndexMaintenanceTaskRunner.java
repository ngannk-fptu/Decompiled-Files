/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections4.ListUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.system.runner;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.impl.system.MaintenanceTaskExecutionException;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskRegistry;
import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.ReIndexMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexJobManager;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.internal.index.EventPublishingReindexProgress;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReIndexMaintenanceTaskRunner
implements SystemMaintenanceTaskRunner<ReIndexMaintenanceTask> {
    private static final Logger log = LoggerFactory.getLogger(ReIndexMaintenanceTaskRunner.class);
    private final SystemMaintenanceTaskRegistry registry;
    private final IndexManager indexManager;
    private final ClusterManager clusterManager;
    private final ReIndexJobManager reIndexJobManager;
    private final SearchPlatformConfig searchPlatformConfig;
    private final EventPublisher eventPublisher;

    public ReIndexMaintenanceTaskRunner(SystemMaintenanceTaskRegistry registry, IndexManager indexManager, ClusterManager clusterManager, ReIndexJobManager reIndexJobManager, SearchPlatformConfig searchPlatformConfig, EventPublisher eventPublisher) {
        this.registry = Objects.requireNonNull(registry);
        this.indexManager = Objects.requireNonNull(indexManager);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.reIndexJobManager = Objects.requireNonNull(reIndexJobManager);
        this.searchPlatformConfig = Objects.requireNonNull(searchPlatformConfig);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void register() {
        this.registry.register(SystemMaintenanceTaskType.REINDEX, this);
    }

    @PreDestroy
    public void unregister() {
        this.registry.unregister(SystemMaintenanceTaskType.REINDEX);
    }

    @Override
    public void execute(ReIndexMaintenanceTask task) throws MaintenanceTaskExecutionException {
        if (this.shouldReIndex(task)) {
            if (this.shouldSkip(task)) {
                new EventPublishingReindexProgress(this.eventPublisher, null).reIndexSkipped();
                return;
            }
            this.indexManager.reIndex(task.getOptions(), task.getSpaceKeys());
        }
    }

    private boolean shouldReIndex(ReIndexMaintenanceTask task) {
        boolean isSiteReIndex = ListUtils.emptyIfNull(task.getSpaceKeys()).isEmpty();
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        Optional<ReIndexJob> currentJobOptional = this.reIndexJobManager.getRunningOrMostRecentReIndex();
        if (currentJobOptional.isEmpty()) {
            log.error("Reindex job not found. It could have been deleted before.");
            return false;
        }
        if (!currentJobOptional.get().getId().equals(task.getJobId())) {
            log.error("The task job id {} is different from the persisted job ID {}", (Object)task.getJobId(), (Object)currentJobOptional.get().getId());
            return false;
        }
        if (currentJobOptional.get().getStage() != ReIndexStage.REBUILDING) {
            log.error("The job id {} is in {} stage, which is not REBUILDING", (Object)task.getJobId(), (Object)currentJobOptional.get().getStage());
            return false;
        }
        if (!isSiteReIndex || !this.clusterManager.isClustered() || thisNodeInformation == null) {
            return true;
        }
        return thisNodeInformation.getAnonymizedNodeIdentifier().equals(task.getSourceNodeId());
    }

    private boolean shouldSkip(ReIndexMaintenanceTask task) {
        if (this.searchPlatformConfig.isSharedIndex() && Optional.ofNullable(this.clusterManager.getThisNodeInformation()).map(currentNode -> !currentNode.getAnonymizedNodeIdentifier().equals(task.getSourceNodeId())).orElse(false).booleanValue()) {
            log.warn("This node is configured with a shared index - skipping index rebuild triggered by {}", (Object)task.getSourceNodeId());
            return true;
        }
        return false;
    }
}

