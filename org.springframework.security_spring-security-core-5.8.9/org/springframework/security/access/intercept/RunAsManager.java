/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

@Deprecated
public interface RunAsManager {
    public Authentication buildRunAs(Authentication var1, Object var2, Collection<ConfigAttribute> var3);

    public boolean supports(ConfigAttribute var1);

    public boolean supports(Class<?> var1);
}

