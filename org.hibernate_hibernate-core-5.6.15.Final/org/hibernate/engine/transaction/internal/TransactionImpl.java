/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.transaction.internal;

import javax.transaction.Synchronization;
import org.hibernate.HibernateException;
import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.spi.TransactionImplementor;
import org.hibernate.internal.AbstractSharedSessionContract;
import org.hibernate.internal.CoreLogging;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public class TransactionImpl
implements TransactionImplementor {
    private static final Logger LOG = CoreLogging.logger(TransactionImpl.class);
    private final TransactionCoordinator transactionCoordinator;
    private final JpaCompliance jpaCompliance;
    private final AbstractSharedSessionContract session;
    private TransactionCoordinator.TransactionDriver transactionDriverControl;

    public TransactionImpl(TransactionCoordinator transactionCoordinator, AbstractSharedSessionContract session) {
        this.transactionCoordinator = transactionCoordinator;
        this.jpaCompliance = session.getFactory().getSessionFactoryOptions().getJpaCompliance();
        this.session = session;
        if (session.isOpen() && transactionCoordinator.isActive()) {
            this.transactionDriverControl = transactionCoordinator.getTransactionDriverControl();
        } else {
            LOG.debug((Object)"TransactionImpl created on closed Session/EntityManager");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("On TransactionImpl creation, JpaCompliance#isJpaTransactionComplianceEnabled == %s", (Object)this.jpaCompliance.isJpaTransactionComplianceEnabled());
        }
    }

    public void begin() {
        if (!this.session.isOpen()) {
            throw new IllegalStateException("Cannot begin Transaction on closed Session/EntityManager");
        }
        if (this.transactionDriverControl == null) {
            this.transactionDriverControl = this.transactionCoordinator.getTransactionDriverControl();
        }
        if (this.isActive()) {
            if (this.jpaCompliance.isJpaTransactionComplianceEnabled() || !this.transactionCoordinator.getTransactionCoordinatorBuilder().isJta()) {
                throw new IllegalStateException("Transaction already active");
            }
            return;
        }
        LOG.debug((Object)"begin");
        this.transactionDriverControl.begin();
    }

    public void commit() {
        if (!this.isActive(true)) {
            throw new IllegalStateException("Transaction not successfully started");
        }
        LOG.debug((Object)"committing");
        try {
            this.internalGetTransactionDriverControl().commit();
        }
        catch (RuntimeException e) {
            throw this.session.getExceptionConverter().convertCommitException(e);
        }
    }

    public TransactionCoordinator.TransactionDriver internalGetTransactionDriverControl() {
        if (this.transactionDriverControl == null) {
            throw new IllegalStateException("Transaction was not properly begun/started");
        }
        return this.transactionDriverControl;
    }

    public void rollback() {
        if (!this.isActive() && this.jpaCompliance.isJpaTransactionComplianceEnabled()) {
            throw new IllegalStateException("JPA compliance dictates throwing IllegalStateException when #rollback is called on non-active transaction");
        }
        TransactionStatus status = this.getStatus();
        if (status == TransactionStatus.ROLLED_BACK || status == TransactionStatus.NOT_ACTIVE) {
            LOG.debug((Object)"rollback() called on an inactive transaction");
            return;
        }
        if (!status.canRollback()) {
            throw new TransactionException("Cannot rollback transaction in current status [" + status.name() + "]");
        }
        LOG.debug((Object)"rolling back");
        if (status != TransactionStatus.FAILED_COMMIT || this.allowFailedCommitToPhysicallyRollback()) {
            this.internalGetTransactionDriverControl().rollback();
        }
    }

    public boolean isActive() {
        return this.isActive(true);
    }

    @Override
    public boolean isActive(boolean isMarkedForRollbackConsideredActive) {
        if (this.transactionDriverControl == null) {
            if (this.session.isOpen()) {
                this.transactionDriverControl = this.transactionCoordinator.getTransactionDriverControl();
            } else {
                return false;
            }
        }
        return this.transactionDriverControl.isActive(isMarkedForRollbackConsideredActive);
    }

    @Override
    public TransactionStatus getStatus() {
        if (this.transactionDriverControl == null) {
            if (this.session.isOpen()) {
                this.transactionDriverControl = this.transactionCoordinator.getTransactionDriverControl();
            } else {
                return TransactionStatus.NOT_ACTIVE;
            }
        }
        return this.transactionDriverControl.getStatus();
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) throws HibernateException {
        this.transactionCoordinator.getLocalSynchronizations().registerSynchronization(synchronization);
    }

    @Override
    public void setTimeout(int seconds) {
        this.transactionCoordinator.setTimeOut(seconds);
    }

    @Override
    public int getTimeout() {
        return this.transactionCoordinator.getTimeOut();
    }

    @Override
    public void markRollbackOnly() {
        if (this.isActive()) {
            this.internalGetTransactionDriverControl().markRollbackOnly();
        }
    }

    public void setRollbackOnly() {
        if (!this.isActive()) {
            if (this.jpaCompliance.isJpaTransactionComplianceEnabled()) {
                throw new IllegalStateException("JPA compliance dictates throwing IllegalStateException when #setRollbackOnly is called on non-active transaction");
            }
            LOG.debug((Object)"#setRollbackOnly called on a not-active transaction");
        } else {
            this.markRollbackOnly();
        }
    }

    public boolean getRollbackOnly() {
        if (!this.isActive() && this.jpaCompliance.isJpaTransactionComplianceEnabled()) {
            throw new IllegalStateException("JPA compliance dictates throwing IllegalStateException when #getRollbackOnly is called on non-active transaction");
        }
        return this.getStatus() == TransactionStatus.MARKED_ROLLBACK;
    }

    protected boolean allowFailedCommitToPhysicallyRollback() {
        return false;
    }
}

