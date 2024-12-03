/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.event.events.content.page.PageMoveEvent
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.ContentMovedPayload;
import com.atlassian.fugue.Maybe;

public class PageMovedEventPayloadTransformer
extends PayloadTransformerTemplate<PageMoveEvent, ContentMovedPayload> {
    protected Maybe<ContentMovedPayload> checkedCreate(PageMoveEvent event) {
        return MaybeNot.becauseOf((String)"Move notification is disabled", (Object[])new Object[0]);
    }
}

