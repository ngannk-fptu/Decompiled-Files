/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.sal.api.usersettings;

import io.atlassian.fugue.Option;
import java.util.Set;

public interface UserSettings {
    public Option<String> getString(String var1);

    public Option<Boolean> getBoolean(String var1);

    public Option<Long> getLong(String var1);

    public Set<String> getKeys();
}

