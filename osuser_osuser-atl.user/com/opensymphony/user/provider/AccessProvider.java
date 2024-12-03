/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider;

import com.opensymphony.user.provider.UserProvider;
import java.util.List;

public interface AccessProvider
extends UserProvider {
    public boolean addToGroup(String var1, String var2);

    public boolean inGroup(String var1, String var2);

    public List listGroupsContainingUser(String var1);

    public List listUsersInGroup(String var1);

    public boolean removeFromGroup(String var1, String var2);
}

