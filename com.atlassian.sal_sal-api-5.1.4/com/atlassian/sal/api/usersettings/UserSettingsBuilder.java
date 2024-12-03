/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.sal.api.usersettings;

import com.atlassian.sal.api.usersettings.UserSettings;
import io.atlassian.fugue.Option;
import java.util.Set;

public interface UserSettingsBuilder {
    public UserSettingsBuilder put(String var1, String var2);

    public UserSettingsBuilder put(String var1, boolean var2);

    public UserSettingsBuilder put(String var1, long var2);

    public UserSettingsBuilder remove(String var1);

    public Option<Object> get(String var1);

    public Set<String> getKeys();

    public UserSettings build();
}

