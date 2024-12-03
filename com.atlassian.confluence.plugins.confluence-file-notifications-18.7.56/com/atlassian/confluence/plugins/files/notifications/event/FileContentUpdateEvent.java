/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.plugins.files.notifications.event;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.event.FileContentEvent;
import java.util.List;

public class FileContentUpdateEvent
extends FileContentEvent
implements NotificationEnabledEvent {
    public FileContentUpdateEvent(FileContentEventType type, NotificationContent containerContent, List<NotificationContent> fileContents, NotificationContent previousFileContent, NotificationContent descendantContent, String originatingUserKey, boolean suppressNotifications) {
        super(type, containerContent, fileContents, previousFileContent, descendantContent, originatingUserKey, suppressNotifications);
    }
}

