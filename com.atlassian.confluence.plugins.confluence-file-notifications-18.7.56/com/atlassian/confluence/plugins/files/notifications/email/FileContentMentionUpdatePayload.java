/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileContentMentionUpdatePayload
extends FileContentUpdatePayload {
    private final String mentionedUserKey;

    @JsonCreator
    public FileContentMentionUpdatePayload(@JsonProperty(value="type") FileContentEventType type, @JsonProperty(value="containerNotificationContent") NotificationContent containerNotificationContent, @JsonProperty(value="fileNotificationContents") List<NotificationContent> fileNotificationContents, @JsonProperty(value="previousFileNotificationContent") NotificationContent previousFileNotificationContent, @JsonProperty(value="descendantNotificationContent") NotificationContent descendantNotificationContent, @JsonProperty(value="originatingUserKey") String originatingUserKey, @JsonProperty(value="mentionedUserKey") String mentionedUserKey) {
        super(type, containerNotificationContent, fileNotificationContents, previousFileNotificationContent, descendantNotificationContent, originatingUserKey);
        this.mentionedUserKey = mentionedUserKey;
    }

    public String getMentionedUserKey() {
        return this.mentionedUserKey;
    }
}

