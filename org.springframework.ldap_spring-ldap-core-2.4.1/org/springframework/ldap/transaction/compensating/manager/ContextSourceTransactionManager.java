/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.support.AbstractPlatformTransactionManager
 *  org.springframework.transaction.support.DefaultTransactionStatus
 */
package org.springframework.ldap.transaction.compensating.manager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManagerDelegate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.compensating.support.CompensatingTransactionObject;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class ContextSourceTransactionManager
extends AbstractPlatformTransactionManager
implements InitializingBean {
    private static final long serialVersionUID = 7138208218687237856L;
    private ContextSourceTransactionManagerDelegate delegate = new ContextSourceTransactionManagerDelegate();

    protected void doBegin(Object transaction, TransactionDefinition definition) {
        this.delegate.doBegin(transaction, definition);
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        this.delegate.doCleanupAfterCompletion(transaction);
    }

    protected void doCommit(DefaultTransactionStatus status) {
        this.delegate.doCommit(status);
    }

    protected Object doGetTransaction() {
        return this.delegate.doGetTransaction();
    }

    protected void doRollback(DefaultTransactionStatus status) {
        this.delegate.doRollback(status);
    }

    public ContextSource getContextSource() {
        return this.delegate.getContextSource();
    }

    public void setContextSource(ContextSource contextSource) {
        this.delegate.setContextSource(contextSource);
    }

    public void setRenamingStrategy(TempEntryRenamingStrategy renamingStrategy) {
        this.delegate.setRenamingStrategy(renamingStrategy);
    }

    public void afterPropertiesSet() throws Exception {
        this.delegate.checkRenamingStrategy();
    }

    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        CompensatingTransactionObject txObject = (CompensatingTransactionObject)transaction;
        return txObject.getHolder() != null;
    }
}

