/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.spi.UserRole;

public interface UserNotificationPreferences {
    public boolean isNotificationEnabled(ServerConfiguration var1, UserRole var2);

    public void setNotificationEnabled(ServerConfiguration var1, UserRole var2, boolean var3);

    public boolean isOwnEventNotificationsEnabled(ServerConfiguration var1);

    public void setOwnEventNotificationsEnabled(ServerConfiguration var1, boolean var2);

    public String getServerMapping(ServerConfiguration var1);

    public void setServerMapping(int var1, String var2);
}

