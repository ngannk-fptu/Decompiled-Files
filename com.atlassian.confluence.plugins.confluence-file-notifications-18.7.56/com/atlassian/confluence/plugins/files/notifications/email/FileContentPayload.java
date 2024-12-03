/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileContentPayload
implements NotificationPayload {
    private final FileContentEventType type;
    private final NotificationContent containerNotificationContent;
    private final List<NotificationContent> fileNotificationContents;
    private final NotificationContent previousFileNotificationContent;
    private final NotificationContent descendantNotificationContent;
    private final String originatingUserKey;

    @JsonCreator
    public FileContentPayload(@JsonProperty(value="type") FileContentEventType type, @JsonProperty(value="containerNotificationContent") NotificationContent containerNotificationContent, @JsonProperty(value="fileNotificationContents") List<NotificationContent> fileNotificationContents, @JsonProperty(value="previousFileNotificationContent") NotificationContent previousFileNotificationContent, @JsonProperty(value="descendantNotificationContent") NotificationContent descendantNotificationContent, @JsonProperty(value="originatingUserKey") String originatingUserKey) {
        this.type = type;
        this.containerNotificationContent = containerNotificationContent;
        this.fileNotificationContents = fileNotificationContents;
        this.previousFileNotificationContent = previousFileNotificationContent;
        this.descendantNotificationContent = descendantNotificationContent;
        this.originatingUserKey = originatingUserKey;
    }

    public FileContentEventType getType() {
        return this.type;
    }

    public NotificationContent getContainerNotificationContent() {
        return this.containerNotificationContent;
    }

    public List<NotificationContent> getFileNotificationContents() {
        return this.fileNotificationContents;
    }

    public NotificationContent getPreviousFileNotificationContent() {
        return this.previousFileNotificationContent;
    }

    public NotificationContent getDescendantNotificationContent() {
        return this.descendantNotificationContent;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.option((Object)this.originatingUserKey);
    }
}

