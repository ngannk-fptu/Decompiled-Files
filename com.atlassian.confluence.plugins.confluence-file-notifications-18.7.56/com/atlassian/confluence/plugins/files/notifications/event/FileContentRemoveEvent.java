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
import com.atlassian.confluence.plugins.files.notifications.email.RemovedFileContent;
import com.atlassian.confluence.plugins.files.notifications.event.FileContentEvent;
import java.util.List;
import java.util.Map;

public class FileContentRemoveEvent
extends FileContentEvent
implements NotificationEnabledEvent {
    private final Map<Long, RemovedFileContent> removedFileContents;

    public FileContentRemoveEvent(FileContentEventType type, NotificationContent containerContent, List<NotificationContent> fileContents, NotificationContent previousFileContent, NotificationContent descendantContent, String originatingUserKey, Map<Long, RemovedFileContent> removedFileContents, boolean suppressNotifications) {
        super(type, containerContent, fileContents, previousFileContent, descendantContent, originatingUserKey, suppressNotifications);
        this.removedFileContents = removedFileContents;
    }

    public Map<Long, RemovedFileContent> getRemovedFileContents() {
        return this.removedFileContents;
    }
}

