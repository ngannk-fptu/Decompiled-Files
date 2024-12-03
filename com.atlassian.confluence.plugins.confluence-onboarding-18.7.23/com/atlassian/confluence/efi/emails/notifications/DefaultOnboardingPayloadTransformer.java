/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.efi.emails.notifications;

import com.atlassian.confluence.efi.emails.events.OnboardingEvent;
import com.atlassian.confluence.efi.emails.notifications.OnboardingPayload;
import com.atlassian.confluence.efi.emails.notifications.SimpleOnboardingPayload;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class DefaultOnboardingPayloadTransformer
extends PayloadTransformerTemplate<OnboardingEvent, OnboardingPayload> {
    protected Maybe<OnboardingPayload> checkedCreate(OnboardingEvent onboardingEvent) {
        SimpleOnboardingPayload payload = new SimpleOnboardingPayload(onboardingEvent.getUserKey());
        return Option.some((Object)payload);
    }
}

