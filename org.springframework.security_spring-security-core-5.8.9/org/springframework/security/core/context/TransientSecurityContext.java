/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.core.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.Transient;
import org.springframework.security.core.context.SecurityContextImpl;

@Transient
public class TransientSecurityContext
extends SecurityContextImpl {
    public TransientSecurityContext() {
    }

    public TransientSecurityContext(Authentication authentication) {
        super(authentication);
    }
}

