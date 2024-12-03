/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.notifications.batch.service.BatchingKey
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.ContentIdPayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.SimpleCommentPayload;
import com.atlassian.confluence.notifications.content.TransformerUtils;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;

public class CommentCreatedPayloadTransformer
extends ContentIdPayloadTransformerTemplate<CommentCreateEvent, CommentPayload> {
    private static final Function<Comment, Long> TO_COMMENT_ID = EntityObject::getId;

    @Override
    public BatchingKey getBatchingColumnValue(CommentPayload payload) {
        return payload == null ? BatchingKey.NO_BATCHING : new BatchingKey(String.valueOf(payload.getContainerId()), payload.getContainerType().getType());
    }

    protected Maybe<CommentPayload> checkedCreate(CommentCreateEvent commentCreateEvent) {
        Comment comment = commentCreateEvent.getComment();
        ContentEntityObject container = comment.getContainer();
        if (commentCreateEvent.isSuppressNotifications() || container instanceof Attachment) {
            return Option.none();
        }
        Long parentId = (Long)Option.option((Object)comment.getParent()).map(TO_COMMENT_ID).getOrNull();
        String parentInlineContext = null;
        if (comment.getParent() != null && comment.getParent().getProperties() != null) {
            parentInlineContext = comment.getParent().getProperties().getStringProperty("inline-original-selection");
        }
        String lastModifierKey = (String)TransformerUtils.getOriginatingUserForContented((Contented)commentCreateEvent).getOrNull();
        SimpleCommentPayload payload = new SimpleCommentPayload(comment.getId(), container.getId(), container.getType(), parentId, lastModifierKey);
        payload.setParentInlineContext(parentInlineContext != null ? parentInlineContext : "");
        return Option.some((Object)payload);
    }
}

