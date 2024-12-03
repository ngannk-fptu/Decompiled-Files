/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import java.security.ProtectionDomain;
import org.springframework.lang.Nullable;

public interface SmartClassLoader {
    default public boolean isClassReloadable(Class<?> clazz) {
        return false;
    }

    default public ClassLoader getOriginalClassLoader() {
        return (ClassLoader)((Object)this);
    }

    default public Class<?> publicDefineClass(String name, byte[] b, @Nullable ProtectionDomain protectionDomain) {
        throw new UnsupportedOperationException();
    }
}

