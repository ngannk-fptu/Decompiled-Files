/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.retention.RuleScope
 *  com.atlassian.confluence.api.model.retention.TrashRetentionRule
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.retention.manager;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.retention.RuleScope;
import com.atlassian.confluence.api.model.retention.TrashRetentionRule;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.confluence.impl.retention.RemovalType;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalJobCompletedEvent;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatisticHolder;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatisticThreadLocal;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.manager.TrashRemovalManager;
import com.atlassian.confluence.impl.retention.rules.EvaluatedTrash;
import com.atlassian.confluence.impl.retention.rules.TrashRuleEvaluator;
import com.atlassian.confluence.impl.retention.status.TrashCleanupJobStatus;
import com.atlassian.confluence.impl.retention.status.TrashCleanupJobStatusManager;
import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultTrashRemovalManager
implements TrashRemovalManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultTrashRemovalManager.class);
    private static final Long DEFAULT_CONTENT_ID_OFFSET = 0L;
    private static final int BATCH_SIZE = Integer.getInteger("confluence.trash.retention.batch.size", 100);
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;
    private final TrashCleanupJobStatusManager jobStatusManager;
    private final TrashManagerInternal trashManagerInternal;
    private final PlatformTransactionManager transactionManager;
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final TrashRuleEvaluator trashRuleEvaluator;
    private final EventPublisher eventPublisher;

    public DefaultTrashRemovalManager(GlobalRetentionPolicyManager globalRetentionPolicyManager, TrashCleanupJobStatusManager jobStatusManager, TrashManagerInternal trashManagerInternal, PlatformTransactionManager transactionManager, RetentionFeatureChecker retentionFeatureChecker, TrashRuleEvaluator trashRuleEvaluator, EventPublisher eventPublisher) {
        this.globalRetentionPolicyManager = Objects.requireNonNull(globalRetentionPolicyManager);
        this.jobStatusManager = Objects.requireNonNull(jobStatusManager);
        this.trashManagerInternal = Objects.requireNonNull(trashManagerInternal);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.retentionFeatureChecker = Objects.requireNonNull(retentionFeatureChecker);
        this.trashRuleEvaluator = Objects.requireNonNull(trashRuleEvaluator);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void softRemove(int limit) {
        log.debug("Beginning soft removal of trash");
        TrashCleanupJobStatus status = this.jobStatusManager.getCurrentStatus();
        TrashRemovalStatisticHolder stats = new TrashRemovalStatisticHolder();
        try (Cleanup ignore = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            List evaluatedTrashes = (List)this.getTransactionTemplate().execute(transactionStatus -> this.cleanupTrashedEntities(this.globalRetentionPolicyManager.getPolicy().getTrashRetentionRule(), status.getNextContentIdOffset(), limit, stats));
            if (evaluatedTrashes != null) {
                status.setNextContentIdOffset(this.determineNextContentIdOffset(evaluatedTrashes, limit));
                this.eventPublisher.publish((Object)new TrashRemovalJobCompletedEvent(RemovalType.SOFT, stats));
            }
        }
        catch (Exception e) {
            log.warn("Error purging trash", (Throwable)e);
            status.setNextContentIdOffset(status.getNextContentIdOffset() + (long)limit);
        }
        finally {
            this.jobStatusManager.setCurrentStatus(status);
        }
        log.debug("Finished soft removal of trash");
    }

    @Override
    public void hardRemove() {
        log.debug("Beginning HARD removal of trash");
        long trashIdOffset = DEFAULT_CONTENT_ID_OFFSET;
        TrashRetentionRule globalRule = this.globalRetentionPolicyManager.getPolicy().getTrashRetentionRule();
        TrashRemovalStatisticHolder stats = new TrashRemovalStatisticHolder();
        try (Cleanup ignore = SessionCacheModeThreadLocal.temporarilySetCacheMode(CacheMode.IGNORE);){
            do {
                long currentTrashIdOffset = trashIdOffset;
                try {
                    Long nextTrashIdOffset = (Long)this.getTransactionTemplate().execute(transactionStatus -> {
                        List<EvaluatedTrash> evaluatedTrashes = this.cleanupTrashedEntities(globalRule, currentTrashIdOffset, this.getBatchSize(), stats);
                        return this.determineNextContentIdOffset(evaluatedTrashes, this.getBatchSize());
                    });
                    trashIdOffset = nextTrashIdOffset != null ? nextTrashIdOffset : DEFAULT_CONTENT_ID_OFFSET;
                }
                catch (Exception e) {
                    log.warn("Error purging trash for batch offset={}, limit={}", new Object[]{currentTrashIdOffset, this.getBatchSize(), e});
                    trashIdOffset = currentTrashIdOffset + (long)this.getBatchSize();
                }
            } while (trashIdOffset != DEFAULT_CONTENT_ID_OFFSET);
        }
        this.eventPublisher.publish((Object)new TrashRemovalJobCompletedEvent(RemovalType.HARD, stats));
        log.debug("Finished hard removal of trash");
    }

    @VisibleForTesting
    int getBatchSize() {
        return BATCH_SIZE;
    }

    @VisibleForTesting
    void deleteForRule(List<EvaluatedTrash> evaluatedTrashes, RuleScope ruleScope) {
        List<SpaceContentEntityObject> toDelete = evaluatedTrashes.stream().filter(evaluatedTrash -> evaluatedTrash.shouldBeDeleted() && evaluatedTrash.getRuleScope() == ruleScope).map(EvaluatedTrash::getTrash).collect(Collectors.toList());
        if (this.retentionFeatureChecker.isDryRunModeEnabled()) {
            toDelete.forEach(trash -> {
                if (log.isDebugEnabled()) {
                    log.debug("Deleting trash {}", trash);
                }
            });
        } else if (!toDelete.isEmpty()) {
            this.trashManagerInternal.purge(toDelete);
        }
    }

    private List<EvaluatedTrash> cleanupTrashedEntities(TrashRetentionRule globalRule, long contentIdOffset, int limit, TrashRemovalStatisticHolder statistics) {
        List<SpaceContentEntityObject> trashedEntities = this.trashManagerInternal.getTrashedEntities(contentIdOffset, limit);
        if (log.isDebugEnabled()) {
            log.debug("Found {} trashed entities. Applying trash retention rules ...", (Object)trashedEntities.size());
        }
        List<EvaluatedTrash> evaluatedTrashes = this.trashRuleEvaluator.evaluate(globalRule, trashedEntities);
        TrashRemovalStatisticThreadLocal.withStatistic(statistics.getGlobalStats(), () -> this.deleteForRule(evaluatedTrashes, RuleScope.GLOBAL));
        TrashRemovalStatisticThreadLocal.withStatistic(statistics.getSpaceStats(), () -> this.deleteForRule(evaluatedTrashes, RuleScope.SPACE));
        return evaluatedTrashes;
    }

    private long determineNextContentIdOffset(List<EvaluatedTrash> evaluatedTrashes, int batchSize) {
        return evaluatedTrashes.size() == batchSize ? evaluatedTrashes.get(evaluatedTrashes.size() - 1).getTrash().getId() + 1L : DEFAULT_CONTENT_ID_OFFSET;
    }

    private TransactionTemplate getTransactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        transactionTemplate.setPropagationBehavior(3);
        transactionTemplate.setIsolationLevel(-1);
        transactionTemplate.setName("TrashRemovalService");
        return transactionTemplate;
    }
}

