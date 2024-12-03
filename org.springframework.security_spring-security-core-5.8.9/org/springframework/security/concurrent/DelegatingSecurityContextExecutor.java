/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.concurrent;

import java.util.concurrent.Executor;
import org.springframework.security.concurrent.AbstractDelegatingSecurityContextSupport;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public class DelegatingSecurityContextExecutor
extends AbstractDelegatingSecurityContextSupport
implements Executor {
    private final Executor delegate;

    public DelegatingSecurityContextExecutor(Executor delegateExecutor, SecurityContext securityContext) {
        super(securityContext);
        Assert.notNull((Object)delegateExecutor, (String)"delegateExecutor cannot be null");
        this.delegate = delegateExecutor;
    }

    public DelegatingSecurityContextExecutor(Executor delegate) {
        this(delegate, null);
    }

    @Override
    public final void execute(Runnable task) {
        this.delegate.execute(this.wrap(task));
    }

    protected final Executor getDelegateExecutor() {
        return this.delegate;
    }

    @Override
    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        super.setSecurityContextHolderStrategy(securityContextHolderStrategy);
    }
}

