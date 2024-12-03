/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.rest;

import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.rest.JsonGroupNotification;
import com.atlassian.mywork.rest.JsonNotificationItem;
import com.atlassian.mywork.rest.JsonObject;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonNotificationGroup
implements JsonObject {
    @JsonProperty
    private JsonNotificationItem item;
    @JsonProperty
    private List<JsonGroupNotification> notifications;

    private JsonNotificationGroup() {
    }

    public JsonNotificationGroup(Notification notification, List<JsonGroupNotification> notifications, String aggregateKey) {
        this.item = new JsonNotificationItem(notification, aggregateKey);
        this.notifications = notifications;
    }

    public JsonNotificationItem getItem() {
        return this.item;
    }

    public void setNotifications(List<JsonGroupNotification> notifications) {
        this.notifications = notifications;
    }

    public List<JsonGroupNotification> getNotifications() {
        return this.notifications;
    }
}

