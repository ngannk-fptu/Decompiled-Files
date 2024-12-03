/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.BucketExceptions;
import java.io.Serializable;
import java.time.Duration;

public class Refill
implements Serializable {
    private static final long serialVersionUID = 42L;
    final long periodNanos;
    final long tokens;
    final boolean refillIntervally;

    private Refill(long tokens, Duration period, boolean refillIntervally) {
        if (period == null) {
            throw BucketExceptions.nullRefillPeriod();
        }
        if (tokens <= 0L) {
            throw BucketExceptions.nonPositivePeriodTokens(tokens);
        }
        this.periodNanos = period.toNanos();
        if (this.periodNanos <= 0L) {
            throw BucketExceptions.nonPositivePeriod(this.periodNanos);
        }
        if (tokens > this.periodNanos) {
            throw BucketExceptions.tooHighRefillRate(this.periodNanos, tokens);
        }
        this.tokens = tokens;
        this.refillIntervally = refillIntervally;
    }

    @Deprecated
    public static Refill of(long tokens, Duration period) {
        return Refill.greedy(tokens, period);
    }

    @Deprecated
    public static Refill smooth(long tokens, Duration period) {
        return Refill.greedy(tokens, period);
    }

    public static Refill greedy(long tokens, Duration period) {
        return new Refill(tokens, period, false);
    }

    public static Refill intervally(long tokens, Duration period) {
        return new Refill(tokens, period, true);
    }

    public String toString() {
        return "Refill{periodNanos=" + this.periodNanos + ", tokens=" + this.tokens + '}';
    }
}

