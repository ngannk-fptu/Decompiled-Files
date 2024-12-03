/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsBuilder
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.opensymphony.module.propertyset.PropertySet
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.usersettings;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.atlassian.sal.confluence.usersettings.ConfluencePropertySetUserSettingsBuilder;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.opensymphony.module.propertyset.PropertySet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceUserSettingsService
implements UserSettingsService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceUserSettingsService.class);
    private final UserAccessor userAccessor;

    public ConfluenceUserSettingsService(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public UserSettings getUserSettings(String userName) {
        UserSettingsBuilder builder = this.getBuilder(userName);
        return builder.build();
    }

    public @Nullable UserSettings getUserSettings(UserKey userKey) {
        if (userKey == null) {
            return null;
        }
        UserSettingsBuilder builder = this.getBuilder(userKey);
        return builder.build();
    }

    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"})
    public void updateUserSettings(String userName, Function<UserSettingsBuilder, UserSettings> updateFunction) {
        UserSettingsBuilder builder = this.getBuilder(userName);
        UserSettings unused = (UserSettings)updateFunction.apply((Object)builder);
    }

    public void updateUserSettings(UserKey userKey, Function<UserSettingsBuilder, UserSettings> guavaUpdateFunction) {
        Function<UserSettingsBuilder, UserSettings> updateFunction = guavaUpdateFunction;
        this.updateUserSettings(userKey, (java.util.function.Function<UserSettingsBuilder, UserSettings>)updateFunction);
    }

    public void updateUserSettings(UserKey userKey, java.util.function.Function<UserSettingsBuilder, UserSettings> updateFunction) {
        if (userKey == null) {
            log.warn("UserKey was null, therefore did not update UserSettings");
            return;
        }
        UserSettingsBuilder builder = this.getBuilder(userKey);
        updateFunction.apply(builder);
    }

    private UserSettingsBuilder getBuilder(String userName) {
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        Preconditions.checkArgument((user != null ? 1 : 0) != 0, (String)"No user exists with the username %s", (Object)userName);
        return this.getBuilder(user);
    }

    private UserSettingsBuilder getBuilder(UserKey userKey) {
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        Preconditions.checkArgument((user != null ? 1 : 0) != 0, (String)"No user exists with the user key %s", (Object)userKey);
        return this.getBuilder(user);
    }

    private UserSettingsBuilder getBuilder(ConfluenceUser user) {
        PropertySet propertySet = this.userAccessor.getPropertySet(user);
        return new ConfluencePropertySetUserSettingsBuilder(propertySet);
    }

    public static void checkArgumentKey(@Nullable String key) {
        Preconditions.checkArgument((key != null ? 1 : 0) != 0, (Object)"key cannot be null");
        Preconditions.checkArgument((key.length() <= MAX_KEY_LENGTH ? 1 : 0) != 0, (String)"key cannot be longer than %s characters", (int)MAX_KEY_LENGTH);
    }

    public static void checkArgumentValue(@Nullable String value) {
        Preconditions.checkArgument((value != null ? 1 : 0) != 0, (Object)"value cannot be null");
        Preconditions.checkArgument((value.length() <= 255 ? 1 : 0) != 0, (String)"value cannot be longer than %s characters", (int)255);
    }

    public static enum PrefixStrippingFunction implements Function<Object, String>
    {
        INSTANCE;


        public String apply(Object input) {
            String val = input.toString();
            return val.substring("sal_".length());
        }
    }
}

