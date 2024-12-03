/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access;

import java.util.Collection;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

@Deprecated
public interface AccessDecisionManager {
    public void decide(Authentication var1, Object var2, Collection<ConfigAttribute> var3) throws AccessDeniedException, InsufficientAuthenticationException;

    public boolean supports(ConfigAttribute var1);

    public boolean supports(Class<?> var1);
}

