/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 */
package com.atlassian.confluence.extra.calendar3.watchdog.impl.cleaner.callables;

import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import java.util.concurrent.Callable;

class TransactionalCallable<T>
implements Callable<T> {
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final Callable<T> innerCallable;

    public TransactionalCallable(TransactionalHostContextAccessor hostContextAccessor, Callable<T> innerCallable) {
        this.hostContextAccessor = hostContextAccessor;
        this.innerCallable = innerCallable;
    }

    @Override
    public T call() throws Exception {
        Object result = this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> {
            try {
                return this.innerCallable.call();
            }
            catch (Exception e) {
                throw new RuntimeException("Rolled back transaction because of", e);
            }
        });
        return (T)result;
    }
}

