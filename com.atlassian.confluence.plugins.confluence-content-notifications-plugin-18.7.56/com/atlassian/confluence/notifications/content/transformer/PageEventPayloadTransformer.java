/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.notifications.batch.service.BatchingKey
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.notifications.batch.service.BatchingKey;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.ContentIdPayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.SimpleContentIdPayload;
import com.atlassian.confluence.notifications.content.TransformerUtils;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class PageEventPayloadTransformer
extends ContentIdPayloadTransformerTemplate<PageEvent, ContentIdPayload> {
    protected Maybe<ContentIdPayload> checkedCreate(PageEvent source) {
        if (source.isSuppressNotifications()) {
            return Option.none();
        }
        String originatingUserKey = (String)TransformerUtils.getOriginatingUserForContented((Contented)source).getOrNull();
        SimpleContentIdPayload payload = new SimpleContentIdPayload(ContentType.PAGE, source.getContent().getId(), originatingUserKey);
        return Option.some((Object)payload);
    }

    @Override
    public BatchingKey getBatchingColumnValue(ContentIdPayload payload) {
        return BatchingKey.NO_BATCHING;
    }
}

