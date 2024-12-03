/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.engine.transaction.jta.platform.internal.JtaSynchronizationStrategy;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryAccess;

public class SynchronizationRegistryBasedSynchronizationStrategy
implements JtaSynchronizationStrategy {
    private final SynchronizationRegistryAccess synchronizationRegistryAccess;

    public SynchronizationRegistryBasedSynchronizationStrategy(SynchronizationRegistryAccess synchronizationRegistryAccess) {
        this.synchronizationRegistryAccess = synchronizationRegistryAccess;
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        this.synchronizationRegistryAccess.getSynchronizationRegistry().registerInterposedSynchronization(synchronization);
    }

    @Override
    public boolean canRegisterSynchronization() {
        TransactionSynchronizationRegistry registry = this.synchronizationRegistryAccess.getSynchronizationRegistry();
        return JtaStatusHelper.isActive(registry.getTransactionStatus()) && !registry.getRollbackOnly();
    }
}

