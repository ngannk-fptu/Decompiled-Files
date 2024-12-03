/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.events;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.events.UserRateLimitSettingsEvent;
import com.atlassian.sal.api.user.UserKey;

public class UserRateLimitSettingsDeletedEvent
implements UserRateLimitSettingsEvent {
    private final UserRateLimitSettings settings;

    @Override
    public UserKey getUserKey() {
        return this.settings.getUserKey();
    }

    public UserRateLimitSettings getSettings() {
        return this.settings;
    }

    public UserRateLimitSettingsDeletedEvent(UserRateLimitSettings settings) {
        this.settings = settings;
    }
}

