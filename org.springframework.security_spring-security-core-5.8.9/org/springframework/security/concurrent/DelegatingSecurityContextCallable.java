/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.concurrent;

import java.util.concurrent.Callable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public final class DelegatingSecurityContextCallable<V>
implements Callable<V> {
    private final Callable<V> delegate;
    private final boolean explicitSecurityContextProvided;
    private SecurityContext delegateSecurityContext;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private SecurityContext originalSecurityContext;

    public DelegatingSecurityContextCallable(Callable<V> delegate, SecurityContext securityContext) {
        this(delegate, securityContext, true);
    }

    public DelegatingSecurityContextCallable(Callable<V> delegate) {
        this(delegate, SecurityContextHolder.getContext(), false);
    }

    private DelegatingSecurityContextCallable(Callable<V> delegate, SecurityContext securityContext, boolean explicitSecurityContextProvided) {
        Assert.notNull(delegate, (String)"delegate cannot be null");
        Assert.notNull((Object)securityContext, (String)"securityContext cannot be null");
        this.delegate = delegate;
        this.delegateSecurityContext = securityContext;
        this.explicitSecurityContextProvided = explicitSecurityContextProvided;
    }

    @Override
    public V call() throws Exception {
        this.originalSecurityContext = this.securityContextHolderStrategy.getContext();
        try {
            this.securityContextHolderStrategy.setContext(this.delegateSecurityContext);
            V v = this.delegate.call();
            return v;
        }
        finally {
            SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
            if (emptyContext.equals(this.originalSecurityContext)) {
                this.securityContextHolderStrategy.clearContext();
            } else {
                this.securityContextHolderStrategy.setContext(this.originalSecurityContext);
            }
            this.originalSecurityContext = null;
        }
    }

    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
        if (!this.explicitSecurityContextProvided) {
            this.delegateSecurityContext = securityContextHolderStrategy.getContext();
        }
    }

    public String toString() {
        return this.delegate.toString();
    }

    public static <V> Callable<V> create(Callable<V> delegate, SecurityContext securityContext) {
        return securityContext != null ? new DelegatingSecurityContextCallable<V>(delegate, securityContext) : new DelegatingSecurityContextCallable<V>(delegate);
    }

    static <V> Callable<V> create(Callable<V> delegate, SecurityContext securityContext, SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(delegate, (String)"delegate cannot be null");
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        DelegatingSecurityContextCallable<V> callable = securityContext != null ? new DelegatingSecurityContextCallable<V>(delegate, securityContext) : new DelegatingSecurityContextCallable<V>(delegate);
        callable.setSecurityContextHolderStrategy(securityContextHolderStrategy);
        return callable;
    }
}

