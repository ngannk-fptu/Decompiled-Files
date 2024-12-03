/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.Trigger
 *  org.springframework.util.Assert
 */
package org.springframework.security.scheduling;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;

public class DelegatingSecurityContextTaskScheduler
implements TaskScheduler {
    private final TaskScheduler delegate;
    private final SecurityContext securityContext;

    public DelegatingSecurityContextTaskScheduler(TaskScheduler delegateTaskScheduler, SecurityContext securityContext) {
        Assert.notNull((Object)delegateTaskScheduler, (String)"delegateTaskScheduler cannot be null");
        this.delegate = delegateTaskScheduler;
        this.securityContext = securityContext;
    }

    public DelegatingSecurityContextTaskScheduler(TaskScheduler delegate) {
        this(delegate, null);
    }

    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return this.delegate.schedule(this.wrap(task), trigger);
    }

    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return this.delegate.schedule(this.wrap(task), startTime);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return this.delegate.scheduleAtFixedRate(this.wrap(task), startTime, period);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return this.delegate.scheduleAtFixedRate(this.wrap(task), period);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return this.delegate.scheduleWithFixedDelay(this.wrap(task), startTime, delay);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return this.delegate.scheduleWithFixedDelay(this.wrap(task), delay);
    }

    private Runnable wrap(Runnable delegate) {
        return DelegatingSecurityContextRunnable.create(delegate, this.securityContext);
    }
}

