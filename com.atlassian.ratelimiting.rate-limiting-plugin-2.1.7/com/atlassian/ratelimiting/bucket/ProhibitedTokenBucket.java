/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.bucket;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public class ProhibitedTokenBucket
extends TokenBucket {
    public ProhibitedTokenBucket() {
        super(TokenBucketSettings.prohibited());
    }

    @Override
    public boolean tryAcquire() {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public long getAvailableTokens() {
        return 0L;
    }

    @Override
    public long getSecondsUntilTokenAvailable() {
        return Long.MAX_VALUE;
    }
}

