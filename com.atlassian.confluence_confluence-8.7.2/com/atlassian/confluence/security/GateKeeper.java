/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.user.User;
import java.util.function.Predicate;

public interface GateKeeper {
    @Deprecated
    public void addKey(String var1, User var2);

    public void addKey(String var1, User var2, Predicate<User> var3);

    @Deprecated
    public void addKey(String var1, String var2);

    public void addKey(String var1, String var2, Predicate<User> var3);

    public boolean isAccessPermitted(String var1, User var2);

    public boolean isAccessPermitted(String var1, String var2);

    public void cleanAllKeys();

    @Deprecated
    public void allowAnonymousAccess(String var1);

    public void allowAnonymousAccess(String var1, Predicate<User> var2);
}

