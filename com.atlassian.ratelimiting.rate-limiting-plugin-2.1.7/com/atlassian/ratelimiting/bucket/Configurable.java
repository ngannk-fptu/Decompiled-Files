/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.bucket;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public interface Configurable {
    public TokenBucketSettings getSettings();
}

