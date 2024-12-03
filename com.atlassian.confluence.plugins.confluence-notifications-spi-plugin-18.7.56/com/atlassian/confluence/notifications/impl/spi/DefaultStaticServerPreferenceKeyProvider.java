/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.impl.spi.StaticServerPreferenceKeyProvider;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.google.common.base.Preconditions;

public class DefaultStaticServerPreferenceKeyProvider
implements StaticServerPreferenceKeyProvider {
    private static final String NOTIFICATION_PREFERENCE_PREFIX = "notifications.conf.static.user.pref.%s.%s";

    @Override
    public String getPreferenceKey(ServerConfiguration server, UserRole role) {
        Preconditions.checkArgument((!server.isConfigurable() ? 1 : 0) != 0, (String)"%s : Should only be used for static servers", (Object)this.getClass().getName());
        return String.format(NOTIFICATION_PREFERENCE_PREFIX, server.getNotificationMedium().getKey(), role.getID());
    }
}

