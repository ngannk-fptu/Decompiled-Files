/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.context;

import java.util.function.Supplier;
import org.springframework.security.core.context.SecurityContext;

public interface DeferredSecurityContext
extends Supplier<SecurityContext> {
    public boolean isGenerated();
}

