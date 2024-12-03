/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.user.User;
import java.util.List;

public interface UserDetailsManager {
    public String getStringProperty(User var1, String var2);

    public void setStringProperty(User var1, String var2, String var3);

    public void removeProperty(User var1, String var2);

    public List<String> getProfileKeys(String var1);

    public List<String> getProfileGroups();
}

