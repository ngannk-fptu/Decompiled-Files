/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.NotificationMedium
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.impl.RoutesProvider;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.spi.UserRole;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public class DefaultRoutesProvider
implements RoutesProvider {
    @Override
    public URI getStaticMediumPreference(NotificationMedium medium, UserRole userRole) {
        return UriBuilder.fromPath((String)"/rest/notifications/confluence/latest/user/notifications/static/{mediumKey}/{role}").build(new Object[]{medium.getKey(), userRole.getID()});
    }
}

