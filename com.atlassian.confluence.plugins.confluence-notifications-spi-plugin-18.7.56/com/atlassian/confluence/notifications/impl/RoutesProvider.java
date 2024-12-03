/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.NotificationMedium
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.spi.UserRole;
import java.net.URI;

public interface RoutesProvider {
    public URI getStaticMediumPreference(NotificationMedium var1, UserRole var2);
}

