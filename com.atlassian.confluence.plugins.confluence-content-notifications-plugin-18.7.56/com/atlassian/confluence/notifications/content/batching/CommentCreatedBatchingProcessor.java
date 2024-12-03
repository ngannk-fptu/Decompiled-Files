/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.notifications.batch.service.BatchingProcessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content.batching;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.batch.service.BatchingProcessor;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.SimpleCommentPayload;
import com.atlassian.confluence.notifications.content.batching.CommentContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExperimentalSpi
public class CommentCreatedBatchingProcessor
implements BatchingProcessor<CommentPayload, SimpleCommentPayload, List<CommentContext>> {
    public List<CommentContext> process(CommentPayload payload, List<CommentContext> previousContext) {
        ArrayList<CommentContext> newContext = previousContext == null ? new ArrayList<CommentContext>() : previousContext;
        Optional originator = payload.getOriginatorUserKey();
        CommentContext contextData = new CommentContext((UserKey)originator.orElse(null), payload.getContentId(), payload.getParentCommentId(), payload.getContainerId(), (Maybe<String>)payload.getNotificationKey());
        newContext.add(contextData);
        return newContext;
    }

    public Class<SimpleCommentPayload> getPayloadTypeImpl() {
        return SimpleCommentPayload.class;
    }

    public Class<CommentPayload> getPayloadType() {
        return CommentPayload.class;
    }
}

