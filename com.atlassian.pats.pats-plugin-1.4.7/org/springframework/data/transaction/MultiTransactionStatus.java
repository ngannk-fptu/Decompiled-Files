/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.util.Assert
 */
package org.springframework.data.transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

class MultiTransactionStatus
implements TransactionStatus {
    private final PlatformTransactionManager mainTransactionManager;
    private final Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = Collections.synchronizedMap(new HashMap());
    private boolean newSynchonization;

    public MultiTransactionStatus(PlatformTransactionManager mainTransactionManager) {
        Assert.notNull((Object)mainTransactionManager, (String)"TransactionManager must not be null!");
        this.mainTransactionManager = mainTransactionManager;
    }

    public Map<PlatformTransactionManager, TransactionStatus> getTransactionStatuses() {
        return this.transactionStatuses;
    }

    public void setNewSynchonization() {
        this.newSynchonization = true;
    }

    public boolean isNewSynchonization() {
        return this.newSynchonization;
    }

    public void registerTransactionManager(TransactionDefinition definition, PlatformTransactionManager transactionManager) {
        this.getTransactionStatuses().put(transactionManager, transactionManager.getTransaction(definition));
    }

    public void commit(PlatformTransactionManager transactionManager) {
        TransactionStatus transactionStatus = this.getTransactionStatus(transactionManager);
        transactionManager.commit(transactionStatus);
    }

    public void rollback(PlatformTransactionManager transactionManager) {
        transactionManager.rollback(this.getTransactionStatus(transactionManager));
    }

    public boolean isRollbackOnly() {
        return this.getMainTransactionStatus().isRollbackOnly();
    }

    public boolean isCompleted() {
        return this.getMainTransactionStatus().isCompleted();
    }

    public boolean isNewTransaction() {
        return this.getMainTransactionStatus().isNewTransaction();
    }

    public boolean hasSavepoint() {
        return this.getMainTransactionStatus().hasSavepoint();
    }

    public void setRollbackOnly() {
        for (TransactionStatus ts : this.transactionStatuses.values()) {
            ts.setRollbackOnly();
        }
    }

    public Object createSavepoint() throws TransactionException {
        SavePoints savePoints = new SavePoints();
        for (TransactionStatus transactionStatus : this.transactionStatuses.values()) {
            savePoints.save(transactionStatus);
        }
        return savePoints;
    }

    public void rollbackToSavepoint(Object savepoint) throws TransactionException {
        SavePoints savePoints = (SavePoints)savepoint;
        savePoints.rollback();
    }

    public void releaseSavepoint(Object savepoint) throws TransactionException {
        ((SavePoints)savepoint).release();
    }

    public void flush() {
        for (TransactionStatus transactionStatus : this.transactionStatuses.values()) {
            transactionStatus.flush();
        }
    }

    private TransactionStatus getMainTransactionStatus() {
        return this.transactionStatuses.get(this.mainTransactionManager);
    }

    private TransactionStatus getTransactionStatus(PlatformTransactionManager transactionManager) {
        return this.getTransactionStatuses().get(transactionManager);
    }

    private static class SavePoints {
        private final Map<TransactionStatus, Object> savepoints = new HashMap<TransactionStatus, Object>();

        private SavePoints() {
        }

        private void addSavePoint(TransactionStatus status, Object savepoint) {
            Assert.notNull((Object)status, (String)"TransactionStatus must not be null!");
            this.savepoints.put(status, savepoint);
        }

        private void save(TransactionStatus transactionStatus) {
            Object savepoint = transactionStatus.createSavepoint();
            this.addSavePoint(transactionStatus, savepoint);
        }

        public void rollback() {
            for (TransactionStatus transactionStatus : this.savepoints.keySet()) {
                transactionStatus.rollbackToSavepoint(this.savepointFor(transactionStatus));
            }
        }

        private Object savepointFor(TransactionStatus transactionStatus) {
            return this.savepoints.get(transactionStatus);
        }

        public void release() {
            for (TransactionStatus transactionStatus : this.savepoints.keySet()) {
                transactionStatus.releaseSavepoint(this.savepointFor(transactionStatus));
            }
        }
    }
}

