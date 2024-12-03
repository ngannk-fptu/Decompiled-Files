/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 */
package com.benryan.components;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;

public interface TemporaryAuthTokenManager {
    public ConfluenceUser getUser(String var1) throws EntityException;

    public String createToken(User var1);

    @Deprecated
    public void cleanExpiredTokens();
}

