/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.security.ForgotPasswordEvent
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content.transformer;

import com.atlassian.confluence.event.events.security.ForgotPasswordEvent;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.ForgotPasswordPayload;
import com.atlassian.confluence.notifications.content.SimpleForgotPasswordPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class ForgotPasswordPayloadTransformer
extends PayloadTransformerTemplate<ForgotPasswordEvent, ForgotPasswordPayload> {
    protected Maybe<ForgotPasswordPayload> checkedCreate(ForgotPasswordEvent forgotPasswordEvent) {
        return Option.some((Object)new SimpleForgotPasswordPayload(forgotPasswordEvent.getUser().getKey().getStringValue(), forgotPasswordEvent.getResetPasswordLink(), forgotPasswordEvent.getChangePasswordRequestLink()));
    }
}

