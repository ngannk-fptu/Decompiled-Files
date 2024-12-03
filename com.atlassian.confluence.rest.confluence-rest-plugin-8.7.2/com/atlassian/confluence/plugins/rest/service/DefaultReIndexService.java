/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.cluster.ClusterNodeInformation
 *  com.atlassian.confluence.event.events.admin.ReIndexRequestEvent
 *  com.atlassian.confluence.index.status.ReIndexJob
 *  com.atlassian.confluence.index.status.ReIndexJobManager
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.search.ReIndexOption
 *  com.atlassian.confluence.search.SearchPlatformConfig
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.service;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.event.events.admin.ReIndexRequestEvent;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexJobManager;
import com.atlassian.confluence.plugins.rest.service.ReIndexService;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultReIndexService
implements ReIndexService {
    private static final Logger log = LoggerFactory.getLogger(DefaultReIndexService.class);
    private final IndexManager indexManager;
    private final ReIndexJobManager reIndexJobManager;
    private final EventPublisher eventPublisher;
    private final ClusterLockService clusterLockService;
    private final ClusterManager clusterManager;
    private final TransactionTemplate transactionTemplate;
    private final SearchPlatformConfig searchPlatformConfig;

    public DefaultReIndexService(IndexManager indexManager, ReIndexJobManager reIndexJobManager, EventPublisher eventPublisher, ClusterLockService clusterLockService, ClusterManager clusterManager, TransactionTemplate transactionTemplate, SearchPlatformConfig searchPlatformConfig) {
        this.indexManager = indexManager;
        this.reIndexJobManager = reIndexJobManager;
        this.eventPublisher = eventPublisher;
        this.clusterLockService = clusterLockService;
        this.clusterManager = clusterManager;
        this.transactionTemplate = transactionTemplate;
        this.searchPlatformConfig = searchPlatformConfig;
    }

    @Override
    public boolean isReIndexing() {
        Optional reIndexJobOptional = this.reIndexJobManager.getRunningOrMostRecentReIndex();
        return reIndexJobOptional.filter(reIndexJob -> !reIndexJob.getStage().isFinal()).isPresent();
    }

    @Override
    public boolean reindex(@NonNull List<String> spaceKeys, @NonNull EnumSet<ReIndexOption> reIndexOptions) throws InterruptedException {
        if (this.isClusteredIndex()) {
            return this.performClusteredReindex(spaceKeys, reIndexOptions);
        }
        return this.performStandaloneReindex(spaceKeys, reIndexOptions);
    }

    private boolean isClusteredIndex() {
        return !this.searchPlatformConfig.isSharedIndex() && this.isInClusterWithMoreThanOneNode();
    }

    private boolean isInClusterWithMoreThanOneNode() {
        return this.clusterManager.isClustered() && this.clusterManager.getThisNodeInformation() != null && this.clusterManager.getClusterInformation().getMemberCount() >= 2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean performClusteredReindex(List<String> spaceKeys, EnumSet<ReIndexOption> reIndexOptions) throws InterruptedException {
        ClusterLock clusterLock = this.clusterLockService.getLockForName("confluence_reindex_cluster_lock");
        if (clusterLock.tryLock(REINDEX_CLUSTER_LOCK_ACQUIRE_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            try {
                boolean bl = (Boolean)this.transactionTemplate.execute(() -> {
                    Optional newJobOptional = this.reIndexJobManager.createNewJob(spaceKeys);
                    if (newJobOptional.isPresent()) {
                        ClusterNodeInformation nodeInformation = this.clusterManager.getThisNodeInformation();
                        this.eventPublisher.publish((Object)new ReIndexRequestEvent((Object)this, ((ReIndexJob)newJobOptional.get()).getId(), nodeInformation.getAnonymizedNodeIdentifier(), reIndexOptions, spaceKeys));
                        return true;
                    }
                    log.warn("Could not create a new reindex job. Confluence may be re-indexing");
                    return false;
                });
                return bl;
            }
            finally {
                clusterLock.unlock();
            }
        }
        return false;
    }

    private boolean performStandaloneReindex(List<String> spaceKeys, EnumSet<ReIndexOption> reIndexOptions) {
        Optional newJobOptional = this.reIndexJobManager.createNewJob(spaceKeys);
        if (newJobOptional.isPresent()) {
            this.indexManager.reIndex(reIndexOptions, spaceKeys);
            return true;
        }
        log.warn("Could not create a new reindex job. Confluence may be re-indexing");
        return false;
    }

    @Override
    public void resetJobStatus() {
        this.reIndexJobManager.clear();
    }
}

