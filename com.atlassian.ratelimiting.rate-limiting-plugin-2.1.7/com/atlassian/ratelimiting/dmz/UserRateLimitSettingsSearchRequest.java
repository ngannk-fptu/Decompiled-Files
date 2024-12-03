/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.ratelimiting.dmz;

import java.util.List;
import javax.annotation.Nullable;

public class UserRateLimitSettingsSearchRequest {
    public static final UserRateLimitSettingsSearchRequest NO_FILTER = new UserRateLimitSettingsSearchRequest(null);
    private final List<String> filter;

    public UserRateLimitSettingsSearchRequest(@Nullable List<String> filter) {
        this.filter = filter;
    }

    @Nullable
    public List<String> getFilter() {
        return this.filter;
    }
}

