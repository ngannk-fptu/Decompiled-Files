/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.scope.ScopeManager
 */
package com.atlassian.plugin.scope;

import com.atlassian.plugin.scope.ScopeManager;

@Deprecated
public class AlwaysEnabledScopeManager
implements ScopeManager {
    public boolean isScopeActive(String scopeKey) {
        return true;
    }
}

