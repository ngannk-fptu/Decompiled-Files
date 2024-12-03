/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.notification.rest.representations;

import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentation;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationGroupRepresentation {
    @JsonProperty
    private final String notificationType;
    @JsonProperty
    private final Collection<NotificationRepresentation> notifications;
    @JsonProperty
    private final int notificationCount;
    @JsonProperty
    private final Boolean dismissed;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final boolean displayIndividually;
    @JsonProperty
    private final boolean dismissOnClick;

    @JsonCreator
    public NotificationGroupRepresentation(@JsonProperty(value="notificationType") String notificationType, @JsonProperty(value="notifications") Collection<NotificationRepresentation> notifications, @JsonProperty(value="notificationCount") int notificationCount, @JsonProperty(value="dismissed") Boolean dismissed, @JsonProperty(value="title") String title, @JsonProperty(value="message") String message, @JsonProperty(value="links") Map<String, URI> links) {
        this(NotificationType.fromKey(notificationType), notifications, notificationCount, dismissed, title, message, links);
    }

    public NotificationGroupRepresentation(NotificationType notificationType, Collection<NotificationRepresentation> notifications, int notificationCount, Boolean dismissed, String title, String message, Map<String, URI> links) {
        this.notificationType = Objects.requireNonNull(notificationType, "notificationType").getKey();
        this.notifications = Collections.unmodifiableCollection(notifications);
        this.notificationCount = notificationCount;
        this.dismissed = dismissed;
        this.title = Objects.requireNonNull(title, "title");
        this.message = Objects.requireNonNull(message, "message");
        this.links = Collections.unmodifiableMap(links);
        this.displayIndividually = notificationType.isAlwaysDisplayedIndividually();
        this.dismissOnClick = notificationType.isDismissedOnClick();
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public Collection<NotificationRepresentation> getNotifications() {
        return this.notifications;
    }

    public int getNotificationCount() {
        return this.notificationCount;
    }

    public Boolean isDismissed() {
        return this.dismissed;
    }

    public String getTitle() {
        return this.title;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isDisplayIndividually() {
        return this.displayIndividually;
    }

    public boolean isDismissOnClick() {
        return this.dismissOnClick;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

