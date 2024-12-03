/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.DeletedUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.user.User;

public class UserHelper {
    public boolean isUnknownUser(User user) {
        return UnknownUser.isUnknownUser(user);
    }

    public boolean isDeletedUser(User user) {
        return DeletedUser.isDeletedUser(user);
    }
}

