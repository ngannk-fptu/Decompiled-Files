/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.intercept;

import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.core.Authentication;

final class NullRunAsManager
implements RunAsManager {
    NullRunAsManager() {
    }

    @Override
    public Authentication buildRunAs(Authentication authentication, Object object, Collection<ConfigAttribute> config) {
        return null;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

