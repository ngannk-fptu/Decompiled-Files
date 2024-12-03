/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.RollbackException
 */
package org.hibernate.resource.transaction.backend.jdbc.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.RollbackException;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.transaction.spi.IsolationDelegate;
import org.hibernate.engine.transaction.spi.TransactionObserver;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcIsolationDelegate;
import org.hibernate.resource.transaction.backend.jdbc.spi.JdbcResourceTransaction;
import org.hibernate.resource.transaction.backend.jdbc.spi.JdbcResourceTransactionAccess;
import org.hibernate.resource.transaction.internal.SynchronizationRegistryStandardImpl;
import org.hibernate.resource.transaction.spi.SynchronizationRegistry;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorOwner;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class JdbcResourceLocalTransactionCoordinatorImpl
implements TransactionCoordinator {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(JdbcResourceLocalTransactionCoordinatorImpl.class);
    private final TransactionCoordinatorBuilder transactionCoordinatorBuilder;
    private final JdbcResourceTransactionAccess jdbcResourceTransactionAccess;
    private final TransactionCoordinatorOwner transactionCoordinatorOwner;
    private final SynchronizationRegistryStandardImpl synchronizationRegistry = new SynchronizationRegistryStandardImpl();
    private final JpaCompliance jpaCompliance;
    private TransactionDriverControlImpl physicalTransactionDelegate;
    private int timeOut = -1;
    private transient List<TransactionObserver> observers = null;

    JdbcResourceLocalTransactionCoordinatorImpl(TransactionCoordinatorBuilder transactionCoordinatorBuilder, TransactionCoordinatorOwner owner, JdbcResourceTransactionAccess jdbcResourceTransactionAccess) {
        this.transactionCoordinatorBuilder = transactionCoordinatorBuilder;
        this.jdbcResourceTransactionAccess = jdbcResourceTransactionAccess;
        this.transactionCoordinatorOwner = owner;
        this.jpaCompliance = owner.getJdbcSessionOwner().getJdbcSessionContext().getSessionFactory().getSessionFactoryOptions().getJpaCompliance();
    }

    private Iterable<TransactionObserver> observers() {
        if (this.observers == null || this.observers.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<TransactionObserver>(this.observers);
    }

    @Override
    public TransactionCoordinator.TransactionDriver getTransactionDriverControl() {
        if (this.physicalTransactionDelegate == null) {
            this.physicalTransactionDelegate = new TransactionDriverControlImpl(this.jdbcResourceTransactionAccess.getResourceLocalTransaction());
        }
        return this.physicalTransactionDelegate;
    }

    @Override
    public void explicitJoin() {
        log.callingJoinTransactionOnNonJtaEntityManager();
    }

    @Override
    public boolean isJoined() {
        return this.physicalTransactionDelegate != null && this.getTransactionDriverControl().isActive(true);
    }

    @Override
    public void pulse() {
    }

    @Override
    public SynchronizationRegistry getLocalSynchronizations() {
        return this.synchronizationRegistry;
    }

    @Override
    public JpaCompliance getJpaCompliance() {
        return this.jpaCompliance;
    }

    @Override
    public boolean isActive() {
        return this.transactionCoordinatorOwner.isActive();
    }

    @Override
    public IsolationDelegate createIsolationDelegate() {
        JdbcSessionOwner jdbcSessionOwner = this.transactionCoordinatorOwner.getJdbcSessionOwner();
        return new JdbcIsolationDelegate(jdbcSessionOwner.getJdbcConnectionAccess(), jdbcSessionOwner.getJdbcSessionContext().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper());
    }

    @Override
    public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder() {
        return this.transactionCoordinatorBuilder;
    }

    @Override
    public void setTimeOut(int seconds) {
        this.timeOut = seconds;
    }

    @Override
    public int getTimeOut() {
        return this.timeOut;
    }

    private void afterBeginCallback() {
        if (this.timeOut > 0) {
            this.transactionCoordinatorOwner.setTransactionTimeOut(this.timeOut);
        }
        this.transactionCoordinatorOwner.startTransactionBoundary();
        this.transactionCoordinatorOwner.afterTransactionBegin();
        for (TransactionObserver observer : this.observers()) {
            observer.afterBegin();
        }
        log.trace("ResourceLocalTransactionCoordinatorImpl#afterBeginCallback");
    }

    private void beforeCompletionCallback() {
        log.trace("ResourceLocalTransactionCoordinatorImpl#beforeCompletionCallback");
        try {
            this.transactionCoordinatorOwner.beforeTransactionCompletion();
            this.synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
            for (TransactionObserver observer : this.observers()) {
                observer.beforeCompletion();
            }
        }
        catch (RuntimeException e) {
            if (this.physicalTransactionDelegate != null) {
                this.physicalTransactionDelegate.markRollbackOnly();
            }
            throw e;
        }
    }

    private void afterCompletionCallback(boolean successful) {
        log.tracef("ResourceLocalTransactionCoordinatorImpl#afterCompletionCallback(%s)", successful);
        int statusToSend = successful ? 3 : 5;
        this.synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion(statusToSend);
        this.transactionCoordinatorOwner.afterTransactionCompletion(successful, false);
        for (TransactionObserver observer : this.observers()) {
            observer.afterCompletion(successful, false);
        }
    }

    @Override
    public void addObserver(TransactionObserver observer) {
        if (this.observers == null) {
            this.observers = new ArrayList<TransactionObserver>(6);
        }
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(TransactionObserver observer) {
        if (this.observers != null) {
            this.observers.remove(observer);
        }
    }

    public class TransactionDriverControlImpl
    implements TransactionCoordinator.TransactionDriver {
        private final JdbcResourceTransaction jdbcResourceTransaction;
        private boolean invalid;
        private boolean rollbackOnly = false;

        public TransactionDriverControlImpl(JdbcResourceTransaction jdbcResourceTransaction) {
            this.jdbcResourceTransaction = jdbcResourceTransaction;
        }

        protected void invalidate() {
            this.invalid = true;
        }

        @Override
        public void begin() {
            this.errorIfInvalid();
            this.jdbcResourceTransaction.begin();
            JdbcResourceLocalTransactionCoordinatorImpl.this.afterBeginCallback();
        }

        protected void errorIfInvalid() {
            if (this.invalid) {
                throw new IllegalStateException("Physical-transaction delegate is no longer valid");
            }
        }

        @Override
        public void commit() {
            try {
                if (this.rollbackOnly) {
                    log.debugf("On commit, transaction was marked for roll-back only, rolling back", new Object[0]);
                    try {
                        this.rollback();
                        if (JdbcResourceLocalTransactionCoordinatorImpl.this.jpaCompliance.isJpaTransactionComplianceEnabled()) {
                            log.debugf("Throwing RollbackException on roll-back of transaction marked rollback-only on commit", new Object[0]);
                            throw new RollbackException("Transaction was marked for rollback-only");
                        }
                        return;
                    }
                    catch (RollbackException e) {
                        throw e;
                    }
                    catch (RuntimeException e) {
                        log.debug("Encountered failure rolling back failed commit", e);
                        throw e;
                    }
                }
                JdbcResourceLocalTransactionCoordinatorImpl.this.beforeCompletionCallback();
                this.jdbcResourceTransaction.commit();
                JdbcResourceLocalTransactionCoordinatorImpl.this.afterCompletionCallback(true);
            }
            catch (RollbackException e) {
                throw e;
            }
            catch (RuntimeException e) {
                try {
                    this.rollback();
                }
                catch (RuntimeException e2) {
                    log.debug("Encountered failure rolling back failed commit", e2);
                }
                throw e;
            }
        }

        @Override
        public void rollback() {
            try {
                TransactionStatus status = this.jdbcResourceTransaction.getStatus();
                if (this.rollbackOnly && status != TransactionStatus.NOT_ACTIVE || status == TransactionStatus.ACTIVE) {
                    this.jdbcResourceTransaction.rollback();
                    JdbcResourceLocalTransactionCoordinatorImpl.this.afterCompletionCallback(false);
                }
            }
            finally {
                this.rollbackOnly = false;
            }
        }

        @Override
        public TransactionStatus getStatus() {
            return this.rollbackOnly ? TransactionStatus.MARKED_ROLLBACK : this.jdbcResourceTransaction.getStatus();
        }

        @Override
        public void markRollbackOnly() {
            if (this.getStatus() != TransactionStatus.ROLLED_BACK) {
                if (log.isDebugEnabled()) {
                    log.debug("JDBC transaction marked for rollback-only (exception provided for stack trace)", new Exception("exception just for purpose of providing stack trace"));
                }
                this.rollbackOnly = true;
            }
        }
    }
}

