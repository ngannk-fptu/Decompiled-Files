/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.requestaccess.events.AbstractAccessEvent;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public abstract class AbstractAccessPayloadTransformer<EVENT extends AbstractAccessEvent>
extends PayloadTransformerTemplate<EVENT, DefaultAccessNotificationPayload> {
    protected final Maybe<DefaultAccessNotificationPayload> checkedCreate(EVENT event) {
        return Option.option((Object)new DefaultAccessNotificationPayload(((AbstractAccessEvent)event).getSourceUser().getKey(), ((AbstractAccessEvent)event).getTargetUser().getKey(), ((AbstractAccessEvent)event).getContent().getId(), ContentType.valueOf((String)((AbstractAccessEvent)event).getContent().getType()), ((AbstractAccessEvent)event).getUserRole(), ((AbstractAccessEvent)event).getAccessType().name().toLowerCase(), ((AbstractAccessEvent)event).getContent().isDraft(), ((AbstractAccessEvent)event).getSpaceKey()));
    }
}

