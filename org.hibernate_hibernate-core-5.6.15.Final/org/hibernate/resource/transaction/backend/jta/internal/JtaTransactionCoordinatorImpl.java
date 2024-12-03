/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.transaction.backend.jta.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.HibernateException;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.spi.IsolationDelegate;
import org.hibernate.engine.transaction.spi.TransactionObserver;
import org.hibernate.internal.CoreLogging;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.resource.transaction.TransactionRequiredForJoinException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaIsolationDelegate;
import org.hibernate.resource.transaction.backend.jta.internal.JtaPlatformInaccessibleException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionAdapter;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionAdapterTransactionManagerImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionAdapterUserTransactionImpl;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.RegisteredSynchronization;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinator;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinatorNonTrackingImpl;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinatorTrackingImpl;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackTarget;
import org.hibernate.resource.transaction.internal.SynchronizationRegistryStandardImpl;
import org.hibernate.resource.transaction.spi.SynchronizationRegistry;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorOwner;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public class JtaTransactionCoordinatorImpl
implements TransactionCoordinator,
SynchronizationCallbackTarget {
    private static final Logger log = CoreLogging.logger(JtaTransactionCoordinatorImpl.class);
    private final TransactionCoordinatorBuilder transactionCoordinatorBuilder;
    private final TransactionCoordinatorOwner transactionCoordinatorOwner;
    private final JtaPlatform jtaPlatform;
    private final boolean autoJoinTransactions;
    private final boolean preferUserTransactions;
    private final boolean performJtaThreadTracking;
    private boolean synchronizationRegistered;
    private SynchronizationCallbackCoordinator callbackCoordinator;
    private TransactionDriverControlImpl physicalTransactionDelegate;
    private final SynchronizationRegistryStandardImpl synchronizationRegistry = new SynchronizationRegistryStandardImpl();
    private int timeOut = -1;
    private transient List<TransactionObserver> observers = null;

    JtaTransactionCoordinatorImpl(TransactionCoordinatorBuilder transactionCoordinatorBuilder, TransactionCoordinatorOwner owner, boolean autoJoinTransactions, JtaPlatform jtaPlatform) {
        this.transactionCoordinatorBuilder = transactionCoordinatorBuilder;
        this.transactionCoordinatorOwner = owner;
        this.autoJoinTransactions = autoJoinTransactions;
        JdbcSessionContext jdbcSessionContext = owner.getJdbcSessionOwner().getJdbcSessionContext();
        this.jtaPlatform = jtaPlatform;
        SessionFactoryOptions sessionFactoryOptions = jdbcSessionContext.getSessionFactory().getSessionFactoryOptions();
        this.preferUserTransactions = sessionFactoryOptions.isPreferUserTransaction();
        this.performJtaThreadTracking = sessionFactoryOptions.isJtaTrackByThread();
        this.synchronizationRegistered = false;
        this.pulse();
    }

    public JtaTransactionCoordinatorImpl(TransactionCoordinatorBuilder transactionCoordinatorBuilder, TransactionCoordinatorOwner owner, boolean autoJoinTransactions, JtaPlatform jtaPlatform, boolean preferUserTransactions, boolean performJtaThreadTracking, TransactionObserver ... observers) {
        this.transactionCoordinatorBuilder = transactionCoordinatorBuilder;
        this.transactionCoordinatorOwner = owner;
        this.autoJoinTransactions = autoJoinTransactions;
        this.jtaPlatform = jtaPlatform;
        this.preferUserTransactions = preferUserTransactions;
        this.performJtaThreadTracking = performJtaThreadTracking;
        if (observers != null) {
            this.observers = new ArrayList<TransactionObserver>(observers.length);
            Collections.addAll(this.observers, observers);
        }
        this.synchronizationRegistered = false;
        this.pulse();
    }

    private Iterable<TransactionObserver> observers() {
        if (this.observers == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<TransactionObserver>(this.observers);
    }

    public SynchronizationCallbackCoordinator getSynchronizationCallbackCoordinator() {
        if (this.callbackCoordinator == null) {
            this.callbackCoordinator = this.performJtaThreadTracking ? new SynchronizationCallbackCoordinatorTrackingImpl(this) : new SynchronizationCallbackCoordinatorNonTrackingImpl(this);
        }
        return this.callbackCoordinator;
    }

    @Override
    public void pulse() {
        if (!this.autoJoinTransactions) {
            return;
        }
        if (this.synchronizationRegistered) {
            return;
        }
        if (!this.jtaPlatform.canRegisterSynchronization()) {
            log.trace((Object)"JTA platform says we cannot currently register synchronization; skipping");
            return;
        }
        this.joinJtaTransaction();
    }

    private void joinJtaTransaction() {
        if (this.synchronizationRegistered) {
            return;
        }
        this.jtaPlatform.registerSynchronization(new RegisteredSynchronization(this.getSynchronizationCallbackCoordinator()));
        this.getSynchronizationCallbackCoordinator().synchronizationRegistered();
        this.synchronizationRegistered = true;
        log.debug((Object)"Hibernate RegisteredSynchronization successfully registered with JTA platform");
        this.getTransactionCoordinatorOwner().startTransactionBoundary();
    }

    @Override
    public void explicitJoin() {
        if (this.synchronizationRegistered) {
            log.debug((Object)"JTA transaction was already joined (RegisteredSynchronization already registered)");
            return;
        }
        if (this.getTransactionDriverControl().getStatus() != TransactionStatus.ACTIVE) {
            throw new TransactionRequiredForJoinException("Explicitly joining a JTA transaction requires a JTA transaction be currently active");
        }
        this.joinJtaTransaction();
    }

    @Override
    public boolean isJoined() {
        return this.synchronizationRegistered;
    }

    public boolean isSynchronizationRegistered() {
        return this.synchronizationRegistered;
    }

    public TransactionCoordinatorOwner getTransactionCoordinatorOwner() {
        return this.transactionCoordinatorOwner;
    }

    @Override
    public JpaCompliance getJpaCompliance() {
        return this.transactionCoordinatorOwner.getJdbcSessionOwner().getJdbcSessionContext().getSessionFactory().getSessionFactoryOptions().getJpaCompliance();
    }

    @Override
    public TransactionCoordinator.TransactionDriver getTransactionDriverControl() {
        if (this.physicalTransactionDelegate == null) {
            this.physicalTransactionDelegate = this.makePhysicalTransactionDelegate();
        }
        return this.physicalTransactionDelegate;
    }

    private TransactionDriverControlImpl makePhysicalTransactionDelegate() {
        JtaTransactionAdapter adapter;
        if (this.preferUserTransactions) {
            adapter = this.makeUserTransactionAdapter();
            if (adapter == null) {
                log.debug((Object)"Unable to access UserTransaction, attempting to use TransactionManager instead");
                adapter = this.makeTransactionManagerAdapter();
            }
        } else {
            adapter = this.makeTransactionManagerAdapter();
            if (adapter == null) {
                log.debug((Object)"Unable to access TransactionManager, attempting to use UserTransaction instead");
                adapter = this.makeUserTransactionAdapter();
            }
        }
        if (adapter == null) {
            throw new JtaPlatformInaccessibleException("Unable to access TransactionManager or UserTransaction to make physical transaction delegate");
        }
        return new TransactionDriverControlImpl(adapter);
    }

    private JtaTransactionAdapter makeUserTransactionAdapter() {
        try {
            UserTransaction userTransaction = this.jtaPlatform.retrieveUserTransaction();
            if (userTransaction != null) {
                return new JtaTransactionAdapterUserTransactionImpl(userTransaction);
            }
            log.debug((Object)"JtaPlatform#retrieveUserTransaction returned null");
        }
        catch (Exception ignore) {
            log.debugf("JtaPlatform#retrieveUserTransaction threw an exception [%s]", (Object)ignore.getMessage());
        }
        return null;
    }

    private JtaTransactionAdapter makeTransactionManagerAdapter() {
        try {
            TransactionManager transactionManager = this.jtaPlatform.retrieveTransactionManager();
            if (transactionManager != null) {
                return new JtaTransactionAdapterTransactionManagerImpl(transactionManager);
            }
            log.debug((Object)"JtaPlatform#retrieveTransactionManager returned null");
        }
        catch (Exception ignore) {
            log.debugf("JtaPlatform#retrieveTransactionManager threw an exception [%s]", (Object)ignore.getMessage());
        }
        return null;
    }

    @Override
    public SynchronizationRegistry getLocalSynchronizations() {
        return this.synchronizationRegistry;
    }

    @Override
    public boolean isActive() {
        return this.transactionCoordinatorOwner.isActive();
    }

    public boolean isJtaTransactionCurrentlyActive() {
        return this.getTransactionDriverControl().getStatus() == TransactionStatus.ACTIVE;
    }

    @Override
    public IsolationDelegate createIsolationDelegate() {
        JdbcSessionOwner jdbcSessionOwner = this.transactionCoordinatorOwner.getJdbcSessionOwner();
        return new JtaIsolationDelegate(jdbcSessionOwner.getJdbcConnectionAccess(), jdbcSessionOwner.getJdbcSessionContext().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper(), this.jtaPlatform.retrieveTransactionManager());
    }

    @Override
    public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder() {
        return this.transactionCoordinatorBuilder;
    }

    @Override
    public void setTimeOut(int seconds) {
        this.timeOut = seconds;
        this.physicalTransactionDelegate.jtaTransactionAdapter.setTimeOut(seconds);
    }

    @Override
    public int getTimeOut() {
        return this.timeOut;
    }

    @Override
    public void invalidate() {
        if (this.physicalTransactionDelegate != null) {
            this.physicalTransactionDelegate.invalidate();
        }
        this.physicalTransactionDelegate = null;
    }

    @Override
    public void beforeCompletion() {
        try {
            this.transactionCoordinatorOwner.beforeTransactionCompletion();
        }
        catch (HibernateException e) {
            this.physicalTransactionDelegate.markRollbackOnly();
            throw e;
        }
        catch (RuntimeException re) {
            this.physicalTransactionDelegate.markRollbackOnly();
            throw re;
        }
        finally {
            this.synchronizationRegistry.notifySynchronizationsBeforeTransactionCompletion();
            for (TransactionObserver observer : this.observers()) {
                observer.beforeCompletion();
            }
        }
    }

    @Override
    public void afterCompletion(boolean successful, boolean delayed) {
        if (!this.transactionCoordinatorOwner.isActive()) {
            return;
        }
        int statusToSend = successful ? 3 : 5;
        this.synchronizationRegistry.notifySynchronizationsAfterTransactionCompletion(statusToSend);
        this.transactionCoordinatorOwner.afterTransactionCompletion(successful, delayed);
        for (TransactionObserver observer : this.observers()) {
            observer.afterCompletion(successful, delayed);
        }
        this.synchronizationRegistered = false;
    }

    @Override
    public void addObserver(TransactionObserver observer) {
        if (this.observers == null) {
            this.observers = new ArrayList<TransactionObserver>(3);
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
        private final JtaTransactionAdapter jtaTransactionAdapter;
        private boolean invalid;

        public TransactionDriverControlImpl(JtaTransactionAdapter jtaTransactionAdapter) {
            this.jtaTransactionAdapter = jtaTransactionAdapter;
        }

        protected void invalidate() {
            this.invalid = true;
        }

        @Override
        public void begin() {
            this.errorIfInvalid();
            this.jtaTransactionAdapter.begin();
            JtaTransactionCoordinatorImpl.this.joinJtaTransaction();
        }

        protected void errorIfInvalid() {
            if (this.invalid) {
                throw new IllegalStateException("Physical-transaction delegate is no longer valid");
            }
        }

        @Override
        public void commit() {
            this.errorIfInvalid();
            JtaTransactionCoordinatorImpl.this.getTransactionCoordinatorOwner().flushBeforeTransactionCompletion();
            this.jtaTransactionAdapter.commit();
        }

        @Override
        public void rollback() {
            this.errorIfInvalid();
            this.jtaTransactionAdapter.rollback();
        }

        @Override
        public TransactionStatus getStatus() {
            return this.jtaTransactionAdapter.getStatus();
        }

        @Override
        public void markRollbackOnly() {
            this.jtaTransactionAdapter.markRollbackOnly();
        }
    }
}

