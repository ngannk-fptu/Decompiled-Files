/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.emailgateway.notifications;

import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionEvent;
import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionPayload;
import com.atlassian.confluence.plugins.emailgateway.events.SimpleEmailHandlingExceptionPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class EmailHandlingExceptionPayloadTransformer
extends PayloadTransformerTemplate<EmailHandlingExceptionEvent, EmailHandlingExceptionPayload> {
    protected Maybe<EmailHandlingExceptionPayload> checkedCreate(EmailHandlingExceptionEvent emailHandlingExceptionEvent) {
        if (emailHandlingExceptionEvent.isSuppressNotifications()) {
            return Option.none();
        }
        SimpleEmailHandlingExceptionPayload value = new SimpleEmailHandlingExceptionPayload(emailHandlingExceptionEvent.getEmailAddress(), emailHandlingExceptionEvent.getEmailSubject(), emailHandlingExceptionEvent.isCreatePageError(), emailHandlingExceptionEvent.isAttachmentError(), emailHandlingExceptionEvent.isReadOnlyModeError());
        return Option.some((Object)value);
    }
}

