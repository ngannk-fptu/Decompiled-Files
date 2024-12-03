/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.efi.emails.events;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.sal.api.user.UserKey;

public abstract class OnboardingEvent
extends ConfluenceEvent {
    private UserKey userKey;

    public OnboardingEvent(Object src, UserKey userKey) {
        super(src);
        this.userKey = userKey;
    }

    public String getUserKey() {
        return this.userKey.getStringValue();
    }
}

