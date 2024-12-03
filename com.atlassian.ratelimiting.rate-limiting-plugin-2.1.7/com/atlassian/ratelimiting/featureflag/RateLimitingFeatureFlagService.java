/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.ratelimiting.featureflag;

import org.apache.commons.lang3.StringUtils;

public interface RateLimitingFeatureFlagService {
    public static final String DRY_RUN = "com.atlassian.ratelimiting.dry.run";

    public boolean isDryRunEnabled();

    default public boolean isDryRunKey(String key) {
        return StringUtils.defaultString((String)key).startsWith(DRY_RUN);
    }
}

