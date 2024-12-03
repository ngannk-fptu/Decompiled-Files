/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.module.NotificationMediumModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Set;

public interface NotificationMediumManager {
    public NotificationMedium getNotificationMedium(String var1);

    public NotificationMediumModuleDescriptor getNotificationMediumModuleDescriptor(String var1);

    public String getI18nizedMediumName(I18nResolver var1, String var2);

    public Set<NotificationMedium> getNotificationMediums();
}

