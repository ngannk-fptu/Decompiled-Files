/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.notifications.batch.service.BatchingProcessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.batching;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.batch.service.BatchingProcessor;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.SimpleContentEditedPayload;
import com.atlassian.confluence.notifications.content.batching.ContentBatchContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;

@ExperimentalSpi
public class ContentEditedBatchingProcessor
implements BatchingProcessor<ContentEditedPayload, SimpleContentEditedPayload, List<ContentBatchContext>> {
    private final ContentEntityManager contentEntityManager;

    public ContentEditedBatchingProcessor(@Qualifier(value="pageManager") ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public List<ContentBatchContext> process(ContentEditedPayload payload, List<ContentBatchContext> previousContext) {
        ContentEntityObject ceo = this.contentEntityManager.getById(payload.getContentId());
        if (ceo == null || ceo.isDeleted()) {
            return previousContext;
        }
        ArrayList<ContentBatchContext> newContext = previousContext == null ? new ArrayList<ContentBatchContext>() : previousContext;
        Optional originator = payload.getOriginatorUserKey();
        newContext.add(new ContentBatchContext(originator.isPresent() ? (UserKey)originator.get() : null, payload.getOriginalId(), (Maybe<String>)payload.getNotificationKey()));
        return newContext;
    }

    public Class<SimpleContentEditedPayload> getPayloadTypeImpl() {
        return SimpleContentEditedPayload.class;
    }

    public Class<ContentEditedPayload> getPayloadType() {
        return ContentEditedPayload.class;
    }
}

