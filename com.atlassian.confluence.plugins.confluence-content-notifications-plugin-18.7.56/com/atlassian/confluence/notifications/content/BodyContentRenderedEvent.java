/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.notifications.NotificationContentCacheKey
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.notifications.NotificationContentCacheKey;

@EventName(value="confluence.notifications.BodyContentRendered")
public class BodyContentRenderedEvent {
    private final String notificationModuleKey;

    public BodyContentRenderedEvent(NotificationContentCacheKey cacheKey) {
        this.notificationModuleKey = cacheKey.getKey().getCompleteKey();
    }

    public String getNotificationModuleKey() {
        return this.notificationModuleKey;
    }
}

