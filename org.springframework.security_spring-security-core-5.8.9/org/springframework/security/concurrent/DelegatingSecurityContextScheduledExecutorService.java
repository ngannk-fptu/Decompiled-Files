/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;

public final class DelegatingSecurityContextScheduledExecutorService
extends DelegatingSecurityContextExecutorService
implements ScheduledExecutorService {
    public DelegatingSecurityContextScheduledExecutorService(ScheduledExecutorService delegateScheduledExecutorService, SecurityContext securityContext) {
        super(delegateScheduledExecutorService, securityContext);
    }

    public DelegatingSecurityContextScheduledExecutorService(ScheduledExecutorService delegate) {
        this(delegate, null);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.getDelegate().schedule(this.wrap(command), delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.getDelegate().schedule(this.wrap(callable), delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.getDelegate().scheduleAtFixedRate(this.wrap(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.getDelegate().scheduleWithFixedDelay(this.wrap(command), initialDelay, delay, unit);
    }

    private ScheduledExecutorService getDelegate() {
        return (ScheduledExecutorService)this.getDelegateExecutor();
    }
}

