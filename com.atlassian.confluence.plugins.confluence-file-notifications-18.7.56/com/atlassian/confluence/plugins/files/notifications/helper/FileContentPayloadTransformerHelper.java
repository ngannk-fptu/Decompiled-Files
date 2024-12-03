/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Version
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.files.api.FileComment
 */
package com.atlassian.confluence.plugins.files.notifications.helper;

import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.files.api.FileComment;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;

public class FileContentPayloadTransformerHelper {
    public static NotificationContent getNotificationContentForCeo(ContentEntityObject ceo) {
        if (ceo == null) {
            return NotificationContent.EMPTY;
        }
        return new NotificationContent(FileContentPayloadTransformerHelper.getLatestCeoId(ceo), ceo.getVersion(), ceo.isLatestVersion());
    }

    public static NotificationContent getNotificationContentForFileComment(FileComment fileComment) {
        if (fileComment == null) {
            return NotificationContent.EMPTY;
        }
        return new NotificationContent(fileComment.getId(), ((Version)fileComment.getVersion().get()).getNumber(), true);
    }

    private static ContentId getLatestCeoId(ContentEntityObject ceo) {
        return ContentId.deserialise((String)((ContentEntityObject)ceo.getLatestVersion()).getIdAsString());
    }
}

