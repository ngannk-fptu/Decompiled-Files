/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.scope;

import com.atlassian.plugin.ScopeAware;

@Deprecated
public interface ScopeManager {
    public boolean isScopeActive(String var1);

    public static boolean isActive(ScopeManager scopeManager, ScopeAware scopeAware) {
        return scopeAware.getScopeKey().map(scopeManager::isScopeActive).orElse(true);
    }
}

