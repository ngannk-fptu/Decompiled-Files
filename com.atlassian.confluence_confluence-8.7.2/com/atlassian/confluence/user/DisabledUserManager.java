/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.UserNotFoundException;

public interface DisabledUserManager {
    public boolean isDisabled(User var1);

    public boolean isDisabled(com.atlassian.user.User var1);

    public boolean isDisabled(String var1);

    public void disableUser(User var1) throws UserNotFoundException;

    public void enableUser(User var1) throws UserNotFoundException;
}

