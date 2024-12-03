/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.bucket;

import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.sal.api.user.UserKey;

public interface TokenBucketFactory {
    public TokenBucket createTokenBucket(UserKey var1);

    public boolean hasCurrentSettings(UserKey var1, TokenBucket var2);
}

