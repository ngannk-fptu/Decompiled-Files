/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.sampler.Sampler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimitingSampler
extends Sampler {
    static final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
    static final long NANOS_PER_DECISECOND = NANOS_PER_SECOND / 10L;
    final MaxFunction maxFunction;
    final AtomicInteger usage = new AtomicInteger(0);
    final AtomicLong nextReset;

    public static Sampler create(int tracesPerSecond) {
        if (tracesPerSecond < 0) {
            throw new IllegalArgumentException("tracesPerSecond < 0");
        }
        if (tracesPerSecond == 0) {
            return Sampler.NEVER_SAMPLE;
        }
        return new RateLimitingSampler(tracesPerSecond);
    }

    RateLimitingSampler(int tracesPerSecond) {
        this.maxFunction = tracesPerSecond < 10 ? new LessThan10(tracesPerSecond) : new AtLeast10(tracesPerSecond);
        long now = System.nanoTime();
        this.nextReset = new AtomicLong(now + NANOS_PER_SECOND);
    }

    @Override
    public boolean isSampled(long ignoredTraceId) {
        int next;
        int prev;
        long updateAt;
        long now = System.nanoTime();
        long nanosUntilReset = -(now - (updateAt = this.nextReset.get()));
        if (nanosUntilReset <= 0L) {
            if (this.nextReset.compareAndSet(updateAt, now + NANOS_PER_SECOND)) {
                this.usage.set(0);
            }
            return this.isSampled(ignoredTraceId);
        }
        int max = this.maxFunction.max(nanosUntilReset);
        do {
            if ((next = (prev = this.usage.get()) + 1) <= max) continue;
            return false;
        } while (!this.usage.compareAndSet(prev, next));
        return true;
    }

    static final class AtLeast10
    extends MaxFunction {
        final int[] max;

        AtLeast10(int tracesPerSecond) {
            int tracesPerDecisecond = tracesPerSecond / 10;
            int remainder = tracesPerSecond % 10;
            this.max = new int[10];
            this.max[0] = tracesPerDecisecond + remainder;
            for (int i = 1; i < 10; ++i) {
                this.max[i] = this.max[i - 1] + tracesPerDecisecond;
            }
        }

        @Override
        int max(long nanosUntilReset) {
            if (nanosUntilReset > NANOS_PER_SECOND - NANOS_PER_DECISECOND) {
                return this.max[0];
            }
            if (nanosUntilReset < NANOS_PER_DECISECOND) {
                return this.max[9];
            }
            int decisecondsUntilReset = (int)(nanosUntilReset / NANOS_PER_DECISECOND);
            return this.max[10 - decisecondsUntilReset];
        }
    }

    static final class LessThan10
    extends MaxFunction {
        final int tracesPerSecond;

        LessThan10(int tracesPerSecond) {
            this.tracesPerSecond = tracesPerSecond;
        }

        @Override
        int max(long nanosUntilResetIgnored) {
            return this.tracesPerSecond;
        }
    }

    static abstract class MaxFunction {
        MaxFunction() {
        }

        abstract int max(long var1);
    }
}

