/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.notification;

import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationSchemeRepresentation {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final Iterable<NotificationRepresentation> notifications;

    @JsonCreator
    public NotificationSchemeRepresentation(@JsonProperty(value="id") int id, @JsonProperty(value="name") String name, @JsonProperty(value="description") String description, @JsonProperty(value="notifications") Iterable<NotificationRepresentation> notifications) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.notifications = notifications;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Iterable<NotificationRepresentation> getNotifications() {
        return this.notifications;
    }
}

