/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;

@ExperimentalApi
public interface NotificationFactory<PAYLOAD extends NotificationPayload> {
    public Notification<PAYLOAD> create(PAYLOAD var1);
}

