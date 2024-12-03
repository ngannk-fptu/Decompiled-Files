/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class DelayLimiter<C> {
    final SuppressionFactory suppressionFactory;
    final ConcurrentHashMap<C, Suppression<C>> cache = new ConcurrentHashMap();
    final DelayQueue<Suppression<C>> suppressions = new DelayQueue();
    final int cardinality;

    public static Builder newBuilder() {
        return new Builder();
    }

    DelayLimiter(SuppressionFactory suppressionFactory, int cardinality) {
        this.suppressionFactory = suppressionFactory;
        this.cardinality = cardinality;
    }

    public boolean shouldInvoke(C context) {
        this.cleanupExpiredSuppressions();
        if (this.cache.containsKey(context)) {
            return false;
        }
        Suppression<C> suppression = this.suppressionFactory.create(context);
        if (this.cache.putIfAbsent(context, suppression) != null) {
            return false;
        }
        this.suppressions.offer(suppression);
        if (this.suppressions.size() > this.cardinality) {
            this.removeOneSuppression();
        }
        return true;
    }

    void removeOneSuppression() {
        Suppression eldest;
        while ((eldest = (Suppression)this.suppressions.peek()) != null) {
            if (!this.suppressions.remove(eldest)) continue;
            this.cache.remove(eldest.context, eldest);
            break;
        }
    }

    public void invalidate(C context) {
        Suppression<C> suppression = this.cache.remove(context);
        if (suppression != null) {
            this.suppressions.remove(suppression);
        }
    }

    public void clear() {
        this.cache.clear();
        this.suppressions.clear();
    }

    void cleanupExpiredSuppressions() {
        Suppression expiredSuppression;
        while ((expiredSuppression = (Suppression)this.suppressions.poll()) != null) {
            this.cache.remove(expiredSuppression.context, expiredSuppression);
        }
    }

    static final class Suppression<C>
    implements Delayed {
        final SuppressionFactory factory;
        final C context;
        final long expiration;

        Suppression(SuppressionFactory factory, C context, long expiration) {
            this.factory = factory;
            this.context = context;
            this.expiration = expiration;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expiration - this.factory.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.signum(this.expiration - ((Suppression)o).expiration);
        }
    }

    static class SuppressionFactory {
        final long ttlNanos;

        SuppressionFactory(long ttlNanos) {
            this.ttlNanos = ttlNanos;
        }

        long nanoTime() {
            return System.nanoTime();
        }

        <C> Suppression<C> create(C context) {
            return new Suppression<C>(this, context, this.nanoTime() + this.ttlNanos);
        }
    }

    public static final class Builder {
        long ttl = 0L;
        TimeUnit ttlUnit = TimeUnit.MILLISECONDS;
        int cardinality = 0;

        public Builder ttl(long ttl, TimeUnit ttlUnit) {
            if (ttlUnit == null) {
                throw new NullPointerException("ttlUnit == null");
            }
            this.ttl = ttl;
            this.ttlUnit = ttlUnit;
            return this;
        }

        public Builder cardinality(int cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public <C> DelayLimiter<C> build() {
            if (this.ttl <= 0L) {
                throw new IllegalArgumentException("ttl <= 0");
            }
            if (this.cardinality <= 0) {
                throw new IllegalArgumentException("cardinality <= 0");
            }
            return new DelayLimiter(new SuppressionFactory(this.ttlUnit.toNanos(this.ttl)), this.cardinality);
        }

        Builder() {
        }
    }
}

