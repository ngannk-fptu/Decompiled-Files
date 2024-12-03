/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.usersettings.UserSettings
 *  com.atlassian.sal.api.usersettings.UserSettingsBuilder
 *  com.atlassian.sal.core.usersettings.DefaultUserSettings
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Sets
 *  com.opensymphony.module.propertyset.PropertySet
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.usersettings;

import com.atlassian.sal.api.usersettings.UserSettings;
import com.atlassian.sal.api.usersettings.UserSettingsBuilder;
import com.atlassian.sal.confluence.usersettings.ConfluenceUserSettingsService;
import com.atlassian.sal.core.usersettings.DefaultUserSettings;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.opensymphony.module.propertyset.PropertySet;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ConfluencePropertySetUserSettingsBuilder
implements UserSettingsBuilder {
    private static final Logger log = LoggerFactory.getLogger(ConfluencePropertySetUserSettingsBuilder.class);
    private final PropertySet propertySet;

    public ConfluencePropertySetUserSettingsBuilder(PropertySet propertySet) {
        this.propertySet = propertySet;
    }

    public UserSettingsBuilder put(String key, String value) {
        ConfluenceUserSettingsService.checkArgumentKey(key);
        ConfluenceUserSettingsService.checkArgumentValue(value);
        this.propertySet.setString("sal_" + key, value);
        return this;
    }

    public UserSettingsBuilder put(String key, boolean value) {
        ConfluenceUserSettingsService.checkArgumentKey(key);
        this.propertySet.setBoolean("sal_" + key, value);
        return this;
    }

    public UserSettingsBuilder put(String key, long value) {
        ConfluenceUserSettingsService.checkArgumentKey(key);
        this.propertySet.setLong("sal_" + key, value);
        return this;
    }

    public UserSettingsBuilder remove(String key) {
        ConfluenceUserSettingsService.checkArgumentKey(key);
        this.propertySet.remove("sal_" + key);
        return this;
    }

    public Option<Object> get(String key) {
        ConfluenceUserSettingsService.checkArgumentKey(key);
        int type = this.propertySet.getType("sal_" + key);
        switch (type) {
            case 3: {
                return Option.some((Object)this.propertySet.getLong("sal_" + key));
            }
            case 1: {
                return Option.some((Object)this.propertySet.getBoolean("sal_" + key));
            }
            case 5: {
                return Option.some((Object)this.propertySet.getString("sal_" + key));
            }
        }
        return Option.none();
    }

    public Set<String> getKeys() {
        HashSet keys = Sets.newHashSet();
        Collection propertySetKeys = this.propertySet.getKeys("sal_");
        for (Object propertySetKey : propertySetKeys) {
            keys.add(ConfluenceUserSettingsService.PrefixStrippingFunction.INSTANCE.apply(propertySetKey));
        }
        return keys;
    }

    public UserSettings build() {
        return ConfluencePropertySetUserSettingsBuilder.buildUserSettings(this.propertySet);
    }

    public static UserSettings buildUserSettings(PropertySet propertySet) {
        Collection propertySetKeys = propertySet.getKeys("sal_");
        UserSettingsBuilder settings = DefaultUserSettings.builder();
        Collection settingKeys = Collections2.transform((Collection)propertySetKeys, (Function)ConfluenceUserSettingsService.PrefixStrippingFunction.INSTANCE);
        block5: for (String settingKey : settingKeys) {
            int type = propertySet.getType("sal_" + settingKey);
            switch (type) {
                case 1: {
                    settings.put(settingKey, propertySet.getBoolean("sal_" + settingKey));
                    continue block5;
                }
                case 5: {
                    settings.put(settingKey, propertySet.getString("sal_" + settingKey));
                    continue block5;
                }
                case 3: {
                    settings.put(settingKey, propertySet.getLong("sal_" + settingKey));
                    continue block5;
                }
            }
            log.info("Property type '{}' for key {} is not supported by the SAL UserSettingsService", (Object)type, (Object)settingKey);
        }
        return settings.build();
    }
}

