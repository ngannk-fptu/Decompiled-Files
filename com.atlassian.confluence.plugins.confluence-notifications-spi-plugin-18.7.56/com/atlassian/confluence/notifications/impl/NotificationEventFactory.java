/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.event.NotificationEvent
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;

public interface NotificationEventFactory<PAYLOAD extends NotificationPayload> {
    public NotificationEvent<Notification<PAYLOAD>> create(Notification<PAYLOAD> var1);
}

