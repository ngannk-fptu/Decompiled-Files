/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public final class ProxyCreationContext {
    private static final ThreadLocal<String> currentProxiedBeanName = new NamedThreadLocal("Name of currently proxied bean");

    private ProxyCreationContext() {
    }

    @Nullable
    public static String getCurrentProxiedBeanName() {
        return currentProxiedBeanName.get();
    }

    static void setCurrentProxiedBeanName(@Nullable String beanName) {
        if (beanName != null) {
            currentProxiedBeanName.set(beanName);
        } else {
            currentProxiedBeanName.remove();
        }
    }
}

