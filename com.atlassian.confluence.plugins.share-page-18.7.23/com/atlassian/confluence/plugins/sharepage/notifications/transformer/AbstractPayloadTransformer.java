/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.sharepage.notifications.transformer;

import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.sharepage.api.ShareEvent;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.SimpleShareContentPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public abstract class AbstractPayloadTransformer<EVENT extends ShareEvent>
extends PayloadTransformerTemplate<EVENT, SimpleShareContentPayload> {
    private final UserAccessor userAccessor;

    public AbstractPayloadTransformer(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected Maybe<SimpleShareContentPayload> checkedCreate(EVENT event) {
        ConfluenceUser userByName = this.userAccessor.getUserByName(((ShareEvent)event).getSenderUsername());
        if (userByName == null) {
            return Option.none();
        }
        SimpleShareContentPayload payload = new SimpleShareContentPayload(userByName.getKey().getStringValue(), ((ShareEvent)event).getEntityId(), ((ShareEvent)event).getContextualPageId(), ((ShareEvent)event).getUsers(), ((ShareEvent)event).getGroupNames(), ((ShareEvent)event).getEmails(), ((ShareEvent)event).getNote(), ((ShareEvent)event).getEmailsWithGroups(), ((ShareEvent)event).getRequestEmails());
        return Option.some((Object)payload);
    }
}

