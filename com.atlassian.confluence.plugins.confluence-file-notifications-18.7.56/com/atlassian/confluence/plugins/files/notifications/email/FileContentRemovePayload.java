/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentPayload;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.email.RemovedFileContent;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileContentRemovePayload
extends FileContentPayload {
    private final Map<Long, RemovedFileContent> removedFileContents;

    @JsonCreator
    public FileContentRemovePayload(@JsonProperty(value="type") FileContentEventType type, @JsonProperty(value="containerNotificationContent") NotificationContent containerNotificationContent, @JsonProperty(value="fileNotificationContents") List<NotificationContent> fileNotificationContents, @JsonProperty(value="previousFileNotificationContent") NotificationContent previousFileNotificationContent, @JsonProperty(value="descendantNotificationContent") NotificationContent descendantNotificationContent, @JsonProperty(value="originatingUserKey") String originatingUserKey, @JsonProperty(value="removedFileContent") Map<Long, RemovedFileContent> removedFileContents) {
        super(type, containerNotificationContent, fileNotificationContents, previousFileNotificationContent, descendantNotificationContent, originatingUserKey);
        this.removedFileContents = removedFileContents;
    }

    public Map<Long, RemovedFileContent> getRemovedFileContents() {
        return this.removedFileContents;
    }
}

