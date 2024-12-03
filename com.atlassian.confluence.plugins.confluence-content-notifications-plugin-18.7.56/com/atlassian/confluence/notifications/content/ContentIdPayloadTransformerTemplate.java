/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer
 *  com.atlassian.confluence.notifications.batch.service.BatchingKey
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.batch.payload.BatchingPayloadTransformer;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.content.ContentIdPayload;

public abstract class ContentIdPayloadTransformerTemplate<SOURCE, PAYLOAD extends ContentIdPayload>
extends PayloadTransformerTemplate<SOURCE, PAYLOAD>
implements BatchingPayloadTransformer<PAYLOAD> {
    public BatchingKey getBatchingColumnValue(PAYLOAD payload) {
        return payload == null ? BatchingKey.NO_BATCHING : new BatchingKey(String.valueOf(payload.getContentId()), payload.getContentType().getType());
    }
}

