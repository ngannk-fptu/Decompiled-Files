/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.Synchronization;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.engine.transaction.jta.platform.internal.JtaSynchronizationStrategy;
import org.hibernate.engine.transaction.jta.platform.internal.TransactionManagerAccess;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class TransactionManagerBasedSynchronizationStrategy
implements JtaSynchronizationStrategy {
    private final TransactionManagerAccess transactionManagerAccess;

    public TransactionManagerBasedSynchronizationStrategy(TransactionManagerAccess transactionManagerAccess) {
        this.transactionManagerAccess = transactionManagerAccess;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        try {
            this.transactionManagerAccess.getTransactionManager().getTransaction().registerSynchronization(synchronization);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not access JTA Transaction to register synchronization", e);
        }
    }

    @Override
    public boolean canRegisterSynchronization() {
        return JtaStatusHelper.isActive(this.transactionManagerAccess.getTransactionManager());
    }
}

