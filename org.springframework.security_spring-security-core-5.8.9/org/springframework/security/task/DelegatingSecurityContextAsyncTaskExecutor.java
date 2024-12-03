/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 */
package org.springframework.security.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;

public class DelegatingSecurityContextAsyncTaskExecutor
extends DelegatingSecurityContextTaskExecutor
implements AsyncTaskExecutor {
    public DelegatingSecurityContextAsyncTaskExecutor(AsyncTaskExecutor delegateAsyncTaskExecutor, SecurityContext securityContext) {
        super((TaskExecutor)delegateAsyncTaskExecutor, securityContext);
    }

    public DelegatingSecurityContextAsyncTaskExecutor(AsyncTaskExecutor delegateAsyncTaskExecutor) {
        this(delegateAsyncTaskExecutor, null);
    }

    public final void execute(Runnable task, long startTimeout) {
        this.getDelegate().execute(this.wrap(task), startTimeout);
    }

    public final Future<?> submit(Runnable task) {
        return this.getDelegate().submit(this.wrap(task));
    }

    public final <T> Future<T> submit(Callable<T> task) {
        return this.getDelegate().submit(this.wrap(task));
    }

    private AsyncTaskExecutor getDelegate() {
        return (AsyncTaskExecutor)this.getDelegateExecutor();
    }
}

