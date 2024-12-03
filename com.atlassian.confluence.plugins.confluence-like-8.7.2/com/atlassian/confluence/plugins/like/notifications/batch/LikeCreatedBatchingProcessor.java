/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.batch.service.BatchingProcessor
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.like.notifications.batch;

import com.atlassian.confluence.notifications.batch.service.BatchingProcessor;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.plugins.like.notifications.SimpleLikePayload;
import com.atlassian.confluence.plugins.like.notifications.batch.LikeContext;
import com.atlassian.sal.api.user.UserKey;

public class LikeCreatedBatchingProcessor
implements BatchingProcessor<LikePayload, SimpleLikePayload, LikeContext> {
    public LikeContext process(LikePayload payload, LikeContext context) {
        LikeContext newContext = context == null ? new LikeContext(payload.getContentType(), payload.getContentId()) : context;
        newContext.getUserKeys().add(new UserKey((String)payload.getOriginatingUserKey().getOrNull()));
        return newContext;
    }

    public Class<SimpleLikePayload> getPayloadTypeImpl() {
        return SimpleLikePayload.class;
    }

    public Class<LikePayload> getPayloadType() {
        return LikePayload.class;
    }
}

