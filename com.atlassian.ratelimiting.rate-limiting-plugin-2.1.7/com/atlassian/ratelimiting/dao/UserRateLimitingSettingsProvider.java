/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;

public interface UserRateLimitingSettingsProvider {
    public Optional<UserRateLimitSettings> get(UserKey var1);

    public boolean tryReloadCache();

    public void forceReloadCache();
}

