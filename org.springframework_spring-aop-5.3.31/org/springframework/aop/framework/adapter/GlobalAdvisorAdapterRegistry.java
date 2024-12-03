/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry;

public final class GlobalAdvisorAdapterRegistry {
    private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

    private GlobalAdvisorAdapterRegistry() {
    }

    public static AdvisorAdapterRegistry getInstance() {
        return instance;
    }

    static void reset() {
        instance = new DefaultAdvisorAdapterRegistry();
    }
}

