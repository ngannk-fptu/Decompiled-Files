/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

import io.github.bucket4j.BucketExceptions;
import io.github.bucket4j.Refill;
import java.io.Serializable;
import java.time.Duration;

public class Bandwidth
implements Serializable {
    private static final long serialVersionUID = 101L;
    final long capacity;
    final long initialTokens;
    final long refillPeriodNanos;
    final long refillTokens;
    final boolean refillIntervally;

    private Bandwidth(long capacity, long refillPeriodNanos, long refillTokens, long initialTokens, boolean refillIntervally) {
        this.capacity = capacity;
        this.initialTokens = initialTokens;
        this.refillPeriodNanos = refillPeriodNanos;
        this.refillTokens = refillTokens;
        this.refillIntervally = refillIntervally;
    }

    public static Bandwidth simple(long capacity, Duration period) {
        Refill refill = Refill.greedy(capacity, period);
        return Bandwidth.classic(capacity, refill);
    }

    public static Bandwidth classic(long capacity, Refill refill) {
        if (capacity <= 0L) {
            throw BucketExceptions.nonPositiveCapacity(capacity);
        }
        if (refill == null) {
            throw BucketExceptions.nullBandwidthRefill();
        }
        return new Bandwidth(capacity, refill.periodNanos, refill.tokens, capacity, refill.refillIntervally);
    }

    public Bandwidth withInitialTokens(long initialTokens) {
        if (initialTokens < 0L) {
            throw BucketExceptions.nonPositiveInitialTokens(initialTokens);
        }
        return new Bandwidth(this.capacity, this.refillPeriodNanos, this.refillTokens, initialTokens, this.refillIntervally);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Bandwidth{");
        sb.append("capacity=").append(this.capacity);
        sb.append(", initialTokens=").append(this.initialTokens);
        sb.append(", refillPeriodNanos=").append(this.refillPeriodNanos);
        sb.append(", refillTokens=").append(this.refillTokens);
        sb.append(", refillIntervally=").append(this.refillIntervally);
        sb.append('}');
        return sb.toString();
    }
}

