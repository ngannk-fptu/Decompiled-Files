/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;

class ThreadLocalDelegateCallable<C, T>
implements Callable<T> {
    private final Callable<T> delegate;
    private final ThreadLocalContextManager<C> manager;
    private final C context;
    private final ClassLoader contextClassLoader;

    ThreadLocalDelegateCallable(ThreadLocalContextManager<C> manager, Callable<T> delegate) {
        this.delegate = (Callable)Preconditions.checkNotNull(delegate);
        this.manager = (ThreadLocalContextManager)Preconditions.checkNotNull(manager);
        this.context = manager.getThreadLocalContext();
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public T call() throws Exception {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.contextClassLoader);
            this.manager.setThreadLocalContext(this.context);
            T t = this.delegate.call();
            return t;
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
            this.manager.clearThreadLocalContext();
        }
    }
}

