/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access;

import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

@Deprecated
public interface AccessDecisionVoter<S> {
    public static final int ACCESS_GRANTED = 1;
    public static final int ACCESS_ABSTAIN = 0;
    public static final int ACCESS_DENIED = -1;

    public boolean supports(ConfigAttribute var1);

    public boolean supports(Class<?> var1);

    public int vote(Authentication var1, S var2, Collection<ConfigAttribute> var3);
}

