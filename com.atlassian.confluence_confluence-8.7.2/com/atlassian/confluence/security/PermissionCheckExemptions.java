/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.user.User;

public interface PermissionCheckExemptions {
    public boolean isExempt(User var1);
}

