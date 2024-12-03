/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.createcontent.services;

import com.atlassian.user.User;

public interface UserStorageService {
    public boolean isKeyStoredForCurrentUser(String var1);

    public void storeKeyForCurrentUser(String var1);

    public void removeKeyForUser(String var1, User var2);
}

