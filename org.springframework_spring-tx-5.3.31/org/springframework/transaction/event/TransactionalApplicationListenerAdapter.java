/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.core.Ordered
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalApplicationListener;
import org.springframework.transaction.event.TransactionalApplicationListenerSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class TransactionalApplicationListenerAdapter<E extends ApplicationEvent>
implements TransactionalApplicationListener<E>,
Ordered {
    private final ApplicationListener<E> targetListener;
    private int order = Integer.MAX_VALUE;
    private TransactionPhase transactionPhase = TransactionPhase.AFTER_COMMIT;
    private String listenerId = "";
    private final List<TransactionalApplicationListener.SynchronizationCallback> callbacks = new CopyOnWriteArrayList<TransactionalApplicationListener.SynchronizationCallback>();

    public TransactionalApplicationListenerAdapter(ApplicationListener<E> targetListener) {
        this.targetListener = targetListener;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setTransactionPhase(TransactionPhase transactionPhase) {
        this.transactionPhase = transactionPhase;
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return this.transactionPhase;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }

    @Override
    public String getListenerId() {
        return this.listenerId;
    }

    @Override
    public void addCallback(TransactionalApplicationListener.SynchronizationCallback callback) {
        Assert.notNull((Object)callback, (String)"SynchronizationCallback must not be null");
        this.callbacks.add(callback);
    }

    @Override
    public void processEvent(E event) {
        this.targetListener.onApplicationEvent(event);
    }

    public void onApplicationEvent(E event) {
        if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionalApplicationListenerSynchronization<E>(event, this, this.callbacks));
        }
    }
}

