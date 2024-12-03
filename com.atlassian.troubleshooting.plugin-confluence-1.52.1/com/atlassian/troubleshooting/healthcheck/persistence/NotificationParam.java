/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.persistence;

import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationParam {
    @JsonProperty
    private final String username;
    @JsonProperty
    private final Integer notificationId;
    @JsonProperty
    private final boolean snoozed;

    public NotificationParam(@JsonProperty(value="username") String username, @JsonProperty(value="notificationId") Integer notificationId, @JsonProperty(value="snooze") boolean snoozed) {
        this.username = username;
        this.notificationId = notificationId;
        this.snoozed = snoozed;
    }

    public String getUsername() {
        return this.username;
    }

    public int getNotificationId() {
        return this.notificationId;
    }

    public boolean getIsSnoozed() {
        return this.snoozed;
    }
}

