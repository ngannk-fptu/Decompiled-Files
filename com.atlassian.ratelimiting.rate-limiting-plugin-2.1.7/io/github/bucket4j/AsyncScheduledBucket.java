/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

public interface AsyncScheduledBucket {
    public CompletableFuture<Boolean> tryConsume(long var1, long var3, ScheduledExecutorService var5);

    default public CompletableFuture<Boolean> tryConsume(long numTokens, Duration maxWait, ScheduledExecutorService scheduler) {
        return this.tryConsume(numTokens, maxWait.toNanos(), scheduler);
    }

    public CompletableFuture<Void> consume(long var1, ScheduledExecutorService var3);
}

