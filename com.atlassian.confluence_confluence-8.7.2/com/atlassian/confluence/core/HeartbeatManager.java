/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.core;

import com.atlassian.user.User;
import java.util.List;

public interface HeartbeatManager {
    public long getHeartbeatInterval();

    public List<User> getUsersForActivity(String var1);

    public void startActivity(String var1, String var2);

    public void startActivity(String var1, User var2);

    public void stopActivity(String var1, User var2);

    public void stopActivity(String var1, String var2);
}

