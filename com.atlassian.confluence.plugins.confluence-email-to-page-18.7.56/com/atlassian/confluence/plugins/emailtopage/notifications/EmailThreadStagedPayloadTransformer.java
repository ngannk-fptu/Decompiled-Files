/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.plugins.emailgateway.events.EmailThreadStagedForUserEvent
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.emailtopage.notifications;

import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.emailgateway.events.EmailThreadStagedForUserEvent;
import com.atlassian.confluence.plugins.emailtopage.events.EmailThreadStagedPayload;
import com.atlassian.confluence.plugins.emailtopage.events.SimpleEmailThreadStagedPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class EmailThreadStagedPayloadTransformer
extends PayloadTransformerTemplate<EmailThreadStagedForUserEvent, EmailThreadStagedPayload> {
    protected Maybe<EmailThreadStagedPayload> checkedCreate(EmailThreadStagedForUserEvent emailThreadStagedForUserEvent) {
        if (emailThreadStagedForUserEvent.isSuppressNotifications()) {
            return Option.none();
        }
        SimpleEmailThreadStagedPayload value = new SimpleEmailThreadStagedPayload(emailThreadStagedForUserEvent.getUserKey().getStringValue(), emailThreadStagedForUserEvent.getEmailThread().getSubject(), emailThreadStagedForUserEvent.getEmailThread().getKey().getToken(), emailThreadStagedForUserEvent.isError());
        return Option.some((Object)value);
    }
}

