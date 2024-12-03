/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RemovalCount
 *  com.atlassian.confluence.api.model.retention.RemovalCount$Builder
 *  com.atlassian.confluence.api.model.retention.RemovalSummary
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionRule
 *  com.atlassian.confluence.api.model.retention.RuleScope
 *  com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.api.service.retention.SoftCleanupStatusService
 *  com.google.common.collect.Iterators
 *  io.atlassian.util.concurrent.atomic.AtomicReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.api.model.retention.RemovalCount;
import com.atlassian.confluence.api.model.retention.RemovalSummary;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionRule;
import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.api.service.retention.SoftCleanupStatusService;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.impl.retention.HistoricalVersionService;
import com.atlassian.confluence.impl.retention.VersionRemovalEventPublisher;
import com.atlassian.confluence.impl.retention.VersionRemovalService;
import com.atlassian.confluence.impl.retention.exception.VersionRemovalException;
import com.atlassian.confluence.impl.retention.manager.SpaceRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.CleanupSummary;
import com.atlassian.confluence.impl.retention.rules.ContentType;
import com.atlassian.confluence.impl.retention.rules.DeletedHistoricalVersionSummary;
import com.atlassian.confluence.impl.retention.rules.EvaluatedHistoricalVersion;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import com.atlassian.confluence.impl.retention.rules.RetentionRuleEvaluator;
import com.atlassian.confluence.impl.retention.schedule.VersionRemovalJobType;
import com.atlassian.confluence.util.Cleanup;
import com.google.common.collect.Iterators;
import io.atlassian.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultVersionRemovalService
implements VersionRemovalService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultVersionRemovalService.class);
    private static final int DELETION_BATCH_SIZE = Integer.getInteger("confluence.retention.rules.batch.size", 200);
    private static final int SELECT_BATCH_SIZE = Integer.getInteger("confluence.retention.rules.select.size", 4000);
    private static final Long DEFAULT_START_ORIGINAL_ID = 0L;
    private final HistoricalVersionService historicalVersionService;
    private final SoftCleanupStatusService softCleanupStatusService;
    private RetentionRuleEvaluator retentionRuleEvaluator;
    private final VersionRemovalEventPublisher versionRemovalEventPublisher;
    private final PlatformTransactionManager transactionManager;
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final SpaceRetentionPolicyManager spaceRetentionPolicyManager;

    public DefaultVersionRemovalService(HistoricalVersionService historicalVersionService, SoftCleanupStatusService softCleanupStatusService, VersionRemovalEventPublisher versionRemovalEventPublisher, PlatformTransactionManager transactionManager, RetentionFeatureChecker retentionFeatureChecker, SpaceRetentionPolicyManager spaceRetentionPolicyManager) {
        this.historicalVersionService = historicalVersionService;
        this.softCleanupStatusService = softCleanupStatusService;
        this.versionRemovalEventPublisher = versionRemovalEventPublisher;
        this.transactionManager = transactionManager;
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.spaceRetentionPolicyManager = spaceRetentionPolicyManager;
    }

    @Override
    public void hardRemoveVersions(RetentionPolicy policy) {
        logger.debug("Beginning hard removal of content versions");
        ArrayList<CleanupSummary> removalSummaries = new ArrayList<CleanupSummary>();
        long startOriginalId = DEFAULT_START_ORIGINAL_ID;
        int batchSize = this.determineBatchSize(policy, SELECT_BATCH_SIZE);
        logger.info("Removing all versions matching your retention policy using a selection batch size of {}", (Object)batchSize);
        do {
            this.retentionRuleEvaluator = new RetentionRuleEvaluator(this.spaceRetentionPolicyManager);
            try (Cleanup ignore = this.setCacheMode();){
                long latestStartOriginalId = startOriginalId;
                Long nextStartId = (Long)this.getTransactionTemplate().execute(transactionStatus -> {
                    CleanupSummary pageSummary = this.removeExpiredVersions(policy.getPageVersionRule(), latestStartOriginalId, batchSize, ContentType.PAGE);
                    CleanupSummary attachmentSummary = this.removeExpiredVersions(policy.getAttachmentRetentionRule(), latestStartOriginalId, batchSize, ContentType.ATTACHMENT);
                    removalSummaries.add(pageSummary);
                    removalSummaries.add(attachmentSummary);
                    return this.determineNextStartId(pageSummary.getLastIdProcessed(), attachmentSummary.getLastIdProcessed());
                });
                startOriginalId = nextStartId != null ? nextStartId : 0L;
            }
            catch (VersionRemovalException ex) {
                startOriginalId = ex.getOriginalId() + 1L;
            }
            catch (Exception ex) {
                startOriginalId += (long)batchSize;
            }
        } while (startOriginalId != DEFAULT_START_ORIGINAL_ID);
        CleanupSummary cleanupSummary = this.calculateTotals(removalSummaries);
        this.versionRemovalEventPublisher.publishJobCompletedEvent(cleanupSummary, VersionRemovalJobType.HARD);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void softRemoveVersions(RetentionPolicy policy, int batchSize) {
        logger.info("Removing versions matching your retention policy, up to a max of {} records, in batches of {}", (Object)batchSize, (Object)DELETION_BATCH_SIZE);
        int effectiveBatchSize = this.determineBatchSize(policy, batchSize);
        this.retentionRuleEvaluator = new RetentionRuleEvaluator(this.spaceRetentionPolicyManager);
        logger.debug("Beginning soft removal of content versions");
        AtomicReference jobStatus = new AtomicReference((Object)this.softCleanupStatusService.getCurrentStatus());
        try (Cleanup ignore = this.setCacheMode();){
            this.getTransactionTemplate().execute(transactionStatus -> {
                SoftCleanupJobStatus status = (SoftCleanupJobStatus)jobStatus.get();
                try {
                    CleanupSummary pageSummary = this.removeExpiredVersions(policy.getPageVersionRule(), status.getNextStartOriginalId(), effectiveBatchSize, ContentType.PAGE);
                    CleanupSummary attachmentSummary = this.removeExpiredVersions(policy.getAttachmentRetentionRule(), status.getNextStartOriginalId(), effectiveBatchSize, ContentType.ATTACHMENT);
                    boolean isCompletedCycle = pageSummary.getLastIdProcessed() == DEFAULT_START_ORIGINAL_ID.longValue() && attachmentSummary.getLastIdProcessed() == DEFAULT_START_ORIGINAL_ID.longValue();
                    CleanupSummary cleanupSummary = this.combineSummaries(pageSummary, attachmentSummary);
                    this.updateStatusCounts(cleanupSummary, status);
                    this.updateCurrentCycleStatus(status, isCompletedCycle, cleanupSummary.getLastIdProcessed());
                    this.versionRemovalEventPublisher.publishJobCompletedEvent(cleanupSummary, VersionRemovalJobType.SOFT);
                }
                catch (VersionRemovalException ex) {
                    this.updateCurrentCycleStatus(status, false, ex.getOriginalId() + 1L);
                }
                catch (Exception ex) {
                    this.updateCurrentCycleStatus(status, false, status.getNextStartOriginalId() + (long)batchSize);
                }
                finally {
                    jobStatus.set((Object)status);
                }
                return null;
            });
        }
        finally {
            this.saveStatus((SoftCleanupJobStatus)jobStatus.get());
        }
    }

    protected Cleanup setCacheMode() {
        return SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);
    }

    private CleanupSummary calculateTotals(List<CleanupSummary> removalSummaries) {
        return new CleanupSummary.Builder().pageVersionsRemovedByGlobalRules(removalSummaries.stream().filter(Objects::nonNull).mapToLong(CleanupSummary::getPageVersionsRemovedByGlobalRules).sum()).attachmentVersionsRemovedByGlobalRules(removalSummaries.stream().filter(Objects::nonNull).mapToLong(CleanupSummary::getAttachmentVersionsRemovedByGlobalRules).sum()).attachmentSizeRemovedByGlobalRules(removalSummaries.stream().filter(Objects::nonNull).mapToLong(CleanupSummary::getAttachmentSizeRemovedByGlobalRules).sum()).build();
    }

    private CleanupSummary removeExpiredVersions(RetentionRule rule, long startOriginalId, int batchSize, ContentType contentType) {
        List<HistoricalVersion> contentVersionList = this.historicalVersionService.find(startOriginalId, batchSize, contentType);
        Map<Long, List<HistoricalVersion>> versionsPerPage = contentVersionList.stream().collect(Collectors.groupingBy(HistoricalVersion::getOriginalId));
        boolean isNotLastIteration = contentVersionList.size() == batchSize;
        List<EvaluatedHistoricalVersion> evaluated = this.retentionRuleEvaluator.evaluate(rule, versionsPerPage);
        List<DeletedHistoricalVersionSummary> globalDeletedSummaryList = this.deleteForRule(evaluated, RuleScope.GLOBAL);
        List<DeletedHistoricalVersionSummary> spaceDeletedSummaryList = this.deleteForRule(evaluated, RuleScope.SPACE);
        long lastIdProcessed = this.getOriginalIdForNextIteration(versionsPerPage, isNotLastIteration);
        return this.generateCleanupSummary(globalDeletedSummaryList, spaceDeletedSummaryList, lastIdProcessed);
    }

    private List<DeletedHistoricalVersionSummary> deleteForRule(List<EvaluatedHistoricalVersion> evaluated, RuleScope ruleScope) {
        return this.deleteHistoricalContentList(evaluated.stream().filter(EvaluatedHistoricalVersion::getShouldBeDeleted).filter(evaluatedHistoricalVersion -> evaluatedHistoricalVersion.getRuleScope().equals((Object)ruleScope)).map(EvaluatedHistoricalVersion::getHistoricalVersion).collect(Collectors.toList()));
    }

    private long determineNextStartId(long nextPageId, long nextAttachmentId) {
        if (nextPageId == 0L) {
            return nextAttachmentId;
        }
        if (nextAttachmentId == 0L) {
            return nextPageId;
        }
        return Math.min(nextPageId, nextAttachmentId);
    }

    private long getOriginalIdForNextIteration(Map<Long, List<HistoricalVersion>> versionsPerContent, boolean isNotLastIteration) {
        if (isNotLastIteration) {
            return Collections.max(versionsPerContent.keySet()) + 1L;
        }
        return DEFAULT_START_ORIGINAL_ID;
    }

    private int determineBatchSize(RetentionPolicy policy, int contentCount) {
        if (policy.getPageVersionRule().hasVersionLimit()) {
            return Math.max(contentCount, policy.getPageVersionRule().getMaxNumberOfVersions());
        }
        return contentCount;
    }

    private void updateCurrentCycleStatus(SoftCleanupJobStatus status, boolean isCompletedCycle, Long nextStartOriginalId) {
        if (isCompletedCycle) {
            logger.info("Versions removal cycle complete, resetting start range to {}", (Object)DEFAULT_START_ORIGINAL_ID);
            status.setCurrentCycle(new RemovalSummary());
            status.incrementCycleCount();
            status.setNextStartOriginalId(DEFAULT_START_ORIGINAL_ID.longValue());
        } else {
            status.setNextStartOriginalId(nextStartOriginalId.longValue());
        }
    }

    private void updateStatusCounts(CleanupSummary cleanupSummary, SoftCleanupJobStatus status) {
        status.setLastIteration(new RemovalSummary());
        this.updateRemovalSummary(status.getOverall(), cleanupSummary);
        this.updateRemovalSummary(status.getCurrentCycle(), cleanupSummary);
        this.updateRemovalSummary(status.getLastIteration(), cleanupSummary);
        status.incrementIterationsCompleted();
    }

    private void saveStatus(SoftCleanupJobStatus status) {
        logger.debug("Saving SoftCleanupJobStatus for cycle [{}]", (Object)status);
        this.softCleanupStatusService.setCurrentStatus(status);
    }

    private void updateRemovalSummary(RemovalSummary removalSummary, CleanupSummary cleanupSummary) {
        removalSummary.setGlobal(this.createGlobalRemovalCount(removalSummary.getGlobal(), cleanupSummary));
        removalSummary.setSpace(this.createSpaceRemovalCount(removalSummary.getSpace(), cleanupSummary));
    }

    private RemovalCount createGlobalRemovalCount(RemovalCount originalRemovalCount, CleanupSummary cleanupSummary) {
        return new RemovalCount.Builder().pageVersionsRemoved(originalRemovalCount.getPageVersionsRemoved() + cleanupSummary.getPageVersionsRemovedByGlobalRules()).attachmentVersionsRemoved(originalRemovalCount.getAttachmentVersionsRemoved() + cleanupSummary.getAttachmentVersionsRemovedByGlobalRules()).attachmentFileSize(originalRemovalCount.getAttachmentFileSize() + cleanupSummary.getAttachmentSizeRemovedByGlobalRules()).build();
    }

    private RemovalCount createSpaceRemovalCount(RemovalCount originalRemovalCount, CleanupSummary cleanupSummary) {
        return new RemovalCount.Builder().pageVersionsRemoved(originalRemovalCount.getPageVersionsRemoved() + cleanupSummary.getPageVersionsRemovedBySpaceRules()).attachmentVersionsRemoved(originalRemovalCount.getAttachmentVersionsRemoved() + cleanupSummary.getAttachmentVersionsRemovedBySpaceRules()).attachmentFileSize(originalRemovalCount.getAttachmentFileSize() + cleanupSummary.getAttachmentSizeRemovedBySpaceRules()).build();
    }

    private List<DeletedHistoricalVersionSummary> deleteHistoricalContentList(List<HistoricalVersion> contentToDelete) {
        ArrayList<DeletedHistoricalVersionSummary> removalSummaries = new ArrayList<DeletedHistoricalVersionSummary>();
        Iterators.partition(contentToDelete.iterator(), (int)DELETION_BATCH_SIZE).forEachRemaining(historicalVersions -> removalSummaries.add(this.deleteContent((List<HistoricalVersion>)historicalVersions)));
        return removalSummaries;
    }

    private CleanupSummary combineSummaries(CleanupSummary pageSummary, CleanupSummary attachmentSummary) {
        return new CleanupSummary.Builder().attachmentVersionsRemovedByGlobalRules(pageSummary.getAttachmentVersionsRemovedByGlobalRules() + attachmentSummary.getAttachmentVersionsRemovedByGlobalRules()).attachmentVersionsRemovedBySpaceRules(pageSummary.getAttachmentSizeRemovedBySpaceRules() + attachmentSummary.getAttachmentVersionsRemovedBySpaceRules()).pageVersionsRemovedByGlobalRules(pageSummary.getPageVersionsRemovedByGlobalRules() + attachmentSummary.getPageVersionsRemovedByGlobalRules()).pageVersionsRemovedBySpaceRules(pageSummary.getPageVersionsRemovedBySpaceRules() + attachmentSummary.getPageVersionsRemovedBySpaceRules()).attachmentSizeRemovedByGlobalRules(attachmentSummary.getAttachmentSizeRemovedByGlobalRules()).attachmentSizeRemovedBySpaceRules(attachmentSummary.getAttachmentSizeRemovedBySpaceRules()).lastIdProcessed(this.determineNextStartId(pageSummary.getLastIdProcessed(), attachmentSummary.getLastIdProcessed())).build();
    }

    private CleanupSummary generateCleanupSummary(List<DeletedHistoricalVersionSummary> globalDeletedSummaryList, List<DeletedHistoricalVersionSummary> spaceDeletedSummaryList, long lastOriginalId) {
        return new CleanupSummary.Builder().pageVersionsRemovedByGlobalRules(globalDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getPageVersionsRemoved).sum()).attachmentVersionsRemovedByGlobalRules(globalDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getAttachmentVersionsRemoved).sum()).attachmentSizeRemovedByGlobalRules(globalDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getAttachmentSizeRemoved).sum()).pageVersionsRemovedBySpaceRules(spaceDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getPageVersionsRemoved).sum()).attachmentVersionsRemovedBySpaceRules(spaceDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getAttachmentVersionsRemoved).sum()).attachmentSizeRemovedBySpaceRules(spaceDeletedSummaryList.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersionSummary::getAttachmentSizeRemoved).sum()).lastIdProcessed(lastOriginalId).build();
    }

    private DeletedHistoricalVersionSummary deleteContent(List<HistoricalVersion> historicalVersions) {
        if (!this.retentionFeatureChecker.isDryRunModeEnabled()) {
            return this.historicalVersionService.delete(historicalVersions);
        }
        return new DeletedHistoricalVersionSummary.Builder().build();
    }

    private TransactionTemplate getTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(3);
        transactionTemplate.setIsolationLevel(-1);
        transactionTemplate.setName("VersionRemovalService");
        return transactionTemplate;
    }
}

