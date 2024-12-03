/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.files.notifications.event;

import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import java.util.List;

public class FileContentEvent {
    private final FileContentEventType type;
    private final NotificationContent containerContent;
    private final List<NotificationContent> fileContents;
    private final NotificationContent previousFileContent;
    private final NotificationContent descendantContent;
    private final String originatingUserKey;
    private final boolean suppressNotifications;

    public FileContentEvent(FileContentEventType type, NotificationContent containerContent, List<NotificationContent> fileContents, NotificationContent previousFileContent, NotificationContent descendantContent, String originatingUserKey, boolean suppressNotifications) {
        this.type = type;
        this.containerContent = containerContent;
        this.fileContents = fileContents;
        this.previousFileContent = previousFileContent;
        this.descendantContent = descendantContent;
        this.originatingUserKey = originatingUserKey;
        this.suppressNotifications = suppressNotifications;
    }

    public FileContentEventType getType() {
        return this.type;
    }

    public NotificationContent getContainerContent() {
        return this.containerContent;
    }

    public List<NotificationContent> getFileContents() {
        return this.fileContents;
    }

    public NotificationContent getPreviousFileContent() {
        return this.previousFileContent;
    }

    public NotificationContent getDescendantContent() {
        return this.descendantContent;
    }

    public String getOriginatingUserKey() {
        return this.originatingUserKey;
    }

    public boolean isSuppressNotifications() {
        return this.suppressNotifications;
    }
}

