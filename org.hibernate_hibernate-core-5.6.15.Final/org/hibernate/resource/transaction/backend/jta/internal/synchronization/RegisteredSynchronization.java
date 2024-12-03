/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.Synchronization
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import javax.transaction.Synchronization;
import org.hibernate.internal.CoreLogging;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinator;
import org.jboss.logging.Logger;

public class RegisteredSynchronization
implements Synchronization {
    private static final Logger log = CoreLogging.logger(RegisteredSynchronization.class);
    private final SynchronizationCallbackCoordinator synchronizationCallbackCoordinator;

    public RegisteredSynchronization(SynchronizationCallbackCoordinator synchronizationCallbackCoordinator) {
        this.synchronizationCallbackCoordinator = synchronizationCallbackCoordinator;
    }

    public void beforeCompletion() {
        log.trace((Object)"Registered JTA Synchronization : beforeCompletion()");
        this.synchronizationCallbackCoordinator.beforeCompletion();
    }

    public void afterCompletion(int status) {
        log.tracef("Registered JTA Synchronization : afterCompletion(%s)", status);
        this.synchronizationCallbackCoordinator.afterCompletion(status);
    }
}

