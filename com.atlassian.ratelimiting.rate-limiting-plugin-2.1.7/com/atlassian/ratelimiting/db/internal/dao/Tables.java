/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.db.internal.dao;

import com.atlassian.ratelimiting.db.internal.dao.QSettingsVersion;
import com.atlassian.ratelimiting.db.internal.dao.QSystemRateLimitSettings;
import com.atlassian.ratelimiting.db.internal.dao.QUserRateLimitCounter;
import com.atlassian.ratelimiting.db.internal.dao.QUserRateLimitSettings;

public final class Tables {
    public static final String AO_PREFIX = "AO_AC3877";
    public static final QUserRateLimitCounter RL_COUNTER = new QUserRateLimitCounter("AO_AC3877");
    public static final QUserRateLimitSettings RL_USER_SETTINGS = new QUserRateLimitSettings("AO_AC3877");
    public static final QSystemRateLimitSettings RL_SYSTEM_SETTINGS = new QSystemRateLimitSettings("AO_AC3877");
    public static final QSettingsVersion SETTINGS_VERSION = new QSettingsVersion("AO_AC3877");

    private Tables() {
    }
}

