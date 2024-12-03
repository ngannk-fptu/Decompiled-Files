/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.event.events.content.Contented
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.event.events.content.Contented;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.notifications.content.ContentEditedPayloadTransformer;
import com.atlassian.confluence.notifications.content.TransformerUtils;

public class PageEditedPayloadTransformer
extends ContentEditedPayloadTransformer<PageUpdateEvent> {
    @Override
    protected long getSourceId(PageUpdateEvent event) {
        return event.getNew().getId();
    }

    @Override
    protected long getOriginalId(PageUpdateEvent event) {
        ConfluenceEntityObject old = event.getOld();
        Long originalId = old != null ? Long.valueOf(old.getId()) : null;
        return originalId;
    }

    @Override
    protected boolean isNotificationSuppressed(PageUpdateEvent event) {
        return event.isSuppressNotifications();
    }

    @Override
    protected String getOriginatingUserKey(PageUpdateEvent event) {
        return (String)TransformerUtils.getOriginatingUserForContented((Contented)event).getOrNull();
    }

    @Override
    protected ContentType getContentType(PageUpdateEvent pageEditedEvent) {
        return ContentType.PAGE;
    }
}

