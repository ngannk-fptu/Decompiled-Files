/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.user.User;

public interface UserAware {
    public User getUser();

    public boolean isUserRequired();

    public boolean isViewPermissionRequired();
}

