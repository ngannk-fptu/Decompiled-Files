/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinator;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackTarget;

public class SynchronizationCallbackCoordinatorNonTrackingImpl
implements SynchronizationCallbackCoordinator {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SynchronizationCallbackCoordinatorNonTrackingImpl.class);
    private final SynchronizationCallbackTarget target;

    public SynchronizationCallbackCoordinatorNonTrackingImpl(SynchronizationCallbackTarget target) {
        this.target = target;
        this.reset();
    }

    public void reset() {
    }

    @Override
    public void synchronizationRegistered() {
    }

    public void beforeCompletion() {
        log.trace("Synchronization coordinator: beforeCompletion()");
        if (!this.target.isActive()) {
            return;
        }
        this.target.beforeCompletion();
    }

    public void afterCompletion(int status) {
        log.tracef("Synchronization coordinator: afterCompletion(status=%s)", status);
        this.doAfterCompletion(JtaStatusHelper.isCommitted(status), false);
    }

    protected void doAfterCompletion(boolean successful, boolean delayed) {
        log.tracef("Synchronization coordinator: doAfterCompletion(successful=%s, delayed=%s)", successful, delayed);
        try {
            this.target.afterCompletion(successful, delayed);
        }
        finally {
            this.reset();
        }
    }

    @Override
    public void processAnyDelayedAfterCompletion() {
    }
}

