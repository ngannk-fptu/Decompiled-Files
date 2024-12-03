/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 */
package org.hibernate.resource.transaction.internal;

import java.util.LinkedHashSet;
import javax.transaction.Synchronization;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.LocalSynchronizationException;
import org.hibernate.resource.transaction.NullSynchronizationException;
import org.hibernate.resource.transaction.spi.SynchronizationRegistryImplementor;

public class SynchronizationRegistryStandardImpl
implements SynchronizationRegistryImplementor {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SynchronizationRegistryStandardImpl.class);
    private LinkedHashSet<Synchronization> synchronizations;

    public int getNumberOfRegisteredSynchronizations() {
        return this.synchronizations == null ? 0 : this.synchronizations.size();
    }

    @Override
    public void registerSynchronization(Synchronization synchronization) {
        boolean added;
        if (synchronization == null) {
            throw new NullSynchronizationException();
        }
        if (this.synchronizations == null) {
            this.synchronizations = new LinkedHashSet();
        }
        if (!(added = this.synchronizations.add(synchronization))) {
            log.synchronizationAlreadyRegistered(synchronization);
        }
    }

    @Override
    public void notifySynchronizationsBeforeTransactionCompletion() {
        log.trace("SynchronizationRegistryStandardImpl.notifySynchronizationsBeforeTransactionCompletion");
        if (this.synchronizations != null) {
            for (Synchronization synchronization : this.synchronizations) {
                try {
                    synchronization.beforeCompletion();
                }
                catch (Throwable t) {
                    log.synchronizationFailed(synchronization, t);
                    throw new LocalSynchronizationException("Exception calling user Synchronization (beforeCompletion): " + synchronization.getClass().getName(), t);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void notifySynchronizationsAfterTransactionCompletion(int status) {
        log.tracef("SynchronizationRegistryStandardImpl.notifySynchronizationsAfterTransactionCompletion(%s)", status);
        if (this.synchronizations == null) return;
        try {
            for (Synchronization synchronization : this.synchronizations) {
                try {
                    synchronization.afterCompletion(status);
                }
                catch (Throwable t) {
                    log.synchronizationFailed(synchronization, t);
                    throw new LocalSynchronizationException("Exception calling user Synchronization (afterCompletion): " + synchronization.getClass().getName(), t);
                    return;
                }
            }
        }
        finally {
            this.clearSynchronizations();
        }
    }

    @Override
    public void clearSynchronizations() {
        log.debug("Clearing local Synchronizations");
        if (this.synchronizations != null) {
            this.synchronizations.clear();
        }
    }
}

