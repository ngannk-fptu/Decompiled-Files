/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.config;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DefaultServerConfiguration
implements ServerConfiguration {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String notificationMediumKey;
    @JsonProperty
    private final boolean enabledForAll;
    @JsonProperty
    private final String defaultUserIdTemplate;
    @JsonProperty
    private final String customTemplatePath;
    @JsonProperty
    private final Map<String, String> settings;
    @JsonProperty
    private final Set<String> groupsWithAccess;
    @JsonIgnore
    private final NotificationMediumManager notificationMediumManager;

    public DefaultServerConfiguration(int id, String name, String notificationMediumKey, boolean isEnabledForAll, String defaultUserIdTemplate, Map<String, String> settings, NotificationMediumManager notificationMediumManager, String customTemplatePath, Iterable<String> groupsWithAccess) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.notificationMediumKey = notificationMediumKey == null ? "" : notificationMediumKey;
        this.enabledForAll = isEnabledForAll;
        this.defaultUserIdTemplate = defaultUserIdTemplate == null ? "" : defaultUserIdTemplate;
        this.customTemplatePath = customTemplatePath == null ? "" : customTemplatePath;
        this.settings = settings != null ? ImmutableMap.copyOf(settings) : ImmutableMap.of();
        this.groupsWithAccess = groupsWithAccess != null ? ImmutableSet.copyOf(groupsWithAccess) : ImmutableSet.of();
        this.notificationMediumManager = notificationMediumManager;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getServerName() {
        return this.name;
    }

    @Override
    public String getFullName(I18nResolver i18n) {
        return this.name + " (" + this.notificationMediumManager.getI18nizedMediumName(i18n, this.notificationMediumKey) + ")";
    }

    @Override
    public NotificationMedium getNotificationMedium() {
        return this.notificationMediumManager.getNotificationMedium(this.notificationMediumKey);
    }

    @Override
    public String getProperty(String propertyKey) {
        return this.settings.get(propertyKey);
    }

    @Override
    public String getDefaultUserIDTemplate() {
        return this.defaultUserIdTemplate;
    }

    @Override
    public boolean isEnabledForAllUsers() {
        return this.enabledForAll;
    }

    @Override
    public String getCustomTemplatePath() {
        return this.customTemplatePath;
    }

    @Override
    public Iterable<String> getGroupsWithAccess() {
        return this.groupsWithAccess;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultServerConfiguration that = (DefaultServerConfiguration)o;
        if (this.enabledForAll != that.enabledForAll) {
            return false;
        }
        if (this.id != that.id) {
            return false;
        }
        if (!this.customTemplatePath.equals(that.customTemplatePath)) {
            return false;
        }
        if (!this.defaultUserIdTemplate.equals(that.defaultUserIdTemplate)) {
            return false;
        }
        if (!this.groupsWithAccess.equals(that.groupsWithAccess)) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.notificationMediumKey.equals(that.notificationMediumKey)) {
            return false;
        }
        return this.settings.equals(that.settings);
    }

    public int hashCode() {
        int result = this.id;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.notificationMediumKey.hashCode();
        result = 31 * result + (this.enabledForAll ? 1 : 0);
        result = 31 * result + this.defaultUserIdTemplate.hashCode();
        result = 31 * result + this.customTemplatePath.hashCode();
        result = 31 * result + this.settings.hashCode();
        result = 31 * result + this.groupsWithAccess.hashCode();
        return result;
    }
}

