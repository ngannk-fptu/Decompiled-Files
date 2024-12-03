/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.orm.hibernate5.HibernateTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionSuspensionNotSupportedException
 *  org.springframework.transaction.support.DefaultTransactionStatus
 */
package org.springframework.ldap.transaction.compensating.manager;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManagerDelegate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class ContextSourceAndHibernateTransactionManager
extends HibernateTransactionManager {
    private static final long serialVersionUID = 1L;
    private ContextSourceTransactionManagerDelegate ldapManagerDelegate = new ContextSourceTransactionManagerDelegate();

    protected boolean isExistingTransaction(Object transaction) {
        ContextSourceAndHibernateTransactionObject actualTransactionObject = (ContextSourceAndHibernateTransactionObject)transaction;
        return super.isExistingTransaction(actualTransactionObject.getHibernateTransactionObject());
    }

    protected Object doGetTransaction() {
        Object dataSourceTransactionObject = super.doGetTransaction();
        Object contextSourceTransactionObject = this.ldapManagerDelegate.doGetTransaction();
        return new ContextSourceAndHibernateTransactionObject(contextSourceTransactionObject, dataSourceTransactionObject);
    }

    protected void doBegin(Object transaction, TransactionDefinition definition) {
        ContextSourceAndHibernateTransactionObject actualTransactionObject = (ContextSourceAndHibernateTransactionObject)transaction;
        super.doBegin(actualTransactionObject.getHibernateTransactionObject(), definition);
        try {
            this.ldapManagerDelegate.doBegin(actualTransactionObject.getLdapTransactionObject(), definition);
        }
        catch (TransactionException e) {
            super.doCleanupAfterCompletion(actualTransactionObject.getHibernateTransactionObject());
            throw e;
        }
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        ContextSourceAndHibernateTransactionObject actualTransactionObject = (ContextSourceAndHibernateTransactionObject)transaction;
        super.doCleanupAfterCompletion(actualTransactionObject.getHibernateTransactionObject());
        this.ldapManagerDelegate.doCleanupAfterCompletion(actualTransactionObject.getLdapTransactionObject());
    }

    protected void doCommit(DefaultTransactionStatus status) {
        ContextSourceAndHibernateTransactionObject actualTransactionObject = (ContextSourceAndHibernateTransactionObject)status.getTransaction();
        try {
            super.doCommit(new DefaultTransactionStatus(actualTransactionObject.getHibernateTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
        }
        catch (TransactionException ex) {
            if (this.isRollbackOnCommitFailure()) {
                this.logger.debug((Object)"Failed to commit db resource, rethrowing", (Throwable)ex);
                throw ex;
            }
            this.logger.warn((Object)"Failed to commit and resource is rollbackOnCommit not set - proceeding to commit ldap resource.");
        }
        this.ldapManagerDelegate.doCommit(new DefaultTransactionStatus(actualTransactionObject.getLdapTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
    }

    protected void doRollback(DefaultTransactionStatus status) {
        ContextSourceAndHibernateTransactionObject actualTransactionObject = (ContextSourceAndHibernateTransactionObject)status.getTransaction();
        super.doRollback(new DefaultTransactionStatus(actualTransactionObject.getHibernateTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
        this.ldapManagerDelegate.doRollback(new DefaultTransactionStatus(actualTransactionObject.getLdapTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
    }

    public ContextSource getContextSource() {
        return this.ldapManagerDelegate.getContextSource();
    }

    public void setContextSource(ContextSource contextSource) {
        this.ldapManagerDelegate.setContextSource(contextSource);
    }

    public void setRenamingStrategy(TempEntryRenamingStrategy renamingStrategy) {
        this.ldapManagerDelegate.setRenamingStrategy(renamingStrategy);
    }

    protected Object doSuspend(Object transaction) {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + ((Object)((Object)this)).getClass().getName() + "] does not support transaction suspension");
    }

    protected void doResume(Object transaction, Object suspendedResources) {
        throw new TransactionSuspensionNotSupportedException("Transaction manager [" + ((Object)((Object)this)).getClass().getName() + "] does not support transaction suspension");
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.ldapManagerDelegate.checkRenamingStrategy();
    }

    private static final class ContextSourceAndHibernateTransactionObject {
        private Object ldapTransactionObject;
        private Object hibernateTransactionObject;

        public ContextSourceAndHibernateTransactionObject(Object ldapTransactionObject, Object hibernateTransactionObject) {
            this.ldapTransactionObject = ldapTransactionObject;
            this.hibernateTransactionObject = hibernateTransactionObject;
        }

        public Object getHibernateTransactionObject() {
            return this.hibernateTransactionObject;
        }

        public Object getLdapTransactionObject() {
            return this.ldapTransactionObject;
        }
    }
}

