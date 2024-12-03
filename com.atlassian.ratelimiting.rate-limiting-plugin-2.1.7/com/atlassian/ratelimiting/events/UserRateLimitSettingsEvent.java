/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.events;

import com.atlassian.sal.api.user.UserKey;

public interface UserRateLimitSettingsEvent {
    public UserKey getUserKey();
}

