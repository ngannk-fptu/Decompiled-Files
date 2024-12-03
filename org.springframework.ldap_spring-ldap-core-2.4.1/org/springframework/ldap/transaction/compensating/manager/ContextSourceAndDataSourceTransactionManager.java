/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.datasource.DataSourceTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionSuspensionNotSupportedException
 *  org.springframework.transaction.support.DefaultTransactionStatus
 */
package org.springframework.ldap.transaction.compensating.manager;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManagerDelegate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class ContextSourceAndDataSourceTransactionManager
extends DataSourceTransactionManager {
    private static final long serialVersionUID = 6832868697460384648L;
    private ContextSourceTransactionManagerDelegate ldapManagerDelegate = new ContextSourceTransactionManagerDelegate();

    public ContextSourceAndDataSourceTransactionManager() {
        this.setNestedTransactionAllowed(false);
    }

    protected boolean isExistingTransaction(Object transaction) {
        return false;
    }

    protected Object doGetTransaction() {
        Object dataSourceTransactionObject = super.doGetTransaction();
        Object contextSourceTransactionObject = this.ldapManagerDelegate.doGetTransaction();
        return new ContextSourceAndDataSourceTransactionObject(contextSourceTransactionObject, dataSourceTransactionObject);
    }

    protected void doBegin(Object transaction, TransactionDefinition definition) {
        ContextSourceAndDataSourceTransactionObject actualTransactionObject = (ContextSourceAndDataSourceTransactionObject)transaction;
        super.doBegin(actualTransactionObject.getDataSourceTransactionObject(), definition);
        try {
            this.ldapManagerDelegate.doBegin(actualTransactionObject.getLdapTransactionObject(), definition);
        }
        catch (TransactionException e) {
            super.doCleanupAfterCompletion(actualTransactionObject.getDataSourceTransactionObject());
            throw e;
        }
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        ContextSourceAndDataSourceTransactionObject actualTransactionObject = (ContextSourceAndDataSourceTransactionObject)transaction;
        super.doCleanupAfterCompletion(actualTransactionObject.getDataSourceTransactionObject());
        this.ldapManagerDelegate.doCleanupAfterCompletion(actualTransactionObject.getLdapTransactionObject());
    }

    protected void doCommit(DefaultTransactionStatus status) {
        ContextSourceAndDataSourceTransactionObject actualTransactionObject = (ContextSourceAndDataSourceTransactionObject)status.getTransaction();
        try {
            super.doCommit(new DefaultTransactionStatus(actualTransactionObject.getDataSourceTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
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
        ContextSourceAndDataSourceTransactionObject actualTransactionObject = (ContextSourceAndDataSourceTransactionObject)status.getTransaction();
        super.doRollback(new DefaultTransactionStatus(actualTransactionObject.getDataSourceTransactionObject(), status.isNewTransaction(), status.isNewSynchronization(), status.isReadOnly(), status.isDebug(), status.getSuspendedResources()));
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

    private static final class ContextSourceAndDataSourceTransactionObject {
        private Object ldapTransactionObject;
        private Object dataSourceTransactionObject;

        public ContextSourceAndDataSourceTransactionObject(Object ldapTransactionObject, Object dataSourceTransactionObject) {
            this.ldapTransactionObject = ldapTransactionObject;
            this.dataSourceTransactionObject = dataSourceTransactionObject;
        }

        public Object getDataSourceTransactionObject() {
            return this.dataSourceTransactionObject;
        }

        public Object getLdapTransactionObject() {
            return this.ldapTransactionObject;
        }
    }
}

