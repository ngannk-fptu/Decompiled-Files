/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.tasklist.notification.api;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.plugins.tasklist.TaskModfication;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;

public interface TaskPayload
extends NotificationPayload {
    public ContentId getContentId();

    public Map<UserKey, Iterable<TaskModfication>> getTasks();
}

