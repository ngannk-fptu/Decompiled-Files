/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchRequest;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import java.util.Optional;

public interface UserRateLimitSettingsDao {
    public Optional<UserRateLimitSettings> get(UserKey var1);

    public List<UserRateLimitSettings> findAll();

    public Page<UserRateLimitSettings> search(UserRateLimitSettingsSearchRequest var1, PageRequest var2);

    public UserRateLimitSettings saveOrUpdate(UserRateLimitSettings var1);

    public void delete(UserKey var1);

    public Optional<Long> getLatestUserSettingsVersion();

    public long getExemptionsCount();
}

