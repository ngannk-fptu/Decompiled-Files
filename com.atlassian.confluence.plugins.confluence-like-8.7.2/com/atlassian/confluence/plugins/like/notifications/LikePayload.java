/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.notifications.NotificationPayload
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.NotificationPayload;

public interface LikePayload
extends NotificationPayload {
    public long getContentId();

    public ContentType getContentType();
}

