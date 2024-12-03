/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.node;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;

public interface RateLimitService {
    public boolean reap();

    public Optional<TokenBucket> getBucket(UserKey var1);

    public boolean tryRateLimitPreAuth(UserKey var1);

    public boolean tryAcquire(UserKey var1);
}

