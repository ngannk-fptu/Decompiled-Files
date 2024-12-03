/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.UninterruptibleBlockingStrategy;
import java.time.Duration;

public interface BlockingBucket {
    public boolean tryConsume(long var1, long var3, BlockingStrategy var5) throws InterruptedException;

    default public boolean tryConsume(long numTokens, Duration maxWait, BlockingStrategy blockingStrategy) throws InterruptedException {
        return this.tryConsume(numTokens, maxWait.toNanos(), blockingStrategy);
    }

    default public boolean tryConsume(long numTokens, long maxWaitTimeNanos) throws InterruptedException {
        return this.tryConsume(numTokens, maxWaitTimeNanos, BlockingStrategy.PARKING);
    }

    default public boolean tryConsume(long numTokens, Duration maxWait) throws InterruptedException {
        return this.tryConsume(numTokens, maxWait.toNanos(), BlockingStrategy.PARKING);
    }

    public boolean tryConsumeUninterruptibly(long var1, long var3, UninterruptibleBlockingStrategy var5);

    default public boolean tryConsumeUninterruptibly(long numTokens, Duration maxWait, UninterruptibleBlockingStrategy blockingStrategy) {
        return this.tryConsumeUninterruptibly(numTokens, maxWait.toNanos(), blockingStrategy);
    }

    default public boolean tryConsumeUninterruptibly(long numTokens, long maxWaitTimeNanos) {
        return this.tryConsumeUninterruptibly(numTokens, maxWaitTimeNanos, UninterruptibleBlockingStrategy.PARKING);
    }

    default public boolean tryConsumeUninterruptibly(long numTokens, Duration maxWait) {
        return this.tryConsumeUninterruptibly(numTokens, maxWait.toNanos(), UninterruptibleBlockingStrategy.PARKING);
    }

    public void consume(long var1, BlockingStrategy var3) throws InterruptedException;

    default public void consume(long numTokens) throws InterruptedException {
        this.consume(numTokens, BlockingStrategy.PARKING);
    }

    public void consumeUninterruptibly(long var1, UninterruptibleBlockingStrategy var3);

    default public void consumeUninterruptibly(long numTokens) {
        this.consumeUninterruptibly(numTokens, UninterruptibleBlockingStrategy.PARKING);
    }
}

