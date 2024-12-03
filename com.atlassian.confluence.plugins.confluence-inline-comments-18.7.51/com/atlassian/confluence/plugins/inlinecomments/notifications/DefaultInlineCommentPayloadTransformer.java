/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer
 *  com.atlassian.confluence.notifications.batch.service.BatchingKey
 *  com.atlassian.confluence.notifications.content.CommentPayload
 *  com.atlassian.confluence.notifications.content.SimpleCommentPayload
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.inlinecomments.notifications;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.SimpleCommentPayload;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentEvent;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class DefaultInlineCommentPayloadTransformer
extends PayloadTransformerTemplate<InlineCommentEvent, CommentPayload>
implements BatchingPayloadTransformer<CommentPayload> {
    protected Maybe<CommentPayload> checkedCreate(InlineCommentEvent inlineCommentEvent) {
        if (inlineCommentEvent.isSuppressNotifications()) {
            return Option.none();
        }
        Comment comment = inlineCommentEvent.getComment();
        ContentEntityObject container = comment.getContainer();
        Comment parentComment = comment.getParent();
        Long parentCommentId = parentComment != null ? Long.valueOf(parentComment.getId()) : null;
        String parentInlineContext = null;
        if (parentComment != null) {
            parentInlineContext = parentComment.getProperties().getStringProperty("inline-original-selection");
        }
        SimpleCommentPayload payload = new SimpleCommentPayload(comment.getId(), container.getId(), container.getType(), parentCommentId, inlineCommentEvent.getUserKey());
        payload.setParentInlineContext(parentInlineContext);
        return Option.some((Object)payload);
    }

    public BatchingKey getBatchingColumnValue(CommentPayload payload) {
        return payload == null ? BatchingKey.NO_BATCHING : new BatchingKey(String.valueOf(payload.getContainerId()), payload.getContainerType().getType());
    }
}

