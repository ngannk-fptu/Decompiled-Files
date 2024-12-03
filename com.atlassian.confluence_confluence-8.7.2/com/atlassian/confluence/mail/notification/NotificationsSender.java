/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

public interface NotificationsSender {
    public void sendNotification(Notification var1, NotificationData var2, ConversionContext var3);

    public void sendNotification(String var1, NotificationContext var2, NotificationData var3, ConversionContext var4);

    public void sendPageNotifications(AbstractPage var1, NotificationData var2, ConversionContext var3);

    public void sendSpaceNotifications(Space var1, NotificationData var2, ConversionContext var3);

    public void sendNetworkNotifications(NotificationData var1, ConversionContext var2);

    public void sendNotifications(List<Notification> var1, NotificationData var2, ConversionContext var3);
}

