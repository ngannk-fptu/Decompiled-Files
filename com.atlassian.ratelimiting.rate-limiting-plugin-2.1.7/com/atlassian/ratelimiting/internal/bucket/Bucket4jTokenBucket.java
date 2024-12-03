/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.ratelimiting.internal.bucket;

import com.atlassian.ratelimiting.bucket.Configurable;
import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.google.common.base.Preconditions;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.github.bucket4j.local.SynchronizationStrategy;
import java.time.Duration;
import java.util.Objects;

public class Bucket4jTokenBucket
extends TokenBucket
implements Configurable {
    private final LocalBucket bucket;

    public Bucket4jTokenBucket(TokenBucketSettings settings) {
        super(settings);
        Preconditions.checkArgument((settings.getCapacity() > 0 ? 1 : 0) != 0, (Object)"Bucket must have positive capacity value");
        this.bucket = this.createBucket(settings, Bucket4j.builder().withSynchronizationStrategy(SynchronizationStrategy.LOCK_FREE));
    }

    public Bucket4jTokenBucket(TokenBucketSettings settings, LocalBucket localBucket) {
        super(settings);
        Objects.requireNonNull(localBucket);
        this.bucket = localBucket;
    }

    public Bucket4jTokenBucket(TokenBucketSettings settings, LocalBucketBuilder localBucketBuilder) {
        super(settings);
        Objects.requireNonNull(localBucketBuilder);
        this.bucket = this.createBucket(settings, localBucketBuilder);
    }

    private LocalBucket createBucket(TokenBucketSettings settings, LocalBucketBuilder localBucketBuilder) {
        if (0 == settings.getFillRate()) {
            return ((LocalBucketBuilder)localBucketBuilder.addLimit(Bandwidth.simple(settings.getCapacity(), settings.getIntervalDuration()))).build();
        }
        Refill refill = Refill.greedy(settings.getFillRate(), settings.getIntervalDuration());
        return ((LocalBucketBuilder)localBucketBuilder.addLimit(Bandwidth.classic(settings.getCapacity(), refill))).build();
    }

    @Override
    public boolean tryAcquire() {
        return this.bucket.tryConsume(1L);
    }

    @Override
    public boolean isFull() {
        return (long)this.settings.getCapacity() == this.bucket.getAvailableTokens();
    }

    @Override
    public long getAvailableTokens() {
        return this.bucket.getAvailableTokens();
    }

    @Override
    public long getSecondsUntilTokenAvailable() {
        return Duration.ofNanos(this.bucket.estimateAbilityToConsume(1L).getNanosToWaitForRefill()).getSeconds();
    }

    @Override
    public TokenBucketSettings getSettings() {
        return this.settings;
    }
}

