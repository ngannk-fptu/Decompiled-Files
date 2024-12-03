/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.like.LikeEvent
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.plugins.like.notifications.LikeNotification;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import java.util.List;

public interface LikeNotificationManager {
    @Deprecated
    public List<LikeNotification> getNotifications(LikeEvent var1);

    public List<LikeNotification> getNotifications(LikePayload var1);
}

