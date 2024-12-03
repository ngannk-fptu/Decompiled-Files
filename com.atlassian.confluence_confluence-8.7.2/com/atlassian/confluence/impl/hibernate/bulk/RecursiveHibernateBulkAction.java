/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate.bulk;

import com.atlassian.confluence.impl.hibernate.bulk.BulkAction;
import com.atlassian.confluence.impl.hibernate.bulk.BulkActionReportAware;
import com.atlassian.confluence.impl.hibernate.bulk.BulkExecutionContext;
import com.atlassian.confluence.impl.hibernate.bulk.BulkTransaction;
import com.atlassian.confluence.impl.hibernate.bulk.StopBatchProcessingException;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecursiveHibernateBulkAction<CONTEXT extends BulkExecutionContext, TARGET> {
    private static final Logger log = LoggerFactory.getLogger(RecursiveHibernateBulkAction.class);
    private final BulkTransaction bulkTransaction;
    private final ProgressMeter progressMeter;
    private final List<TARGET> processedEntries;
    private final int batchSize;
    private final int maximumAllowedProcessedEntries;
    private int processedLevel;
    private int processedLevelTracker;
    private int numEntriesProcessed;
    private int numEntriesActioned;

    public RecursiveHibernateBulkAction(BulkTransaction bulkTransaction, ProgressMeter progressMeter, int batchSize, int maximumAllowedProcessedEntries) {
        this.bulkTransaction = bulkTransaction;
        this.processedEntries = new ArrayList<TARGET>();
        this.batchSize = batchSize;
        this.maximumAllowedProcessedEntries = maximumAllowedProcessedEntries;
        this.progressMeter = progressMeter == null ? new ProgressMeter() : progressMeter;
    }

    public int getNumEntriesProcessed() {
        return this.numEntriesProcessed;
    }

    public int execute(CONTEXT context, TARGET processingObject, BulkAction<CONTEXT, TARGET> processingAction) {
        this.processedLevelTracker = 0;
        this.executeInternal(context, processingObject, null, processingAction);
        this.progressMeter.setCompletedSuccessfully(true);
        return this.numEntriesActioned;
    }

    private boolean executeInternal(CONTEXT context, TARGET processingObject, TARGET previousProcessedObject, BulkAction<CONTEXT, TARGET> processingAction) {
        Preconditions.checkNotNull(processingAction);
        ++this.processedLevelTracker;
        if (this.processedLevelTracker > this.processedLevel) {
            this.processedLevel = this.processedLevelTracker;
        }
        boolean shouldContinue = true;
        try {
            BulkAction.Result<CONTEXT, TARGET> result;
            if (this.bulkTransaction.shouldStartTransaction()) {
                this.bulkTransaction.beginTransaction(new Object[0]);
            }
            if (this.numEntriesProcessed >= this.maximumAllowedProcessedEntries) {
                log.info("Stop the process as we reached the maximum allowed entries to be processed. Processed {} entries when only {} are allowed.", (Object)this.numEntriesProcessed, (Object)this.maximumAllowedProcessedEntries);
                throw new StopBatchProcessingException("Reached the maximum number of entries allowed for processing");
            }
            if (this.processedEntries.size() == this.batchSize && previousProcessedObject != null) {
                this.commitAndUpdateProgress(false, this.numEntriesProcessed);
            }
            if ((result = processingAction.process(context, processingObject)).isActioned()) {
                ++this.numEntriesActioned;
            }
            this.reportStatus(this.progressMeter, processingAction, ++this.numEntriesProcessed, this.numEntriesActioned, this.processedLevel);
            this.processedEntries.add(processingObject);
            CONTEXT innerContext = result.getContext() == null ? context : result.getContext();
            List<TARGET> nextProcessedObjects = result.getNextBatch();
            if (nextProcessedObjects != null) {
                for (TARGET nextProcessesObject : nextProcessedObjects) {
                    if (!this.executeInternal(innerContext, nextProcessesObject, processingObject, processingAction)) break;
                }
            }
            if (previousProcessedObject == null) {
                this.commitAndUpdateProgress(true, this.numEntriesProcessed);
            }
        }
        catch (Exception exception) {
            log.info("An exception occurred during the batch process. Attempting to commit and halt the process.", (Throwable)exception);
            this.commitAndUpdateProgress(true, this.numEntriesProcessed);
            log.debug("Exception happens during batch process. The process halt");
            shouldContinue = false;
            this.progressMeter.setCompletedSuccessfully(false);
        }
        --this.processedLevelTracker;
        return shouldContinue;
    }

    private void commitAndUpdateProgress(boolean isFinish, int currentCount) {
        this.bulkTransaction.commitTransaciton();
        this.processedEntries.clear();
        if (isFinish) {
            this.progressMeter.setPercentage(this.progressMeter.getTotal(), this.progressMeter.getTotal());
        } else {
            this.progressMeter.setPercentage(currentCount, this.progressMeter.getTotal());
        }
    }

    private void reportStatus(ProgressMeter progressMeter, BulkAction<CONTEXT, TARGET> processingAction, int numEntriesProcessed, int numEntriesActioned, int processedLevel) {
        if (processingAction instanceof BulkActionReportAware) {
            BulkActionReportAware bulkActionReportAware = (BulkActionReportAware)((Object)processingAction);
            bulkActionReportAware.report(progressMeter, numEntriesProcessed, numEntriesActioned, processedLevel);
        }
    }
}

