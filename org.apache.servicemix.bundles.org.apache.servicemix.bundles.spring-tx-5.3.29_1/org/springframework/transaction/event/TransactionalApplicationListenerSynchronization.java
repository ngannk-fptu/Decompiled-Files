/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 */
package org.springframework.transaction.event;

import java.util.List;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalApplicationListener;
import org.springframework.transaction.support.TransactionSynchronization;

class TransactionalApplicationListenerSynchronization<E extends ApplicationEvent>
implements TransactionSynchronization {
    private final E event;
    private final TransactionalApplicationListener<E> listener;
    private final List<TransactionalApplicationListener.SynchronizationCallback> callbacks;

    public TransactionalApplicationListenerSynchronization(E event, TransactionalApplicationListener<E> listener, List<TransactionalApplicationListener.SynchronizationCallback> callbacks) {
        this.event = event;
        this.listener = listener;
        this.callbacks = callbacks;
    }

    @Override
    public int getOrder() {
        return this.listener.getOrder();
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (this.listener.getTransactionPhase() == TransactionPhase.BEFORE_COMMIT) {
            this.processEventWithCallbacks();
        }
    }

    @Override
    public void afterCompletion(int status) {
        TransactionPhase phase = this.listener.getTransactionPhase();
        if (phase == TransactionPhase.AFTER_COMMIT && status == 0) {
            this.processEventWithCallbacks();
        } else if (phase == TransactionPhase.AFTER_ROLLBACK && status == 1) {
            this.processEventWithCallbacks();
        } else if (phase == TransactionPhase.AFTER_COMPLETION) {
            this.processEventWithCallbacks();
        }
    }

    private void processEventWithCallbacks() {
        this.callbacks.forEach(callback -> callback.preProcessEvent((ApplicationEvent)this.event));
        try {
            this.listener.processEvent(this.event);
        }
        catch (Error | RuntimeException ex) {
            this.callbacks.forEach(callback -> callback.postProcessEvent((ApplicationEvent)this.event, ex));
            throw ex;
        }
        this.callbacks.forEach(callback -> callback.postProcessEvent((ApplicationEvent)this.event, null));
    }
}

