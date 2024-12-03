/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.context;

import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContext;

public interface SecurityContextHolderStrategy {
    public void clearContext();

    public SecurityContext getContext();

    default public Supplier<SecurityContext> getDeferredContext() {
        return () -> this.getContext();
    }

    public void setContext(SecurityContext var1);

    default public void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        this.setContext(deferredContext.get());
    }

    public SecurityContext createEmptyContext();
}

