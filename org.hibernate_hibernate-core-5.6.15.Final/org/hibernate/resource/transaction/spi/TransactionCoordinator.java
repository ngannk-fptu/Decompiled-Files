/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.spi;

import org.hibernate.engine.transaction.spi.IsolationDelegate;
import org.hibernate.engine.transaction.spi.TransactionObserver;
import org.hibernate.jpa.spi.JpaCompliance;
import org.hibernate.resource.transaction.spi.SynchronizationRegistry;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public interface TransactionCoordinator {
    public TransactionCoordinatorBuilder getTransactionCoordinatorBuilder();

    public TransactionDriver getTransactionDriverControl();

    public SynchronizationRegistry getLocalSynchronizations();

    public JpaCompliance getJpaCompliance();

    public void explicitJoin();

    public boolean isJoined();

    public void pulse();

    public boolean isActive();

    public IsolationDelegate createIsolationDelegate();

    public void addObserver(TransactionObserver var1);

    public void removeObserver(TransactionObserver var1);

    public void setTimeOut(int var1);

    public int getTimeOut();

    default public boolean isTransactionActive() {
        return this.isTransactionActive(true);
    }

    default public boolean isTransactionActive(boolean isMarkedRollbackConsideredActive) {
        return this.isJoined() && this.getTransactionDriverControl().isActive(isMarkedRollbackConsideredActive);
    }

    default public void invalidate() {
    }

    public static interface TransactionDriver {
        public void begin();

        public void commit();

        public void rollback();

        public TransactionStatus getStatus();

        public void markRollbackOnly();

        default public boolean isActive(boolean isMarkedRollbackConsideredActive) {
            TransactionStatus status = this.getStatus();
            return TransactionStatus.ACTIVE == status || isMarkedRollbackConsideredActive && TransactionStatus.MARKED_ROLLBACK == status;
        }
    }
}

