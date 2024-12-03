/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.PayloadApplicationEvent
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.event;

import java.util.function.Consumer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalApplicationListenerAdapter;

public interface TransactionalApplicationListener<E extends ApplicationEvent>
extends ApplicationListener<E>,
Ordered {
    default public int getOrder() {
        return Integer.MAX_VALUE;
    }

    default public String getListenerId() {
        return "";
    }

    default public TransactionPhase getTransactionPhase() {
        return TransactionPhase.AFTER_COMMIT;
    }

    public void addCallback(SynchronizationCallback var1);

    public void processEvent(E var1);

    public static <T> TransactionalApplicationListener<PayloadApplicationEvent<T>> forPayload(Consumer<T> consumer) {
        return TransactionalApplicationListener.forPayload(TransactionPhase.AFTER_COMMIT, consumer);
    }

    public static <T> TransactionalApplicationListener<PayloadApplicationEvent<T>> forPayload(TransactionPhase phase, Consumer<T> consumer) {
        TransactionalApplicationListenerAdapter<PayloadApplicationEvent<T>> listener = new TransactionalApplicationListenerAdapter<PayloadApplicationEvent<T>>(event -> consumer.accept(event.getPayload()));
        listener.setTransactionPhase(phase);
        return listener;
    }

    public static interface SynchronizationCallback {
        default public void preProcessEvent(ApplicationEvent event) {
        }

        default public void postProcessEvent(ApplicationEvent event, @Nullable Throwable ex) {
        }
    }
}

