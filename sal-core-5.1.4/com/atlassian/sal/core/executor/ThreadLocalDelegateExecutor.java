/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.core.executor;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

class ThreadLocalDelegateExecutor
implements Executor {
    private final Executor delegate;
    private final ThreadLocalDelegateExecutorFactory delegateExecutorFactory;

    ThreadLocalDelegateExecutor(Executor delegate, ThreadLocalDelegateExecutorFactory delegateExecutorFactory) {
        this.delegateExecutorFactory = (ThreadLocalDelegateExecutorFactory)Preconditions.checkNotNull((Object)delegateExecutorFactory);
        this.delegate = (Executor)Preconditions.checkNotNull((Object)delegate);
    }

    @Override
    public void execute(Runnable runnable) {
        Runnable wrapper = this.delegateExecutorFactory.createRunnable(runnable);
        this.delegate.execute(wrapper);
    }
}

