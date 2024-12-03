/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.scheduling.SchedulingTaskExecutor
 */
package org.springframework.security.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

public class DelegatingSecurityContextSchedulingTaskExecutor
extends DelegatingSecurityContextAsyncTaskExecutor
implements SchedulingTaskExecutor {
    public DelegatingSecurityContextSchedulingTaskExecutor(SchedulingTaskExecutor delegateSchedulingTaskExecutor, SecurityContext securityContext) {
        super((AsyncTaskExecutor)delegateSchedulingTaskExecutor, securityContext);
    }

    public DelegatingSecurityContextSchedulingTaskExecutor(SchedulingTaskExecutor delegateAsyncTaskExecutor) {
        this(delegateAsyncTaskExecutor, null);
    }

    public boolean prefersShortLivedTasks() {
        return this.getDelegate().prefersShortLivedTasks();
    }

    private SchedulingTaskExecutor getDelegate() {
        return (SchedulingTaskExecutor)this.getDelegateExecutor();
    }
}

