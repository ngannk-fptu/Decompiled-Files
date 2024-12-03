/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.springframework.orm.hibernate5.HibernateTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionStatus
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.confluence.impl.hibernate.TransactionInfo;
import java.util.Optional;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class ConfluenceHibernateTransactionManager
extends HibernateTransactionManager {
    private static final ThreadLocal<TransactionInfo> activeInfo = new ThreadLocal();

    private Optional<TransactionInfo> getActiveInfo() {
        return Optional.ofNullable(activeInfo.get());
    }

    private void setActiveInfo(Optional<TransactionInfo> node) {
        activeInfo.set(node.orElse(null));
    }

    protected void doBegin(Object transaction, TransactionDefinition txDef) {
        super.doBegin(transaction, txDef);
        Session session = this.getSessionFactory().getCurrentSession();
        int sessionId = System.identityHashCode(session);
        this.setActiveInfo(Optional.of(TransactionInfo.createChild(this.getActiveInfo(), txDef, sessionId)));
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        Optional<TransactionInfo> active = this.getActiveInfo();
        if (active.isPresent()) {
            this.setActiveInfo(active.get().getParent());
        } else {
            this.logger.warn((Object)"Transaction cleanup without known transaction");
        }
        super.doCleanupAfterCompletion(transaction);
    }

    protected void doCommit(DefaultTransactionStatus status) {
        try {
            if (this.logger.isDebugEnabled() && this.getActiveInfo().isPresent()) {
                this.logger.debug((Object)("About to commit. Transactions:\n" + this.transactionStateDescription()));
            }
            super.doCommit(status);
        }
        catch (RuntimeException e) {
            this.logger.warn((Object)("Commit failed. Rolling back. Error: " + e.getMessage()));
            throw e;
        }
    }

    protected void doRollback(DefaultTransactionStatus status) {
        if (this.getActiveInfo().isPresent()) {
            this.logger.warn((Object)("Performing rollback. Transactions:\n" + this.transactionStateDescription()));
        }
        super.doRollback(status);
    }

    private String transactionStateDescription() {
        try {
            return activeInfo.get().toString();
        }
        catch (RuntimeException e) {
            this.logger.warn((Object)("Failed to generate transaction state description: " + e.getMessage()), (Throwable)e);
            return "";
        }
    }
}

