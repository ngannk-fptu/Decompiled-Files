/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.plugins.sharepage.api;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.plugins.sharepage.api.ShareEvent;
import java.util.Map;
import java.util.Set;

@EventName(value="confluence.share-page.attachment.success")
public class ShareAttachmentEvent
extends ShareEvent
implements NotificationEnabledEvent {
    public ShareAttachmentEvent(String senderUsername, Set<String> users, Set<String> originalRequestEmails, Map<String, Set<String>> emails, Set<String> groupNames, Long entityId, Long contextualPageId, String note) {
        super(senderUsername, entityId, contextualPageId, ContentType.ATTACHMENT.toString(), note, users, originalRequestEmails, emails, groupNames);
    }

    public Long getAttachmentId() {
        return this.getEntityId();
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

