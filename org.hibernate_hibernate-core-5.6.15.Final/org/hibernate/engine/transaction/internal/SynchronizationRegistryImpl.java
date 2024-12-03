/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.transaction.internal;

import java.util.LinkedHashSet;
import javax.transaction.Synchronization;
import org.hibernate.engine.transaction.internal.NullSynchronizationException;
import org.hibernate.engine.transaction.spi.SynchronizationRegistry;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class SynchronizationRegistryImpl
implements SynchronizationRegistry {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)SynchronizationRegistryImpl.class.getName());
    private LinkedHashSet<Synchronization> synchronizations;

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
            LOG.synchronizationAlreadyRegistered(synchronization);
        }
    }

    @Override
    public void notifySynchronizationsBeforeTransactionCompletion() {
        if (this.synchronizations != null) {
            for (Synchronization synchronization : this.synchronizations) {
                try {
                    synchronization.beforeCompletion();
                }
                catch (Throwable t) {
                    LOG.synchronizationFailed(synchronization, t);
                }
            }
        }
    }

    @Override
    public void notifySynchronizationsAfterTransactionCompletion(int status) {
        if (this.synchronizations != null) {
            for (Synchronization synchronization : this.synchronizations) {
                try {
                    synchronization.afterCompletion(status);
                }
                catch (Throwable t) {
                    LOG.synchronizationFailed(synchronization, t);
                }
            }
        }
    }

    void clearSynchronizations() {
        if (this.synchronizations != null) {
            this.synchronizations.clear();
            this.synchronizations = null;
        }
    }
}

