/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.event.events.like.LikeEvent
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer
 *  com.atlassian.confluence.notifications.batch.service.BatchingKey
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.event.events.like.LikeEvent;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.plugins.like.notifications.SimpleLikePayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;

public class CreateLikeEventPayloadTransformer
extends PayloadTransformerTemplate<LikeEvent, LikePayload>
implements BatchingPayloadTransformer<LikePayload> {
    private final UserAccessor userAccessor;

    protected CreateLikeEventPayloadTransformer(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public BatchingKey getBatchingColumnValue(LikePayload likePayload) {
        if (likePayload == null || !likePayload.getContentType().in(new BaseApiEnum[]{ContentType.BLOG_POST, ContentType.PAGE, ContentType.COMMENT})) {
            return BatchingKey.NO_BATCHING;
        }
        return new BatchingKey(Long.toString(likePayload.getContentId()), likePayload.getContentType().getType());
    }

    protected Maybe<LikePayload> checkedCreate(LikeEvent likeEvent) {
        UserKey userKey;
        ContentEntityObject owner;
        ContentEntityObject content = likeEvent.getContent();
        User originatingUser = likeEvent.getOriginatingUser();
        if (originatingUser == null) {
            return MaybeNot.becauseOf((String)"Can't create payload as user is anonymous. Anonymous users cannot like content.", (Object[])new Object[0]);
        }
        if (content instanceof Comment && (owner = ((Comment)content).getContainer()) == null) {
            return MaybeNot.becauseOf((String)"The page should not be null for a comment", (Object[])new Object[0]);
        }
        if (originatingUser instanceof ConfluenceUser) {
            userKey = ((ConfluenceUser)originatingUser).getKey();
        } else {
            ConfluenceUser user = this.userAccessor.getUserByName(originatingUser.getName());
            if (user == null) {
                return MaybeNot.becauseOf((String)"User not found", (Object[])new Object[0]);
            }
            userKey = user.getKey();
        }
        SimpleLikePayload payload = new SimpleLikePayload(content.getId(), ContentType.valueOf((String)content.getType()), userKey.getStringValue());
        return Option.some((Object)payload);
    }
}

