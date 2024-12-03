/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.efi.store;

import com.atlassian.confluence.user.ConfluenceUser;

public interface UserStorageService {
    public String get(String var1, ConfluenceUser var2);

    public void set(String var1, String var2, ConfluenceUser var3);

    public void remove(String var1, ConfluenceUser var2);
}

