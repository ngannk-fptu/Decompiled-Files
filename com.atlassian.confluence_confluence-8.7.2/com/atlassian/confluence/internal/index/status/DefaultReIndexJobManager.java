/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.status;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.ReIndexingScopeThreadLocal;
import com.atlassian.confluence.event.events.admin.ReIndexJobFinishedEvent;
import com.atlassian.confluence.event.events.admin.ReIndexRequestEvent;
import com.atlassian.confluence.event.events.admin.ReindexFinishedEvent;
import com.atlassian.confluence.event.events.admin.ReindexSkippedEvent;
import com.atlassian.confluence.event.events.admin.ReindexStartedEvent;
import com.atlassian.confluence.event.events.admin.RestoreIndexSnapshotStartedEvent;
import com.atlassian.confluence.impl.system.SystemMaintenanceTaskQueue;
import com.atlassian.confluence.impl.system.task.ReIndexMaintenanceTask;
import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.internal.index.event.IndexSnapshotCreationFailedEvent;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestorationFailedEvent;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestorationSkippedEvent;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestoredSuccessfullyEvent;
import com.atlassian.confluence.internal.index.event.ReIndexFinishedAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.ReIndexStartedAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.ReindexAnalyticsEventPublishingHelper;
import com.atlassian.confluence.internal.index.lucene.LuceneIndexHelper;
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.confluence.internal.index.status.ReIndexJobManagerInternal;
import com.atlassian.confluence.internal.index.status.ReIndexJobPersister;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.Progress;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultReIndexJobManager
implements ReIndexJobManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultReIndexJobManager.class);
    private static final String INDEX_REBUILD_MONITORING_JOB_KEY_ID = "IndexRebuildMonitoring";
    private static final long JOB_PERSISTER_LOCK_ACQUIRE_TIMEOUT_MS = Long.getLong("confluence.reindex.persister.lock.acquire.timeout.ms", 10000L);
    private static final long INDEX_REBUILD_MONITORING_INTERVAL_MS = Long.getLong("confluence.reindex.status.monitoring.interval.ms", 3000L);
    private final ReIndexJobPersister persister;
    private final EventPublisher eventPublisher;
    private final SchedulerService schedulerService;
    private final ClusterManager clusterManager;
    private final ClusterLockService clusterLockService;
    private final DarkFeatureManager darkFeatureManager;
    private final ToLongFunction<Path> indexSizeSupplier;
    private final BootstrapManager bootstrapManager;
    private final SystemMaintenanceTaskQueue systemMaintenanceTaskQueue;
    private volatile Progress legacyIndexRebuildProgress;
    private final SearchPlatformConfig searchPlatformConfig;

    public DefaultReIndexJobManager(ReIndexJobPersister persister, EventPublisher eventPublisher, SchedulerService schedulerService, ClusterManager clusterManager, ClusterLockService clusterLockService, DarkFeatureManager darkFeatureManager, BootstrapManager bootstrapManager, SystemMaintenanceTaskQueue systemMaintenanceTaskQueue, SearchPlatformConfig searchPlatformConfig) {
        this(persister, eventPublisher, schedulerService, clusterManager, clusterLockService, darkFeatureManager, bootstrapManager, LuceneIndexHelper::sizeOfIndexMB, systemMaintenanceTaskQueue, searchPlatformConfig);
    }

    @VisibleForTesting
    DefaultReIndexJobManager(ReIndexJobPersister persister, EventPublisher eventPublisher, SchedulerService schedulerService, ClusterManager clusterManager, ClusterLockService clusterLockService, DarkFeatureManager darkFeatureManager, BootstrapManager bootstrapManager, ToLongFunction<Path> indexSizeSupplier, SystemMaintenanceTaskQueue systemMaintenanceTaskQueue, SearchPlatformConfig searchPlatformConfig) {
        this.persister = Objects.requireNonNull(persister);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.schedulerService = Objects.requireNonNull(schedulerService);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.clusterLockService = Objects.requireNonNull(clusterLockService);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.indexSizeSupplier = Objects.requireNonNull(indexSizeSupplier);
        this.systemMaintenanceTaskQueue = Objects.requireNonNull(systemMaintenanceTaskQueue);
        this.searchPlatformConfig = Objects.requireNonNull(searchPlatformConfig);
    }

    @PostConstruct
    public void register() {
        this.eventPublisher.register((Object)this);
        this.schedulerService.registerJobRunner(JobRunnerKey.of((String)INDEX_REBUILD_MONITORING_JOB_KEY_ID), this::monitorIndexRebuild);
    }

    @PreDestroy
    public void unregister() {
        this.eventPublisher.unregister((Object)this);
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)INDEX_REBUILD_MONITORING_JOB_KEY_ID));
    }

    @Override
    public Optional<ReIndexJob> getRunningOrMostRecentReIndex() {
        return this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").flatMap(enabled -> enabled != false ? this.persister.get() : Optional.empty());
    }

    @Override
    public boolean acknowledgeRunningJob() throws InterruptedException {
        if (!this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            return false;
        }
        AtomicBoolean acknowledged = new AtomicBoolean(false);
        try {
            this.updateReIndexJobIfPresent(runningJob -> {
                runningJob.setAcknowledged(true);
                acknowledged.set(true);
            });
            return acknowledged.get();
        }
        catch (TimeoutException e) {
            log.error("Timed out waiting to update running re-index job", (Throwable)e);
            return false;
        }
    }

    @Override
    public void updateReIndexJobIfPresent(Consumer<ReIndexJob> updater) throws InterruptedException, TimeoutException {
        this.executeWithClusterLock(() -> {
            Optional<ReIndexJob> reIndexJobOptional = this.persister.get();
            if (reIndexJobOptional.isPresent()) {
                ReIndexJob reIndexJob = reIndexJobOptional.get();
                updater.accept(reIndexJob);
                this.persister.saveOrUpdate(reIndexJob);
            }
        });
    }

    @Override
    public void updateReIndexJob(ReIndexJob reIndexJob) throws InterruptedException, TimeoutException {
        this.executeWithClusterLock(() -> this.persister.saveOrUpdate(reIndexJob));
    }

    private void executeWithClusterLock(Runnable runnable) throws TimeoutException, InterruptedException {
        ClusterLock persisterLock = this.clusterLockService.getLockForName("confluence.rendex.job.persister.lock");
        if (persisterLock.tryLock(JOB_PERSISTER_LOCK_ACQUIRE_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                persisterLock.unlock();
            }
        } else {
            throw new TimeoutException("Cannot obtain cluster-lock to update running re-index job");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Optional<ReIndexJob> createNewJob(List<String> spaceKeys) {
        ClusterLock persisterLock = this.clusterLockService.getLockForName("confluence.rendex.job.persister.lock");
        try {
            if (!persisterLock.tryLock(JOB_PERSISTER_LOCK_ACQUIRE_TIMEOUT_MS, TimeUnit.MILLISECONDS)) return Optional.empty();
            try {
                ReIndexJob reIndexJob = new ReIndexJob(spaceKeys);
                if (this.isClusteredIndex()) {
                    ArrayList<ReIndexNodeStatus> nodeStatuses = new ArrayList<ReIndexNodeStatus>();
                    this.clusterManager.getAllNodesInformation().forEach(clusterNodeInformation -> nodeStatuses.add(new ReIndexNodeStatus(clusterNodeInformation.getAnonymizedNodeIdentifier(), ReIndexNodeStatus.State.WAITING)));
                    reIndexJob.setNodeStatuses(nodeStatuses);
                }
                if (!this.persister.saveNewUniquely(reIndexJob)) return Optional.empty();
                Optional<ReIndexJob> optional = Optional.of(reIndexJob);
                return optional;
            }
            finally {
                persisterLock.unlock();
            }
        }
        catch (InterruptedException e) {
            log.error("Could not create a new job: {}", (Object)e.getMessage());
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    @Override
    public void clear() {
        this.persister.clear();
    }

    @EventListener
    public void onReIndexRequest(ReIndexRequestEvent reIndexRequestEvent) {
        this.systemMaintenanceTaskQueue.enqueue(new ReIndexMaintenanceTask(reIndexRequestEvent));
    }

    @EventListener
    public void onIndexRebuildStarted(ReindexStartedEvent startedEvent) {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue() && ReIndexingScopeThreadLocal.currentScope() == ReIndexingScopeThreadLocal.ReIndexingScope.CLUSTER_WIDE) {
            this.indexRebuildStarted(startedEvent);
        }
    }

    @EventListener
    public void onIndexRebuildFinished(ReindexFinishedEvent reindexFinishedEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue() && ReIndexingScopeThreadLocal.currentScope() == ReIndexingScopeThreadLocal.ReIndexingScope.CLUSTER_WIDE) {
            this.indexRebuildFinished(false);
        }
    }

    @EventListener
    public void onIndexRebuildSkipped(ReindexSkippedEvent reindexSkippedEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue() && ReIndexingScopeThreadLocal.currentScope() == ReIndexingScopeThreadLocal.ReIndexingScope.CLUSTER_WIDE) {
            this.indexRebuildFinished(true);
        }
    }

    @EventListener
    public void onRestoreIndexSnapshotStartedEvent(RestoreIndexSnapshotStartedEvent ignored) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            this.restoreIndexSnapshotStarted();
        }
    }

    @EventListener
    public void onIndexSnapshotCreationFailedEvent(IndexSnapshotCreationFailedEvent failedEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            this.createIndexSnapshotFailed(failedEvent.getError());
        }
    }

    @EventListener
    public void onIndexSnapshotRestoredSuccessfullyEvent(IndexSnapshotRestoredSuccessfullyEvent indexSnapshotRestoredSuccessfullyEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            this.restoreIndexSnapshotFinished(indexSnapshotRestoredSuccessfullyEvent.getNodeId(), false, null);
        }
    }

    @EventListener
    public void onIndexSnapshotRestorationFailedEvent(IndexSnapshotRestorationFailedEvent failedEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            this.restoreIndexSnapshotFinished(failedEvent.getNodeId(), false, failedEvent.getError());
        }
    }

    @EventListener
    public void onIndexSnapshotRestorationSkippedEvent(IndexSnapshotRestorationSkippedEvent indexSnapshotRestorationSkippedEvent) throws InterruptedException {
        if (this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            this.restoreIndexSnapshotFinished(indexSnapshotRestorationSkippedEvent.getNodeId(), true, null);
        }
    }

    private void indexRebuildStarted(ReindexStartedEvent startedEvent) {
        Progress progress = startedEvent.getProgress();
        ReIndexJob currentJob = this.getRunningOrMostRecentReIndex().filter(job -> !job.isComplete()).orElse(new ReIndexJob(Instant.now(), progress != null ? (long)progress.getTotal() : 0L, startedEvent.getSpaceKeys()));
        if (this.isClusteredIndex()) {
            String currentNodeId;
            String string = currentNodeId = this.clusterManager.getThisNodeInformation() != null ? this.clusterManager.getThisNodeInformation().getAnonymizedNodeIdentifier() : "";
            if (currentJob.isSiteReindex()) {
                currentJob.setPropagatingProgress(new ReIndexJob.Progress(0L, (long)this.clusterManager.getClusterInformation().getMemberCount() - 1L));
            }
            this.updateMember(currentJob, currentNodeId, ReIndexNodeStatus.State.REBUILDING);
            this.eventPublisher.publish((Object)ReIndexStartedAnalyticsEvent.newPropagationReIndexStartedEvent(currentJob.getId()));
        }
        this.persister.saveOrUpdate(currentJob);
        this.legacyIndexRebuildProgress = startedEvent.getProgress();
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)INDEX_REBUILD_MONITORING_JOB_KEY_ID)).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)INDEX_REBUILD_MONITORING_INTERVAL_MS, (Date)new Date()));
        try {
            this.schedulerService.scheduleJob(JobId.of((String)INDEX_REBUILD_MONITORING_JOB_KEY_ID), jobConfig);
            log.info("Scheduled a job to monitor progress of rebuilding index");
        }
        catch (SchedulerServiceException e) {
            log.error("Cannot schedule a job to monitor progress of rebuilding index", (Throwable)e);
        }
    }

    private void indexRebuildFinished(boolean isSkipped) throws InterruptedException {
        this.schedulerService.unscheduleJob(JobId.of((String)INDEX_REBUILD_MONITORING_JOB_KEY_ID));
        try {
            this.updateReIndexJobIfPresent(reIndexJob -> {
                this.updateReIndexJobOnReindexFinished((ReIndexJob)reIndexJob, isSkipped);
                long indexingTimeSecs = reIndexJob.getStartTime().until(Instant.now(), ChronoUnit.SECONDS);
                Path contentIndexRoot = Paths.get(this.bootstrapManager.getFilePathProperty("lucene.index.dir"), new String[0]);
                Path changeIndexRoot = contentIndexRoot.resolve("change");
                this.eventPublisher.publish((Object)ReIndexFinishedAnalyticsEvent.newPropagationReIndexFinishedEvent(reIndexJob.getId(), indexingTimeSecs, reIndexJob.getNodeStatuses().size(), this.indexSizeSupplier.applyAsLong(contentIndexRoot), this.indexSizeSupplier.applyAsLong(changeIndexRoot)));
            });
        }
        catch (TimeoutException e) {
            log.error("Cannot obtain cluster lock to update re-index status: re-index finished", (Throwable)e);
        }
    }

    private void updateReIndexJobOnReindexFinished(ReIndexJob reIndexJob, boolean isSkipped) {
        ReIndexJob.Progress currentProgress = reIndexJob.getRebuildingProgress();
        this.updateRebuildingProgress(reIndexJob, currentProgress.getTotal(), currentProgress.getTotal());
        if (!this.clusterManager.isClustered()) {
            this.transitionToCompleteStage(reIndexJob);
            return;
        }
        ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
        String currentNodeId = thisNodeInformation != null ? thisNodeInformation.getAnonymizedNodeIdentifier() : "";
        this.updateMember(reIndexJob, currentNodeId, isSkipped ? ReIndexNodeStatus.State.REBUILD_SKIPPED : ReIndexNodeStatus.State.REBUILD_COMPLETE);
        int numberOfNodes = reIndexJob.getNodeStatuses().size();
        if (numberOfNodes <= 1) {
            this.transitionToCompleteStage(reIndexJob);
            return;
        }
        if (reIndexJob.isSiteReindex() && !isSkipped) {
            reIndexJob.setStage(ReIndexStage.PROPAGATING);
            return;
        }
        List rebuildingNodesExceptCurrent = reIndexJob.getNodeStatuses().stream().filter(reIndexNodeStatus -> !reIndexNodeStatus.isFinished() && !reIndexNodeStatus.getNodeId().equals(currentNodeId)).map(ReIndexNodeStatus::getNodeId).collect(Collectors.toList());
        if (rebuildingNodesExceptCurrent.isEmpty()) {
            this.transitionToCompleteStage(reIndexJob);
        }
    }

    private void transitionToCompleteStage(ReIndexJob reIndexJob) {
        AtomicReference<ReIndexStage> finalStage = new AtomicReference<ReIndexStage>(ReIndexStage.COMPLETE);
        reIndexJob.setFinishTime(Instant.now());
        Collection nodeStatuses = CollectionUtils.emptyIfNull(reIndexJob.getNodeStatuses());
        nodeStatuses.stream().filter(ReIndexNodeStatus::isFailed).findFirst().ifPresent(ignored -> finalStage.set(reIndexJob.isSiteReindex() ? ReIndexStage.PROPAGATION_FAILED : ReIndexStage.REBUILD_FAILED));
        reIndexJob.setStage(finalStage.get());
        this.eventPublisher.publish((Object)new ReIndexJobFinishedEvent((Object)this, reIndexJob));
        ReindexAnalyticsEventPublishingHelper.publishReindexingAnalyticsEvent(this.eventPublisher, reIndexJob);
    }

    private void createIndexSnapshotFailed(IndexSnapshotError error) throws InterruptedException {
        try {
            this.updateReIndexJobIfPresent(reIndexJob -> {
                ClusterNodeInformation thisNodeInformation = this.clusterManager.getThisNodeInformation();
                if (thisNodeInformation == null) {
                    log.error("Current node information is not found. The node could have dropped out of the cluster");
                    return;
                }
                this.updateMember((ReIndexJob)reIndexJob, thisNodeInformation.getAnonymizedNodeIdentifier(), ReIndexNodeStatus.State.PROPAGATION_FAIL, error.toReIndexError());
                reIndexJob.setStage(ReIndexStage.PROPAGATION_FAILED);
            });
        }
        catch (TimeoutException e) {
            log.error("Cannot obtain cluster lock to update re-index status: propagation finished", (Throwable)e);
        }
    }

    private void restoreIndexSnapshotStarted() throws InterruptedException {
        try {
            this.updateReIndexJobIfPresent(reIndexJob -> this.updateMember((ReIndexJob)reIndexJob, this.clusterManager.getThisNodeInformation().getAnonymizedNodeIdentifier(), ReIndexNodeStatus.State.PROPAGATING));
        }
        catch (TimeoutException e) {
            log.error("Cannot obtain cluster lock to update re-index status: propagation started", (Throwable)e);
        }
    }

    private void restoreIndexSnapshotFinished(String nodeId, boolean isSkipped, @Nullable IndexSnapshotError indexSnapshotError) throws InterruptedException {
        try {
            this.updateReIndexJobIfPresent(reIndexJob -> {
                if (!this.shouldUpdateReindexJobOnIndexSnapshotRestorationFailed(nodeId, (ReIndexJob)reIndexJob, indexSnapshotError)) {
                    return;
                }
                Pair<ReIndexNodeStatus.State, ReIndexError> nodeStateAndError = this.getNodeStateAndErrorByIndexSnapshotError(isSkipped, indexSnapshotError);
                this.updateMember((ReIndexJob)reIndexJob, nodeId, (ReIndexNodeStatus.State)((Object)((Object)nodeStateAndError.getLeft())), (ReIndexError)((Object)((Object)nodeStateAndError.getRight())));
                ReIndexJob.Progress currentProgress = reIndexJob.getPropagatingProgress();
                ReIndexJob.Progress updatedProgress = new ReIndexJob.Progress(currentProgress.getProcessed() + 1L, currentProgress.getTotal());
                reIndexJob.setPropagatingProgress(updatedProgress);
                if (updatedProgress.getProcessed() == updatedProgress.getTotal()) {
                    this.transitionToCompleteStage((ReIndexJob)reIndexJob);
                }
            });
        }
        catch (TimeoutException e) {
            log.error("Cannot obtain cluster lock to update re-index status: propagation finished", (Throwable)e);
        }
    }

    private boolean shouldUpdateReindexJobOnIndexSnapshotRestorationFailed(String sourceNodeId, ReIndexJob reIndexJob, @Nullable IndexSnapshotError indexSnapshotError) {
        Optional<ReIndexNodeStatus> nodeStatus;
        return indexSnapshotError != IndexSnapshotError.UNAVAILABLE || !(nodeStatus = reIndexJob.getNodeStatuses().stream().filter(node -> sourceNodeId.equals(node.getNodeId())).findFirst()).isPresent() || nodeStatus.get().getState() != ReIndexNodeStatus.State.UNAVAILABLE;
    }

    private Pair<ReIndexNodeStatus.State, ReIndexError> getNodeStateAndErrorByIndexSnapshotError(boolean isSkipped, IndexSnapshotError indexSnapshotError) {
        if (indexSnapshotError == null) {
            return Pair.of((Object)((Object)(isSkipped ? ReIndexNodeStatus.State.PROPAGATION_SKIPPED : ReIndexNodeStatus.State.PROPAGATION_COMPLETE)), null);
        }
        ReIndexNodeStatus.State state = indexSnapshotError == IndexSnapshotError.UNAVAILABLE ? ReIndexNodeStatus.State.UNAVAILABLE : ReIndexNodeStatus.State.PROPAGATION_FAIL;
        return Pair.of((Object)((Object)state), (Object)((Object)indexSnapshotError.toReIndexError()));
    }

    private void updateMember(ReIndexJob job, String nodeId, ReIndexNodeStatus.State newState) {
        this.updateMember(job, nodeId, newState, null);
    }

    private void updateMember(ReIndexJob job, String nodeId, ReIndexNodeStatus.State newState, @Nullable ReIndexError error) {
        if (StringUtils.isBlank((CharSequence)nodeId)) {
            return;
        }
        Optional<ReIndexNodeStatus> nodeStatus = job.getNodeStatuses().stream().filter(reIndexNodeStatus -> nodeId.equals(reIndexNodeStatus.getNodeId())).findFirst();
        if (nodeStatus.isPresent()) {
            ReIndexNodeStatus nodeState = nodeStatus.get();
            nodeState.setState(newState);
            nodeState.setError(error);
        } else {
            job.getNodeStatuses().add(new ReIndexNodeStatus(nodeId, newState, error));
        }
        if (newState.isFinished()) {
            ReindexAnalyticsEventPublishingHelper.publishSpaceNodeAnalyticsEvent(this.eventPublisher, job, nodeId);
        }
    }

    @VisibleForTesting
    JobRunnerResponse monitorIndexRebuild(JobRunnerRequest ignored) {
        try {
            this.updateReIndexJobIfPresent(job -> {
                if (this.legacyIndexRebuildProgress != null) {
                    this.updateRebuildingProgress((ReIndexJob)job);
                    job.setLastRebuildingUpdate(Instant.now());
                }
            });
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return JobRunnerResponse.aborted((String)"Interrupted");
        }
        catch (TimeoutException e) {
            log.error("Cannot obtain cluster lock to update re-index job", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private void updateRebuildingProgress(ReIndexJob job) {
        this.updateRebuildingProgress(job, this.legacyIndexRebuildProgress.getCount(), this.legacyIndexRebuildProgress.getTotal());
    }

    private void updateRebuildingProgress(ReIndexJob job, long processed, long total) {
        if (job.isSiteReindex() || this.isStandaloneOrSingleNodeClusterWhenReindexingStarted(job)) {
            job.setRebuildingProgress(new ReIndexJob.Progress(processed, total));
        } else {
            this.updateCurrentNodeProgress(job, processed, total);
            long processedOnAverage = job.getNodeStatuses().stream().map(nodeStatus -> nodeStatus.getProgress().getProcessed()).mapToLong(Long::longValue).sum() / (long)job.getNodeStatuses().size();
            job.setRebuildingProgress(new ReIndexJob.Progress(processedOnAverage, total));
        }
    }

    private void updateCurrentNodeProgress(ReIndexJob job, long processed, long total) {
        ClusterNodeInformation currentNodeInformation = this.clusterManager.getThisNodeInformation();
        if (currentNodeInformation == null) {
            log.error("Could not find current node's information");
            return;
        }
        String nodeId = currentNodeInformation.getAnonymizedNodeIdentifier();
        Optional<ReIndexNodeStatus> currentNodeStatusOptional = job.getNodeStatuses().stream().filter(nodeStatus -> nodeId.equals(nodeStatus.getNodeId())).findFirst();
        currentNodeStatusOptional.ifPresent(currentNodeStatus -> currentNodeStatus.setProgress(new ReIndexJob.Progress(processed, total)));
    }

    private boolean isStandaloneOrSingleNodeClusterWhenReindexingStarted(ReIndexJob job) {
        return !this.clusterManager.isClustered() || CollectionUtils.size(job.getNodeStatuses()) <= 1 || this.clusterManager.getThisNodeInformation() == null;
    }

    private boolean isClusteredIndex() {
        return !this.searchPlatformConfig.isSharedIndex() && this.clusterManager.isClustered();
    }
}

