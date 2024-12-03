/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.AsyncBucket;
import io.github.bucket4j.AsyncScheduledBucket;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketListener;
import io.github.bucket4j.BucketState;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;

public interface Bucket {
    public BlockingBucket asScheduler();

    public boolean isAsyncModeSupported();

    public AsyncBucket asAsync();

    public AsyncScheduledBucket asAsyncScheduler();

    public boolean tryConsume(long var1);

    public ConsumptionProbe tryConsumeAndReturnRemaining(long var1);

    public EstimationProbe estimateAbilityToConsume(long var1);

    public long tryConsumeAsMuchAsPossible();

    public long tryConsumeAsMuchAsPossible(long var1);

    public void addTokens(long var1);

    public long getAvailableTokens();

    public void replaceConfiguration(BucketConfiguration var1);

    public BucketState createSnapshot();

    public Bucket toListenable(BucketListener var1);
}

