/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.NotificationPayload;

@ExperimentalApi
public interface FollowerPayload
extends NotificationPayload {
    public String getFollower();

    public String getUserBeingFollowed();
}

