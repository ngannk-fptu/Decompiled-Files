/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 */
package org.springframework.data.crossstore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.crossstore.ChangeSetBacked;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.transaction.support.TransactionSynchronization;

public class ChangeSetBackedTransactionSynchronization
implements TransactionSynchronization {
    private static final Log logger = LogFactory.getLog(ChangeSetBackedTransactionSynchronization.class);
    private final ChangeSetPersister<Object> changeSetPersister;
    private final ChangeSetBacked entity;
    private int changeSetTxStatus = -1;

    public ChangeSetBackedTransactionSynchronization(ChangeSetPersister<Object> changeSetPersister, ChangeSetBacked entity) {
        this.changeSetPersister = changeSetPersister;
        this.entity = entity;
    }

    public void afterCommit() {
        logger.debug((Object)("After Commit called for " + this.entity));
        this.changeSetPersister.persistState(this.entity, this.entity.getChangeSet());
        this.changeSetTxStatus = 0;
    }

    public void afterCompletion(int status) {
        logger.debug((Object)("After Completion called with status = " + status));
        if (this.changeSetTxStatus == 0) {
            if (status == 0) {
                logger.debug((Object)("ChangedSetBackedTransactionSynchronization completed successfully for " + this.entity));
            } else {
                logger.error((Object)("ChangedSetBackedTransactionSynchronization failed for " + this.entity));
            }
        }
    }

    public void beforeCommit(boolean readOnly) {
    }

    public void beforeCompletion() {
    }

    public void flush() {
    }

    public void resume() {
        throw new IllegalStateException("ChangedSetBackedTransactionSynchronization does not support transaction suspension currently.");
    }

    public void suspend() {
        throw new IllegalStateException("ChangedSetBackedTransactionSynchronization does not support transaction suspension currently.");
    }
}

