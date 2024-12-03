/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.userlister;

import java.util.Set;

public interface UserListManager {
    public static final String BANDANA_KEY_BLACK_LIST = "com.atlassian.confluence.extra.userlister.blacklist";

    public Set<String> getGroupBlackList();

    public void saveGroupBlackList(Set<String> var1);

    public boolean isGroupPermitted(String var1);

    public Set<String> getLoggedInUsers();

    public void registerLoggedInUser(String var1, String var2);

    public void unregisterLoggedInUser(String var1, String var2);
}

