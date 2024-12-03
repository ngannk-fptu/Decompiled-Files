/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.context;

import org.springframework.security.core.context.SecurityContextChangedEvent;

@FunctionalInterface
public interface SecurityContextChangedListener {
    public void securityContextChanged(SecurityContextChangedEvent var1);
}

