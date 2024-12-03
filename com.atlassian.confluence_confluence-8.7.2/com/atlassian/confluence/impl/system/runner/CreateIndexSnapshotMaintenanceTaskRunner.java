/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.system.runner;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.impl.system.MaintenanceTaskExecutionException;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskQueue;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskRegistry;
import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.CreateIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.impl.system.task.RestoreIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import com.atlassian.confluence.internal.index.Index;
import com.atlassian.confluence.internal.index.event.IndexSnapshotCreationFailedEvent;
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotException;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotManager;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIndexSnapshotMaintenanceTaskRunner
implements SystemMaintenanceTaskRunner<CreateIndexSnapshotMaintenanceTask> {
    private static final Logger log = LoggerFactory.getLogger(CreateIndexSnapshotMaintenanceTaskRunner.class);
    private final ClusterManager clusterManager;
    private final LuceneIndexSnapshotManager snapshotManager;
    private final SystemMaintenanceTaskRegistry registry;
    private final SystemMaintenanceTaskQueue systemMaintenanceTaskQueue;
    private final EventPublisher eventPublisher;

    public CreateIndexSnapshotMaintenanceTaskRunner(ClusterManager clusterManager, LuceneIndexSnapshotManager snapshotManager, SystemMaintenanceTaskRegistry registry, SystemMaintenanceTaskQueue systemMaintenanceTaskQueue, EventPublisher eventPublisher) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.snapshotManager = Objects.requireNonNull(snapshotManager);
        this.registry = Objects.requireNonNull(registry);
        this.systemMaintenanceTaskQueue = Objects.requireNonNull(systemMaintenanceTaskQueue);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @PostConstruct
    public void register() {
        this.registry.register(SystemMaintenanceTaskType.CREATE_INDEX_SNAPSHOT, this);
    }

    @PreDestroy
    public void unregister() {
        this.registry.unregister(SystemMaintenanceTaskType.CREATE_INDEX_SNAPSHOT);
    }

    @Override
    public void execute(CreateIndexSnapshotMaintenanceTask task) throws MaintenanceTaskExecutionException {
        if (this.shouldCreateSnapshotAfterReIndex(task)) {
            ArrayList<RestoreIndexSnapshotMaintenanceTask.IndexSnapshot> snapshots = new ArrayList<RestoreIndexSnapshotMaintenanceTask.IndexSnapshot>();
            log.info("Creating index snapshots. They will then be propagated to other nodes");
            try {
                for (Index index : task.getIndices()) {
                    LuceneIndexSnapshot snapshot = this.snapshotManager.create(index.getJournalIdentifier());
                    log.info("Index snapshot {} has been created", (Object)snapshot);
                    snapshots.add(new RestoreIndexSnapshotMaintenanceTask.IndexSnapshot(index, snapshot.getJournalEntryId()));
                }
            }
            catch (Exception e) {
                IndexSnapshotError error = e instanceof LuceneIndexSnapshotException ? ((LuceneIndexSnapshotException)e).getError() : IndexSnapshotError.UNKNOWN;
                this.eventPublisher.publish((Object)new IndexSnapshotCreationFailedEvent(error));
                throw e;
            }
            log.info("All index snapshots have been created successfully. Informing other nodes.");
            ClusterNodeInformation thisNodeInformation = Objects.requireNonNull(this.clusterManager.getThisNodeInformation());
            this.systemMaintenanceTaskQueue.enqueue(new RestoreIndexSnapshotMaintenanceTask(thisNodeInformation.getAnonymizedNodeIdentifier(), snapshots));
        }
    }

    private boolean shouldCreateSnapshotAfterReIndex(CreateIndexSnapshotMaintenanceTask task) {
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        if (!this.clusterManager.isClustered() || thisNodeInformation == null) {
            log.debug("Not running in cluster. Skipping snapshot creation");
            return false;
        }
        String currentNodeId = thisNodeInformation.getAnonymizedNodeIdentifier();
        if (!currentNodeId.equals(task.getSourceNodeId())) {
            log.debug("This task needs to be executed inside the node that did the reindex.Current node id: {}. Node that did the reindex: {}.", (Object)currentNodeId, (Object)task.getSourceNodeId());
            return false;
        }
        if (this.clusterManager.getClusterInformation().getMemberCount() < 2) {
            log.info("Cluster has just one node. There is no need to create an index snapshot for propagation");
            return false;
        }
        return true;
    }
}

