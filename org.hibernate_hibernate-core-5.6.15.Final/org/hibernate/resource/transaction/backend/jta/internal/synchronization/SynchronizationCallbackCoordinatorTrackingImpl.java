/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.transaction.backend.jta.internal.synchronization;

import org.hibernate.HibernateException;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinatorNonTrackingImpl;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackTarget;

public class SynchronizationCallbackCoordinatorTrackingImpl
extends SynchronizationCallbackCoordinatorNonTrackingImpl {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(SynchronizationCallbackCoordinatorTrackingImpl.class);
    private volatile long registrationThreadId;
    private volatile boolean delayedCompletionHandling;

    public SynchronizationCallbackCoordinatorTrackingImpl(SynchronizationCallbackTarget target) {
        super(target);
    }

    @Override
    public void reset() {
        super.reset();
        this.delayedCompletionHandling = false;
    }

    @Override
    public void afterCompletion(int status) {
        log.tracef("Synchronization coordinator: afterCompletion(status=%s)", status);
        if (JtaStatusHelper.isRollback(status)) {
            boolean isRegistrationThread;
            long currentThreadId = Thread.currentThread().getId();
            boolean bl = isRegistrationThread = currentThreadId == this.registrationThreadId;
            if (!isRegistrationThread) {
                this.delayedCompletionHandling = true;
                log.rollbackFromBackgroundThread(status);
                return;
            }
        }
        this.doAfterCompletion(JtaStatusHelper.isCommitted(status), false);
    }

    @Override
    public void synchronizationRegistered() {
        this.registrationThreadId = Thread.currentThread().getId();
    }

    @Override
    public void processAnyDelayedAfterCompletion() {
        if (this.delayedCompletionHandling) {
            this.delayedCompletionHandling = false;
            this.doAfterCompletion(false, true);
            throw new HibernateException("Transaction was rolled back in a different thread!");
        }
    }
}

