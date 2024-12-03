/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import java.util.concurrent.CompletableFuture;

public interface AsyncBucket {
    public CompletableFuture<Boolean> tryConsume(long var1);

    public CompletableFuture<ConsumptionProbe> tryConsumeAndReturnRemaining(long var1);

    public CompletableFuture<EstimationProbe> estimateAbilityToConsume(long var1);

    public CompletableFuture<Long> tryConsumeAsMuchAsPossible();

    public CompletableFuture<Long> tryConsumeAsMuchAsPossible(long var1);

    public CompletableFuture<Void> addTokens(long var1);

    public CompletableFuture<Void> replaceConfiguration(BucketConfiguration var1);
}

