/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.notifications.content.ContentEditedPayloadTransformer;

public class CommentEditedPayloadTransformer
extends ContentEditedPayloadTransformer<CommentUpdateEvent> {
    @Override
    protected boolean isNotificationSuppressed(CommentUpdateEvent commentUpdateEvent) {
        return commentUpdateEvent.isSuppressNotifications();
    }

    @Override
    protected String getOriginatingUserKey(CommentUpdateEvent commentUpdateEvent) {
        return commentUpdateEvent.getComment().getLastModifier().getKey().getStringValue();
    }

    @Override
    protected ContentType getContentType(CommentUpdateEvent commentUpdateEvent) {
        return ContentType.valueOf((String)commentUpdateEvent.getComment().getType());
    }

    @Override
    protected long getSourceId(CommentUpdateEvent commentUpdateEvent) {
        return commentUpdateEvent.getComment().getId();
    }

    @Override
    protected long getOriginalId(CommentUpdateEvent commentUpdateEvent) {
        return commentUpdateEvent.getOriginalComment().getId();
    }

    @Override
    protected String getInlineContext(CommentUpdateEvent commentUpdateEvent) {
        ContentProperties commentProperties = commentUpdateEvent.getComment().getProperties();
        if (Boolean.valueOf(commentProperties.getStringProperty("inline-comment")).booleanValue()) {
            return commentProperties.getStringProperty("inline-original-selection");
        }
        return null;
    }
}

