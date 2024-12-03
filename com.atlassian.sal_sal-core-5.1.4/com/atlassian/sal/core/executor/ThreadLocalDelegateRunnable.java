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

class ThreadLocalDelegateRunnable<C>
implements Runnable {
    private final C context;
    private final Runnable delegate;
    private final ThreadLocalContextManager<C> manager;
    private final ClassLoader contextClassLoader;

    ThreadLocalDelegateRunnable(ThreadLocalContextManager<C> manager, Runnable delegate) {
        this.delegate = (Runnable)Preconditions.checkNotNull((Object)delegate);
        this.manager = (ThreadLocalContextManager)Preconditions.checkNotNull(manager);
        this.context = manager.getThreadLocalContext();
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void run() {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.contextClassLoader);
            this.manager.setThreadLocalContext(this.context);
            this.delegate.run();
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
            this.manager.clearThreadLocalContext();
        }
    }
}

