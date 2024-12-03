/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.plugin.notifications.dispatcher.task.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="notifications.individual.sent")
public class IndividualNotificationSentEvent {
    private final String notification;
    private final String medium;

    public IndividualNotificationSentEvent(String notification, String medium) {
        this.notification = notification;
        this.medium = medium;
    }

    public String getNotification() {
        return this.notification;
    }

    public String getMedium() {
        return this.medium;
    }
}

