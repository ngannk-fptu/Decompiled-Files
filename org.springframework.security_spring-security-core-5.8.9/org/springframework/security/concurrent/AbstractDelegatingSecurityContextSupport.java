/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.concurrent;

import java.util.concurrent.Callable;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

abstract class AbstractDelegatingSecurityContextSupport {
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final SecurityContext securityContext;

    AbstractDelegatingSecurityContextSupport(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    protected final Runnable wrap(Runnable delegate) {
        return DelegatingSecurityContextRunnable.create(delegate, this.securityContext, this.securityContextHolderStrategy);
    }

    protected final <T> Callable<T> wrap(Callable<T> delegate) {
        return DelegatingSecurityContextCallable.create(delegate, this.securityContext, this.securityContextHolderStrategy);
    }
}

