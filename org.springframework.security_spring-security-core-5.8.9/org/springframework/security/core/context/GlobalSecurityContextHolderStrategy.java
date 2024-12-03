/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

final class GlobalSecurityContextHolderStrategy
implements SecurityContextHolderStrategy {
    private static SecurityContext contextHolder;

    GlobalSecurityContextHolderStrategy() {
    }

    @Override
    public void clearContext() {
        contextHolder = null;
    }

    @Override
    public SecurityContext getContext() {
        if (contextHolder == null) {
            contextHolder = new SecurityContextImpl();
        }
        return contextHolder;
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull((Object)context, (String)"Only non-null SecurityContext instances are permitted");
        contextHolder = context;
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}

