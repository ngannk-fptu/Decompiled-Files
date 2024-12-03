/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Function
 */
package com.atlassian.sal.api.usersettings;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.google.common.base.Function;

@PublicApi
public interface UserSettingsService {
    public static final String USER_SETTINGS_PREFIX = "sal_";
    public static final int MAX_STRING_VALUE_LENGTH = 255;
    public static final int MAX_KEY_LENGTH = 200 - "sal_".length();

    @Deprecated
    public UserSettings getUserSettings(String var1);

    public UserSettings getUserSettings(UserKey var1);

    @Deprecated
    public void updateUserSettings(String var1, Function<UserSettingsBuilder, UserSettings> var2);

    @Deprecated
    public void updateUserSettings(UserKey var1, Function<UserSettingsBuilder, UserSettings> var2);

    public void updateUserSettings(UserKey var1, java.util.function.Function<UserSettingsBuilder, UserSettings> var2);
}

