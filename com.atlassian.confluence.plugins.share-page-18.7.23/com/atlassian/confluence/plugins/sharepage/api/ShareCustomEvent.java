/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.content.ContentType
 */
package com.atlassian.confluence.plugins.sharepage.api;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent;
import java.util.Map;
import java.util.Set;

@EventName(value="confluence.share-page.custom.success")
public class ShareCustomEvent
extends ShareContentEvent {
    public ShareCustomEvent(String senderUsername, Set<String> users, Set<String> originalRequestEmails, Map<String, Set<String>> emails, Set<String> groupNames, Long entityId, ContentType contentType, String note) {
        super(senderUsername, users, originalRequestEmails, emails, groupNames, entityId, contentType, note);
    }
}

