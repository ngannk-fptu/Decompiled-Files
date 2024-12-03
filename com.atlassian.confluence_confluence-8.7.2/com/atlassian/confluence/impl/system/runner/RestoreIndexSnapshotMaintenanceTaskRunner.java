/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.system.runner;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.event.events.admin.RestoreIndexSnapshotStartedEvent;
import com.atlassian.confluence.impl.system.MaintenanceTaskExecutionException;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskRegistry;
import com.atlassian.confluence.impl.system.runner.SystemMaintenanceTaskRunner;
import com.atlassian.confluence.impl.system.task.RestoreIndexSnapshotMaintenanceTask;
import com.atlassian.confluence.impl.system.task.SystemMaintenanceTaskType;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexJobManager;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestorationFailedEvent;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestorationSkippedEvent;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestoredSuccessfullyEvent;
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotException;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotManager;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.event.api.EventPublisher;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreIndexSnapshotMaintenanceTaskRunner
implements SystemMaintenanceTaskRunner<RestoreIndexSnapshotMaintenanceTask> {
    static final long SNAPSHOT_CREATION_MAX_WAIT_TIME_MS = 60000L;
    private static final Logger log = LoggerFactory.getLogger(RestoreIndexSnapshotMaintenanceTaskRunner.class);
    private static final String LOCK_NAME = "snapshot_restoration_lock";
    private static final int MAX_LOCK_ATTEMPTS = Integer.getInteger("node.reindex.propagation.max.lock.attempts", 10000);
    private static final long LOCK_WAITING_SECS = 100L;
    private final SystemMaintenanceTaskRegistry registry;
    private final LuceneIndexSnapshotManager luceneIndexSnapshotManager;
    private final ReIndexJobManager reIndexJobManager;
    private final ClusterManager clusterManager;
    private final ClusterLockService clusterLockService;
    private final EventPublisher eventPublisher;
    private final SearchPlatformConfig searchPlatformConfig;

    public RestoreIndexSnapshotMaintenanceTaskRunner(SystemMaintenanceTaskRegistry registry, LuceneIndexSnapshotManager luceneIndexSnapshotManager, ReIndexJobManager reIndexJobManager, ClusterManager clusterManager, ClusterLockService clusterLockService, EventPublisher eventPublisher, SearchPlatformConfig searchPlatformConfig) {
        this.registry = Objects.requireNonNull(registry);
        this.luceneIndexSnapshotManager = Objects.requireNonNull(luceneIndexSnapshotManager);
        this.reIndexJobManager = Objects.requireNonNull(reIndexJobManager);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.clusterLockService = Objects.requireNonNull(clusterLockService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.searchPlatformConfig = Objects.requireNonNull(searchPlatformConfig);
    }

    @PostConstruct
    public void register() {
        this.registry.register(SystemMaintenanceTaskType.RESTORE_INDEX_SNAPSHOT, this);
    }

    @PreDestroy
    public void unregister() {
        this.registry.unregister(SystemMaintenanceTaskType.RESTORE_INDEX_SNAPSHOT);
    }

    @Override
    public void execute(RestoreIndexSnapshotMaintenanceTask task) throws MaintenanceTaskExecutionException {
        Optional<ReIndexJob> currentJobOptional = this.reIndexJobManager.getRunningOrMostRecentReIndex();
        String currentJobId = currentJobOptional.map(ReIndexJob::getId).orElse(null);
        if (this.shouldSkip(task)) {
            this.eventPublisher.publish((Object)new IndexSnapshotRestorationSkippedEvent(currentJobOptional.map(ReIndexJob::getId).orElse(null), this.getCurrentNodeId()));
            return;
        }
        if (!this.shouldRestoreSnapshot(task)) {
            return;
        }
        this.eventPublisher.publish((Object)new RestoreIndexSnapshotStartedEvent());
        boolean lockAcquired = false;
        int attemptsRemaining = MAX_LOCK_ATTEMPTS;
        Instant startTime = Instant.now();
        while (!lockAcquired && attemptsRemaining > 0) {
            --attemptsRemaining;
            try {
                ClusterLock snapshotRestorationLock = this.clusterLockService.getLockForName(LOCK_NAME);
                if (!snapshotRestorationLock.tryLock(100L, TimeUnit.SECONDS)) continue;
                lockAcquired = true;
                try {
                    this.doRestore(task.getIndexSnapshots());
                    this.eventPublisher.publish((Object)new IndexSnapshotRestoredSuccessfullyEvent(currentJobId, this.getCurrentNodeId(), startTime.until(Instant.now(), ChronoUnit.SECONDS)));
                }
                catch (Exception e) {
                    IndexSnapshotError error = e instanceof LuceneIndexSnapshotException ? ((LuceneIndexSnapshotException)e).getError() : IndexSnapshotError.UNKNOWN;
                    this.eventPublisher.publish((Object)new IndexSnapshotRestorationFailedEvent(currentJobId, this.getCurrentNodeId(), startTime.until(Instant.now(), ChronoUnit.SECONDS), error));
                    throw e;
                }
                finally {
                    snapshotRestorationLock.unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MaintenanceTaskExecutionException("This thread was interrupted while waiting to perform an index replacement. The operation will be retried later", e);
            }
        }
        if (!lockAcquired) {
            this.eventPublisher.publish((Object)new IndexSnapshotRestorationFailedEvent(currentJobId, this.getCurrentNodeId(), startTime.until(Instant.now(), ChronoUnit.SECONDS), IndexSnapshotError.UNKNOWN));
            throw new RuntimeException("This node was unable to obtain an exclusive lock to update its index. The index on this node has not been updated. Consider increasing the node.reindex.propagation.max.lock.attempts property.");
        }
    }

    @VisibleForTesting
    boolean shouldRestoreSnapshot(RestoreIndexSnapshotMaintenanceTask task) {
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        if (!this.clusterManager.isClustered() || thisNodeInformation == null) {
            log.warn("Index propagation is a DC-only feature");
            return false;
        }
        String currentNodeId = thisNodeInformation.getAnonymizedNodeIdentifier();
        if (currentNodeId.equals(task.getSourceNodeId())) {
            log.info("Index snapshot will not be restored on the origin node. Skipping node: '{}'", (Object)currentNodeId);
            return false;
        }
        return true;
    }

    private String getCurrentNodeId() {
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        return thisNodeInformation != null ? thisNodeInformation.getAnonymizedNodeIdentifier() : "";
    }

    private void doRestore(Collection<RestoreIndexSnapshotMaintenanceTask.IndexSnapshot> indexSnapshots) throws MaintenanceTaskExecutionException {
        log.info("Restoring index snapshots");
        for (RestoreIndexSnapshotMaintenanceTask.IndexSnapshot indexSnapshot : indexSnapshots) {
            Optional<LuceneIndexSnapshot> maybeSnapshot;
            try {
                maybeSnapshot = this.luceneIndexSnapshotManager.find(indexSnapshot.getIndex().getJournalIdentifier(), indexSnapshot.getJournalId(), 60000L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MaintenanceTaskExecutionException(String.format("Interrupted while waiting for index snapshot %s : %s", new Object[]{indexSnapshot.getIndex(), indexSnapshot.getJournalId()}));
            }
            if (maybeSnapshot.isEmpty()) {
                throw new LuceneIndexSnapshotException(String.format("No snapshot found in shared home for index %s with journal entry id %s.", indexSnapshot.getIndex().name(), indexSnapshot.getJournalId()), IndexSnapshotError.SNAPSHOT_NOT_EXIST);
            }
            this.luceneIndexSnapshotManager.restore(maybeSnapshot.get());
            log.info("Index snapshot {} has been restored", (Object)maybeSnapshot.get());
        }
        log.info("All index snapshots have been restored successfully");
    }

    private boolean shouldSkip(RestoreIndexSnapshotMaintenanceTask task) {
        if (this.searchPlatformConfig.isSharedIndex()) {
            log.warn("This node is configured with a shared index - skipping snapshot restoration triggered by {}", (Object)task.getSourceNodeId());
            return true;
        }
        return false;
    }
}

