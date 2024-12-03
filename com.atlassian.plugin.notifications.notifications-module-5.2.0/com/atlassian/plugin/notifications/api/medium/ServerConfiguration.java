/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.api.medium;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.sal.api.message.I18nResolver;

public interface ServerConfiguration {
    public static final String SERVER_NAME_PARAM = "server-name";
    public static final String DEFAULT_USER_ID_TEMPLATE_PARAM = "template.user.id";
    public static final int UNKNOWN_ID = -1;

    public int getId();

    public NotificationMedium getNotificationMedium();

    public String getServerName();

    public String getProperty(String var1);

    public boolean isEnabledForAllUsers();

    public String getDefaultUserIDTemplate();

    public String getFullName(I18nResolver var1);

    public String getCustomTemplatePath();

    public Iterable<String> getGroupsWithAccess();

    public boolean isConfigurable();
}

