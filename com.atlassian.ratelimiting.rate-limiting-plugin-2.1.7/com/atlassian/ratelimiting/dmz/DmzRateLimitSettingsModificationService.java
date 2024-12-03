/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.dmz;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchRequest;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchResult;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface DmzRateLimitSettingsModificationService {
    public boolean delete(@Nonnull UserKey var1);

    @Nonnull
    public Optional<UserRateLimitSettings> getUserSettings(@Nonnull UserKey var1);

    @Nonnull
    public TokenBucketSettings getSystemDefaultSettings();

    public void updateSystemDefaultSettings(@Nonnull SystemRateLimitingSettings var1);

    public RateLimitingMode getRateLimitingMode();

    @Nonnull
    public Page<UserRateLimitSettingsSearchResult> searchUserSettings(@Nonnull UserRateLimitSettingsSearchRequest var1, @Nonnull PageRequest var2);

    public UserRateLimitSettings updateUserSettings(@Nonnull UserKey var1, @Nonnull TokenBucketSettings var2);

    public UserRateLimitSettings whitelistUser(@Nonnull UserKey var1);

    public UserRateLimitSettings blacklistUser(@Nonnull UserKey var1);

    @Nonnull
    public SystemJobControlSettings updateJobControlSettings(@Nonnull SystemJobControlSettings var1);

    public long getExemptionsLimit();

    public long getExemptionsCount();
}

