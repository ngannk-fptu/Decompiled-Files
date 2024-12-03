/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.efi.emails.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;
import com.atlassian.confluence.efi.emails.events.OnboardingEvent;
import com.atlassian.sal.api.user.UserKey;

@EventName(value="onboarding.notification.email.no.space.created.event")
public class OnboardingNoSpaceCreatedEvent
extends OnboardingEvent
implements NotificationEnabledEvent {
    public OnboardingNoSpaceCreatedEvent(Object src, UserKey userKey) {
        super(src, userKey);
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

