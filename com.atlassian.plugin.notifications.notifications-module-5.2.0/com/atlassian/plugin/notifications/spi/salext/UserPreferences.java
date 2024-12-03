/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.spi.salext;

import com.atlassian.sal.api.user.UserKey;

public interface UserPreferences {
    public UserKey getUserKey();

    public String getString(String var1);

    public long getLong(String var1);

    public boolean getBoolean(String var1);

    public void setString(String var1, String var2) throws RuntimeException;

    public void setLong(String var1, long var2) throws RuntimeException;

    public void setBoolean(String var1, boolean var2) throws RuntimeException;

    public void remove(String var1) throws RuntimeException;
}

