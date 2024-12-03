/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.bucket;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import java.util.Objects;

public abstract class TokenBucket {
    protected final TokenBucketSettings settings;

    public TokenBucket(TokenBucketSettings settings) {
        Objects.requireNonNull(settings);
        this.settings = settings;
    }

    public abstract boolean tryAcquire();

    public abstract boolean isFull();

    public abstract long getAvailableTokens();

    public abstract long getSecondsUntilTokenAvailable();

    public TokenBucketSettings getSettings() {
        return this.settings;
    }
}

