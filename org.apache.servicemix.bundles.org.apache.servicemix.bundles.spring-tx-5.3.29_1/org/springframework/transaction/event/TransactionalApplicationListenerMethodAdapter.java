/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.event.ApplicationListenerMethodAdapter
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.event;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalApplicationListener;
import org.springframework.transaction.event.TransactionalApplicationListenerSynchronization;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class TransactionalApplicationListenerMethodAdapter
extends ApplicationListenerMethodAdapter
implements TransactionalApplicationListener<ApplicationEvent> {
    private final TransactionalEventListener annotation;
    private final TransactionPhase transactionPhase;
    private final List<TransactionalApplicationListener.SynchronizationCallback> callbacks = new CopyOnWriteArrayList<TransactionalApplicationListener.SynchronizationCallback>();

    public TransactionalApplicationListenerMethodAdapter(String beanName, Class<?> targetClass, Method method) {
        super(beanName, targetClass, method);
        TransactionalEventListener ann = (TransactionalEventListener)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, TransactionalEventListener.class);
        if (ann == null) {
            throw new IllegalStateException("No TransactionalEventListener annotation found on method: " + method);
        }
        this.annotation = ann;
        this.transactionPhase = ann.phase();
    }

    @Override
    public TransactionPhase getTransactionPhase() {
        return this.transactionPhase;
    }

    @Override
    public void addCallback(TransactionalApplicationListener.SynchronizationCallback callback) {
        Assert.notNull((Object)callback, (String)"SynchronizationCallback must not be null");
        this.callbacks.add(callback);
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionalApplicationListenerSynchronization<ApplicationEvent>(event, this, this.callbacks));
        } else if (this.annotation.fallbackExecution()) {
            if (this.annotation.phase() == TransactionPhase.AFTER_ROLLBACK && this.logger.isWarnEnabled()) {
                this.logger.warn((Object)("Processing " + event + " as a fallback execution on AFTER_ROLLBACK phase"));
            }
            this.processEvent(event);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("No transaction is active - skipping " + event));
        }
    }
}

