/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.status.schedule;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.event.events.admin.ReIndexJobFinishedEvent;
import com.atlassian.confluence.index.status.ReIndexError;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import com.atlassian.confluence.index.status.ReIndexStage;
import com.atlassian.confluence.internal.index.event.IndexSnapshotRestorationFailedEvent;
import com.atlassian.confluence.internal.index.event.ReindexAnalyticsEventPublishingHelper;
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.confluence.internal.index.status.ReIndexJobManagerInternal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReIndexHouseKeepingJobRunner
implements JobRunner {
    static final long REBUILDING_INDEX_NO_UPDATES_MAX_SECONDS = Long.getLong("confluence.rendex.noupdate.max.seconds", 60L);
    private static final Logger log = LoggerFactory.getLogger(ReIndexHouseKeepingJobRunner.class);
    private final ReIndexJobManagerInternal jobManager;
    private final ClusterManager clusterManager;
    private final DarkFeatureManager darkFeatureManager;
    private final EventPublisher eventPublisher;

    public ReIndexHouseKeepingJobRunner(ReIndexJobManagerInternal jobManager, ClusterManager clusterManager, DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher) {
        this.jobManager = Objects.requireNonNull(jobManager);
        this.clusterManager = Objects.requireNonNull(clusterManager);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest ignored) {
        if (!this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false).booleanValue()) {
            log.debug("Re-index improvements feature is not enabled");
            return JobRunnerResponse.success();
        }
        Optional<ReIndexJob> currentJob = this.jobManager.getRunningOrMostRecentReIndex();
        if (currentJob.isEmpty() || currentJob.get().getStage() == ReIndexStage.COMPLETE) {
            log.debug("No running re-index jobs");
            return JobRunnerResponse.success();
        }
        return this.processCurrentJob(currentJob.get());
    }

    private JobRunnerResponse processCurrentJob(ReIndexJob currentJob) {
        switch (currentJob.getStage()) {
            case REBUILDING: {
                return this.repairRebuildingJobIfNeeded(currentJob);
            }
            case PROPAGATING: {
                return this.repairPropagatingJobIfNeeded(currentJob);
            }
        }
        log.debug("This re-index job is not running");
        return JobRunnerResponse.success();
    }

    private JobRunnerResponse repairPropagatingJobIfNeeded(ReIndexJob currentJob) {
        Set<String> currentNodeIds = this.findCurrentNodeIds();
        currentJob.getNodeStatuses().forEach(nodeStatus -> {
            if (!currentNodeIds.contains(nodeStatus.getNodeId()) && nodeStatus.getState() != ReIndexNodeStatus.State.UNAVAILABLE) {
                log.warn("Node {} has disappeared since re-index started. Marking it as unavailable.", (Object)nodeStatus.getNodeId());
                this.eventPublisher.publish((Object)new IndexSnapshotRestorationFailedEvent(currentJob.getId(), nodeStatus.getNodeId(), currentJob.getStartTime().until(Instant.now(), ChronoUnit.SECONDS), IndexSnapshotError.UNAVAILABLE));
            }
        });
        return JobRunnerResponse.success();
    }

    private JobRunnerResponse repairRebuildingJobIfNeeded(ReIndexJob currentJob) {
        try {
            if (this.noUpdateSince(currentJob.getLastRebuildingUpdate())) {
                this.jobManager.updateReIndexJobIfPresent(persistedReindexJob -> {
                    if (currentJob.getStage() != persistedReindexJob.getStage()) {
                        return;
                    }
                    Set<String> currentNodeIds = this.findCurrentNodeIds();
                    log.warn("There was no updates for current re-index job for a while. Last update received at {}. Marking it as REBUILD_FAILED", (Object)currentJob.getLastRebuildingUpdate());
                    currentJob.setStage(ReIndexStage.REBUILD_FAILED);
                    this.updateNodeStatus(currentJob, currentNodeIds);
                    currentJob.setFinishTime(Instant.now());
                    this.eventPublisher.publish((Object)new ReIndexJobFinishedEvent((Object)this, currentJob));
                    ReindexAnalyticsEventPublishingHelper.publishReindexingAnalyticsEvent(this.eventPublisher, currentJob);
                });
            }
        }
        catch (InterruptedException e) {
            log.debug("Interrupted", (Throwable)e);
            Thread.currentThread().interrupt();
            return JobRunnerResponse.aborted((String)"Interrupted while waiting to update current re-index job");
        }
        catch (TimeoutException e) {
            log.error("Fail to update current re-index job", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private boolean noUpdateSince(Instant lastUpdate) {
        return Instant.now().getEpochSecond() - lastUpdate.getEpochSecond() > REBUILDING_INDEX_NO_UPDATES_MAX_SECONDS;
    }

    private Set<String> findCurrentNodeIds() {
        return this.clusterManager.getAllNodesInformation().stream().map(ClusterNodeInformation::getAnonymizedNodeIdentifier).collect(Collectors.toSet());
    }

    private void updateNodeStatus(ReIndexJob reIndexJob, Set<String> currentNodeIds) {
        if (reIndexJob.isSiteReindex()) {
            Optional<ReIndexNodeStatus> rebuildingNodeStatus = reIndexJob.getNodeStatuses().stream().filter(reIndexNodeStatus -> ReIndexNodeStatus.State.REBUILDING == reIndexNodeStatus.getState()).findFirst();
            rebuildingNodeStatus.ifPresent(nodeStatus -> {
                nodeStatus.setState(ReIndexNodeStatus.State.REBUILD_FAILED);
                nodeStatus.setError(currentNodeIds.contains(nodeStatus.getNodeId()) ? ReIndexError.UNKNOWN : ReIndexError.UNAVAILABLE);
            });
            return;
        }
        reIndexJob.getNodeStatuses().forEach(nodeStatus -> {
            if (!currentNodeIds.contains(nodeStatus.getNodeId())) {
                log.warn("The node {} has dropped out of the cluster. Marking it as UNAVAILABLE", (Object)nodeStatus.getNodeId());
                nodeStatus.setState(ReIndexNodeStatus.State.UNAVAILABLE);
                nodeStatus.setError(ReIndexError.UNAVAILABLE);
                ReindexAnalyticsEventPublishingHelper.publishSpaceNodeAnalyticsEvent(this.eventPublisher, reIndexJob, nodeStatus.getNodeId(), ReIndexError.UNAVAILABLE);
            } else if (!nodeStatus.isFinished()) {
                nodeStatus.setState(ReIndexNodeStatus.State.REBUILD_FAILED);
                nodeStatus.setError(ReIndexError.UNKNOWN);
                ReindexAnalyticsEventPublishingHelper.publishSpaceNodeAnalyticsEvent(this.eventPublisher, reIndexJob, nodeStatus.getNodeId(), ReIndexError.UNKNOWN);
            }
        });
    }
}

