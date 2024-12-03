/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Collections;
import java.util.Map;

public final class ParameterizedServerConfiguration
implements ServerConfiguration {
    private final Map<String, String> properties;
    private final NotificationMedium notificationMedium;

    public ParameterizedServerConfiguration(Map<String, String> properties, NotificationMedium notificationMedium) {
        this.properties = properties;
        this.notificationMedium = notificationMedium;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public NotificationMedium getNotificationMedium() {
        return this.notificationMedium;
    }

    @Override
    public String getServerName() {
        return "Parameterized Server";
    }

    @Override
    public String getDefaultUserIDTemplate() {
        return "{userName}";
    }

    @Override
    public String getFullName(I18nResolver i18n) {
        return "Parameterized Server";
    }

    @Override
    public String getCustomTemplatePath() {
        return null;
    }

    @Override
    public Iterable<String> getGroupsWithAccess() {
        return Collections.emptySet();
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public String getProperty(String propertyKey) {
        return this.properties.get(propertyKey);
    }

    @Override
    public boolean isEnabledForAllUsers() {
        return false;
    }
}

