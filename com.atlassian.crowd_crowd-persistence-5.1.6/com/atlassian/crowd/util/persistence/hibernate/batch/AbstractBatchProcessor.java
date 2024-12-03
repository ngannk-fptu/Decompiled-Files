/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.Percentage
 *  com.atlassian.crowd.util.TimedProgressOperation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.Percentage;
import com.atlassian.crowd.util.TimedProgressOperation;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchProcessor;
import com.atlassian.crowd.util.persistence.hibernate.batch.BulkAuditMapper;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import com.atlassian.crowd.util.persistence.hibernate.batch.TransactionGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBatchProcessor<S>
implements BatchProcessor<S> {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected int batchSize = 20;

    @Override
    public final <E> BatchResult<E> execute(HibernateOperation<S> op, Collection<E> objects) {
        return this.execute(op, objects, entries -> Collections.emptyList());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final <E> BatchResult<E> execute(HibernateOperation<S> op, Collection<E> objects, BulkAuditMapper<E> bulkAuditMapper) {
        int numberOfObjects = objects.size();
        int numberOfBatches = numberOfObjects / this.batchSize + (numberOfObjects % this.batchSize > 0 ? 1 : 0);
        this.log.debug("processing [ {} ] objects in [ {} ] batches of [ {} ] with [ {} ]", new Object[]{numberOfObjects, numberOfBatches, this.batchSize, op.getClass().getName()});
        BatchResult result = new BatchResult(objects.size());
        TimedProgressOperation operation = new TimedProgressOperation("processed batch", numberOfBatches, this.log);
        this.beforeProcessCollection();
        try {
            ArrayList<E> batch = new ArrayList<E>(this.batchSize);
            for (E object : objects) {
                batch.add(object);
                if (batch.size() != this.batchSize) continue;
                this.processBatch(result, op, batch, bulkAuditMapper);
                operation.incrementedProgress();
                batch.clear();
            }
            if (!batch.isEmpty()) {
                this.processBatch(result, op, batch, bulkAuditMapper);
                batch.clear();
                operation.incrementedProgress();
            }
        }
        finally {
            this.afterProcessCollection();
        }
        return result;
    }

    private <E> void processBatch(BatchResult<E> result, HibernateOperation<S> op, List<E> objects, BulkAuditMapper<E> bulkAuditMapper) {
        int count = 0;
        int numberOfObjects = objects.size();
        try {
            this.beforeProcessBatch();
            for (E object : objects) {
                this.performOperation(op, object);
                if (!this.log.isTraceEnabled()) continue;
                this.log.trace("processed [ {} ] [ {}% ]", new Object[]{object.toString(), Percentage.get((int)count, (int)numberOfObjects)});
            }
            List auditLogChangesets = (List)bulkAuditMapper.apply(objects);
            this.auditOperations(auditLogChangesets);
            this.afterProcessBatch();
            result.addSuccesses(objects);
        }
        catch (RuntimeException e) {
            this.log.warn("batch failed falling back to individual processing", (Throwable)e);
            this.rollbackProcessBatch();
            this.processIndividual(result, op, objects, bulkAuditMapper);
        }
    }

    protected abstract void auditOperations(List<AuditLogChangesetEntity> var1);

    private <E> void processIndividual(BatchResult<E> result, HibernateOperation<S> op, Collection<E> objects, BulkAuditMapper<E> bulkAuditMapper) {
        int numberOfObjects = objects.size();
        this.log.debug("processing [ {} ] individually", (Object)numberOfObjects);
        int count = 0;
        for (E object : objects) {
            ++count;
            try {
                this.beforeProcessIndividual();
                this.performOperation(op, object);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("processed [ {} ] [ {}% ]", new Object[]{object.toString(), Percentage.get((int)count, (int)numberOfObjects)});
                }
                List auditChangesets = (List)bulkAuditMapper.apply(Collections.singleton(object));
                this.auditOperations(auditChangesets);
                this.afterProcessIndividual();
                result.addSuccess(object);
            }
            catch (RuntimeException e) {
                this.rollbackProcessIndividual();
                result.addFailure(object);
                this.log.error("Could not process " + object.getClass() + ": " + object.toString(), (Throwable)e);
            }
        }
    }

    private void performOperation(HibernateOperation<S> op, Object object) {
        if (object instanceof TransactionGroup) {
            TransactionGroup transactionGroup = (TransactionGroup)object;
            op.performOperation(transactionGroup.getPrimaryObject(), this.getSession());
            if (transactionGroup.getDependantObjects() != null) {
                transactionGroup.getDependantObjects().forEach(depObject -> op.performOperation(depObject, this.getSession()));
            }
        } else {
            op.performOperation(object, this.getSession());
        }
    }

    protected abstract S getSession();

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    protected abstract void beforeProcessCollection();

    protected abstract void afterProcessCollection();

    protected abstract void beforeProcessBatch();

    protected abstract void afterProcessBatch();

    protected abstract void rollbackProcessBatch();

    protected abstract void beforeProcessIndividual();

    protected abstract void afterProcessIndividual();

    protected abstract void rollbackProcessIndividual();
}

