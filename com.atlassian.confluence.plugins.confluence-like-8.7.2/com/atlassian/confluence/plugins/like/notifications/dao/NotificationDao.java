/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.like.notifications.dao;

import com.atlassian.confluence.plugins.like.notifications.LikeNotification;

public interface NotificationDao {
    public boolean exists(LikeNotification var1);

    public void save(LikeNotification var1);
}

