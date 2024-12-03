/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.internal.AsynchronousAbleEventDispatcher
 *  com.atlassian.event.spi.EventExecutorFactory
 *  com.atlassian.event.spi.ListenerInvoker
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.crowd.core.event;

import com.atlassian.event.internal.AsynchronousAbleEventDispatcher;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.event.spi.ListenerInvoker;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionAwareEventDispatcher
extends AsynchronousAbleEventDispatcher {
    public static final String DISABLE_TRANSACTION = "DISABLE_TRANSACTION";
    private final TransactionTemplate transactionTemplate;

    public TransactionAwareEventDispatcher(EventExecutorFactory executorFactory, PlatformTransactionManager transactionManager) {
        super(executorFactory);
        this.transactionTemplate = new TransactionTemplate(transactionManager, (TransactionDefinition)new DefaultTransactionDefinition(3));
    }

    public void dispatch(ListenerInvoker invoker, Object event) {
        if (invoker.getScope().map(DISABLE_TRANSACTION::equals).orElse(false).booleanValue()) {
            super.dispatch(invoker, event);
            return;
        }
        this.transactionTemplate.execute(status -> {
            super.dispatch(invoker, event);
            return null;
        });
    }
}

