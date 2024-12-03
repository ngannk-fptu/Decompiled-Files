/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.api.model.content.id.ContentId;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationContent {
    public static final NotificationContent EMPTY = new NotificationContent(ContentId.UNSET, 0, false);
    private final ContentId contentId;
    private final int contentVersion;
    private boolean isLatestVersion;

    @JsonCreator
    public NotificationContent(@JsonProperty(value="contentId") ContentId contentId, @JsonProperty(value="contentVersion") int contentVersion, @JsonProperty(value="latestVersion") boolean isLatestVersion) {
        this.contentId = contentId;
        this.contentVersion = contentVersion;
        this.isLatestVersion = isLatestVersion;
    }

    public ContentId getContentId() {
        return this.contentId;
    }

    public int getContentVersion() {
        return this.contentVersion;
    }

    public boolean isLatestVersion() {
        return this.isLatestVersion;
    }
}

