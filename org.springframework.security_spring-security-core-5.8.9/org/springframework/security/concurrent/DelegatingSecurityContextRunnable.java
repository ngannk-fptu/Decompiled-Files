/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.concurrent;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public final class DelegatingSecurityContextRunnable
implements Runnable {
    private final Runnable delegate;
    private final boolean explicitSecurityContextProvided;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private SecurityContext delegateSecurityContext;
    private SecurityContext originalSecurityContext;

    public DelegatingSecurityContextRunnable(Runnable delegate, SecurityContext securityContext) {
        this(delegate, securityContext, true);
    }

    public DelegatingSecurityContextRunnable(Runnable delegate) {
        this(delegate, SecurityContextHolder.getContext(), false);
    }

    private DelegatingSecurityContextRunnable(Runnable delegate, SecurityContext securityContext, boolean explicitSecurityContextProvided) {
        Assert.notNull((Object)delegate, (String)"delegate cannot be null");
        Assert.notNull((Object)securityContext, (String)"securityContext cannot be null");
        this.delegate = delegate;
        this.delegateSecurityContext = securityContext;
        this.explicitSecurityContextProvided = explicitSecurityContextProvided;
    }

    @Override
    public void run() {
        this.originalSecurityContext = this.securityContextHolderStrategy.getContext();
        try {
            this.securityContextHolderStrategy.setContext(this.delegateSecurityContext);
            this.delegate.run();
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
            this.delegateSecurityContext = this.securityContextHolderStrategy.getContext();
        }
    }

    public String toString() {
        return this.delegate.toString();
    }

    public static Runnable create(Runnable delegate, SecurityContext securityContext) {
        Assert.notNull((Object)delegate, (String)"delegate cannot be  null");
        return securityContext != null ? new DelegatingSecurityContextRunnable(delegate, securityContext) : new DelegatingSecurityContextRunnable(delegate);
    }

    static Runnable create(Runnable delegate, SecurityContext securityContext, SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull((Object)delegate, (String)"delegate cannot be  null");
        Assert.notNull((Object)securityContextHolderStrategy, (String)"securityContextHolderStrategy cannot be null");
        DelegatingSecurityContextRunnable runnable = securityContext != null ? new DelegatingSecurityContextRunnable(delegate, securityContext) : new DelegatingSecurityContextRunnable(delegate);
        runnable.setSecurityContextHolderStrategy(securityContextHolderStrategy);
        return runnable;
    }
}

