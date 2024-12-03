/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.mywork.event.notification.PushNotificationEvent
 */
package com.atlassian.confluence.plugins.mobile.listener;

import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.event.api.EventListener;
import com.atlassian.mywork.event.notification.PushNotificationEvent;

public class PushNotificationEventListener {
    private final PushNotificationService pushNotificationService;

    public PushNotificationEventListener(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @EventListener
    public void onNotificationCreatedEvent(PushNotificationEvent event) throws Exception {
        this.pushNotificationService.push(event.getNotifications());
    }
}

