/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.UserRemoveEvent
 *  com.atlassian.event.api.EventListener
 */
package com.atlassian.confluence.plugins.mobile.listener;

import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.event.api.EventListener;

public class MobileRemoveUserListener {
    private final PushNotificationService pushNotificationService;

    public MobileRemoveUserListener(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @EventListener
    public void userRemoved(UserRemoveEvent event) {
        this.pushNotificationService.removePushNotification(event.getUser());
    }
}

