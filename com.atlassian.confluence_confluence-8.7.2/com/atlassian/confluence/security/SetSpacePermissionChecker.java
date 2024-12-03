/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.user.User;

public interface SetSpacePermissionChecker {
    public boolean canSetPermission(User var1, SpacePermission var2);
}

