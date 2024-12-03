/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.AbstractLoggingTransactionManager;
import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import net.java.ao.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SalTransactionManager
extends AbstractLoggingTransactionManager {
    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;
    private final TransactionSynchronisationManager synchManager;
    private final Logger log = LoggerFactory.getLogger(SalTransactionManager.class);

    SalTransactionManager(TransactionTemplate transactionTemplate, EntityManager entityManager, TransactionSynchronisationManager synchManager) {
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate);
        this.entityManager = (EntityManager)Preconditions.checkNotNull((Object)entityManager);
        this.synchManager = (TransactionSynchronisationManager)Preconditions.checkNotNull((Object)synchManager);
    }

    @Override
    <T> T inTransaction(TransactionCallback<T> callback) {
        Object result;
        Runnable commitAction = this.createCommitAction(this.entityManager);
        Runnable rollBackAction = this.createRollbackAction(this.entityManager);
        boolean transactionSynced = this.synchManager.runOnSuccessfulCommit(commitAction);
        if (transactionSynced) {
            this.synchManager.runOnRollBack(rollBackAction);
        }
        try {
            result = this.transactionTemplate.execute(callback);
        }
        catch (RuntimeException exception) {
            if (!transactionSynced) {
                try {
                    rollBackAction.run();
                }
                catch (Exception ex) {
                    this.log.error("Error occurred performing post roll back action, logging and throwing original exception", (Throwable)ex);
                }
            }
            throw exception;
        }
        if (!transactionSynced) {
            commitAction.run();
        }
        return (T)result;
    }

    private Runnable createCommitAction(EntityManager entityManager) {
        return new Runnable(){

            @Override
            public void run() {
                SalTransactionManager.this.log.debug("Flushing entityManager due to commit");
            }
        };
    }

    private Runnable createRollbackAction(EntityManager entityManager) {
        return new Runnable(){

            @Override
            public void run() {
                SalTransactionManager.this.log.info("Flushing entityManager due to rollback");
            }
        };
    }
}

