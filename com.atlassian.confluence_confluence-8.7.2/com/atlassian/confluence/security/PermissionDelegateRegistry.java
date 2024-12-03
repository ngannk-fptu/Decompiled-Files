/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.PermissionDelegate;

public interface PermissionDelegateRegistry {
    public void register(String var1, PermissionDelegate<?> var2);
}

