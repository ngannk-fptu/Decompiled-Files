/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.bucket;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public class UnlimitedTokenBucket
extends TokenBucket {
    public UnlimitedTokenBucket() {
        super(TokenBucketSettings.unlimited());
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public boolean tryAcquire() {
        return true;
    }

    @Override
    public long getAvailableTokens() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getSecondsUntilTokenAvailable() {
        return 0L;
    }
}

