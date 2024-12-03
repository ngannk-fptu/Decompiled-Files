/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.data.repository.core.support;

import javax.annotation.Nullable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public enum SurroundingTransactionDetectorMethodInterceptor implements MethodInterceptor
{
    INSTANCE;

    private final ThreadLocal<Boolean> SURROUNDING_TX_ACTIVE = new ThreadLocal();

    public boolean isSurroundingTransactionActive() {
        return Boolean.TRUE == this.SURROUNDING_TX_ACTIVE.get();
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        this.SURROUNDING_TX_ACTIVE.set(TransactionSynchronizationManager.isActualTransactionActive());
        try {
            Object object = invocation.proceed();
            return object;
        }
        finally {
            this.SURROUNDING_TX_ACTIVE.remove();
        }
    }
}

