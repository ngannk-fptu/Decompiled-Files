/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.content.FollowerNotification;

@ExperimentalApi
public interface ContentIdPayload
extends NotificationPayload,
FollowerNotification {
    public long getContentId();

    public ContentType getContentType();
}

