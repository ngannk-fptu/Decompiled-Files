/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationFactory;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.PayloadBasedNotification;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.plugin.ModuleCompleteKey;

public class DescriptorBasedNotificationFactory<PAYLOAD extends NotificationPayload>
implements NotificationFactory<PAYLOAD> {
    private final ModuleCompleteKey key;

    public DescriptorBasedNotificationFactory(NotificationDescriptor<PAYLOAD> descriptor) {
        this.key = new ModuleCompleteKey(descriptor.getCompleteKey());
    }

    @Override
    public Notification<PAYLOAD> create(PAYLOAD payload) {
        return new PayloadBasedNotification<PAYLOAD>(payload, this.key);
    }
}

