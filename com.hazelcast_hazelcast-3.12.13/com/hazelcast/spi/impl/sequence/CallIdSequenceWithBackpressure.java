/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.sequence;

import com.hazelcast.core.HazelcastOverloadException;
import com.hazelcast.spi.impl.sequence.AbstractCallIdSequence;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.concurrent.BackoffIdleStrategy;
import com.hazelcast.util.concurrent.IdleStrategy;
import java.util.concurrent.TimeUnit;

public final class CallIdSequenceWithBackpressure
extends AbstractCallIdSequence {
    static final int MAX_DELAY_MS = 500;
    private static final IdleStrategy IDLER = new BackoffIdleStrategy(0L, 0L, TimeUnit.MILLISECONDS.toNanos(1L), TimeUnit.MILLISECONDS.toNanos(500L));
    private final long backoffTimeoutNanos;

    public CallIdSequenceWithBackpressure(int maxConcurrentInvocations, long backoffTimeoutMs) {
        super(maxConcurrentInvocations);
        Preconditions.checkPositive(backoffTimeoutMs, "backoffTimeoutMs should be a positive number. backoffTimeoutMs=" + backoffTimeoutMs);
        this.backoffTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(backoffTimeoutMs);
    }

    @Override
    protected void handleNoSpaceLeft() {
        long start = System.nanoTime();
        long idleCount = 0L;
        while (true) {
            long elapsedNanos;
            if ((elapsedNanos = System.nanoTime() - start) > this.backoffTimeoutNanos) {
                throw new HazelcastOverloadException(String.format("Timed out trying to acquire another call ID. maxConcurrentInvocations = %d, backoffTimeout = %d msecs, elapsed:%d msecs", this.getMaxConcurrentInvocations(), TimeUnit.NANOSECONDS.toMillis(this.backoffTimeoutNanos), TimeUnit.NANOSECONDS.toMillis(elapsedNanos)));
            }
            IDLER.idle(idleCount);
            if (this.hasSpace()) {
                return;
            }
            ++idleCount;
        }
    }
}

