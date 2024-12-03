/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsBuilder
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 */
package com.atlassian.sal.core.usersettings;

import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultUserSettings
implements UserSettings {
    private final Map<String, Object> settings;

    private DefaultUserSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    public Option<String> getString(String key) {
        DefaultUserSettings.checkKeyArgument(key);
        if (!this.settings.containsKey(key)) {
            return Option.none();
        }
        Object value = this.settings.get(key);
        return value instanceof String ? Option.some((Object)((String)value)) : Option.none();
    }

    public Option<Boolean> getBoolean(String key) {
        DefaultUserSettings.checkKeyArgument(key);
        if (!this.settings.containsKey(key)) {
            return Option.none();
        }
        Object value = this.settings.get(key);
        return value instanceof Boolean ? Option.some((Object)((Boolean)value)) : Option.none();
    }

    public Option<Long> getLong(String key) {
        DefaultUserSettings.checkKeyArgument(key);
        if (!this.settings.containsKey(key)) {
            return Option.none();
        }
        Object value = this.settings.get(key);
        return value instanceof Long ? Option.some((Object)((Long)value)) : Option.none();
    }

    public Set<String> getKeys() {
        return this.settings.keySet();
    }

    public static UserSettingsBuilder builder() {
        return new Builder();
    }

    public static UserSettingsBuilder builder(UserSettings userSettings) {
        return new Builder(userSettings);
    }

    private static void checkKeyArgument(String key) {
        Preconditions.checkArgument((key != null ? 1 : 0) != 0, (Object)"key cannot be null");
        Preconditions.checkArgument((key.length() <= UserSettingsService.MAX_KEY_LENGTH ? 1 : 0) != 0, (String)"key cannot be longer than %s characters", (int)UserSettingsService.MAX_KEY_LENGTH);
    }

    private static void checkValueArgument(String value) {
        Preconditions.checkArgument((value != null ? 1 : 0) != 0, (Object)"value cannot be null");
        Preconditions.checkArgument((value.length() <= 255 ? 1 : 0) != 0, (String)"value cannot be longer than %s characters", (int)255);
    }

    public static class Builder
    implements UserSettingsBuilder {
        private final Map<String, Object> settings = new HashMap<String, Object>();

        private Builder(UserSettings userSettings) {
            for (String key : userSettings.getKeys()) {
                for (Object value : userSettings.getBoolean(key)) {
                    this.settings.put(key, value);
                }
                for (Object value : userSettings.getString(key)) {
                    this.settings.put(key, value);
                }
                for (Object value : userSettings.getLong(key)) {
                    this.settings.put(key, value);
                }
            }
        }

        private Builder() {
        }

        public UserSettingsBuilder put(String key, String value) {
            DefaultUserSettings.checkKeyArgument(key);
            DefaultUserSettings.checkValueArgument(value);
            this.settings.put(key, value);
            return this;
        }

        public UserSettingsBuilder put(String key, boolean value) {
            DefaultUserSettings.checkKeyArgument(key);
            this.settings.put(key, value);
            return this;
        }

        public UserSettingsBuilder put(String key, long value) {
            DefaultUserSettings.checkKeyArgument(key);
            this.settings.put(key, value);
            return this;
        }

        public UserSettingsBuilder remove(String key) {
            DefaultUserSettings.checkKeyArgument(key);
            this.settings.remove(key);
            return this;
        }

        public Option<Object> get(String key) {
            DefaultUserSettings.checkKeyArgument(key);
            return this.settings.containsKey(key) ? Option.some((Object)this.settings.get(key)) : Option.none();
        }

        public Set<String> getKeys() {
            return this.settings.keySet();
        }

        public UserSettings build() {
            return new DefaultUserSettings(this.settings);
        }
    }
}

