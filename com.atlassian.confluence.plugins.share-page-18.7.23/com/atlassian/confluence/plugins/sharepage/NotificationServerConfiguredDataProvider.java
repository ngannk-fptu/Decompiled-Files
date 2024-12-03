/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableBoolean
 *  com.atlassian.plugin.notifications.api.medium.ServerManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableBoolean;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationServerConfiguredDataProvider
implements WebResourceDataProvider {
    private final ServerManager serverManager;

    @Autowired
    public NotificationServerConfiguredDataProvider(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public Jsonable get() {
        Iterable serverConfigurations = this.serverManager.getServers();
        boolean isConfigured = StreamSupport.stream(serverConfigurations.spliterator(), false).filter(configuration -> configuration.getNotificationMedium() != null).anyMatch(configuration -> configuration.getNotificationMedium().getKey().matches("mail|hipchat"));
        return new JsonableBoolean(Boolean.valueOf(isConfigured));
    }
}

