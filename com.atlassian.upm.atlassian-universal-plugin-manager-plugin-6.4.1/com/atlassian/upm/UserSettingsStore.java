/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.UserSettings;

public interface UserSettingsStore {
    public boolean getBoolean(UserKey var1, UserSettings var2);

    public void setBoolean(UserKey var1, UserSettings var2, boolean var3);
}

