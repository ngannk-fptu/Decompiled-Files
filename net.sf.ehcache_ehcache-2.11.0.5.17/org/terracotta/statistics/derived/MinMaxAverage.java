/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.derived;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.observer.ChainedEventObserver;
import org.terracotta.statistics.util.InThreadExecutor;

public class MinMaxAverage
implements ChainedEventObserver {
    private final AtomicLong maximum = new AtomicLong(Long.MIN_VALUE);
    private final AtomicLong minimum = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong summation = new AtomicLong(Double.doubleToLongBits(0.0));
    private final AtomicLong count = new AtomicLong(0L);
    private final Executor executor;

    public MinMaxAverage() {
        this(InThreadExecutor.INSTANCE);
    }

    public MinMaxAverage(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void event(long time, final long ... parameters) {
        this.executor.execute(new Runnable(){

            @Override
            public void run() {
                long max = MinMaxAverage.this.maximum.get();
                while (max < parameters[0] && !MinMaxAverage.this.maximum.compareAndSet(max, parameters[0])) {
                    max = MinMaxAverage.this.maximum.get();
                }
                long min = MinMaxAverage.this.minimum.get();
                while (min > parameters[0] && !MinMaxAverage.this.minimum.compareAndSet(min, parameters[0])) {
                    min = MinMaxAverage.this.minimum.get();
                }
                long sumBits = MinMaxAverage.this.summation.get();
                while (!MinMaxAverage.this.summation.compareAndSet(sumBits, Double.doubleToLongBits(Double.longBitsToDouble(sumBits) + (double)parameters[0]))) {
                    sumBits = MinMaxAverage.this.summation.get();
                }
                MinMaxAverage.this.count.incrementAndGet();
            }
        });
    }

    public Long min() {
        if (this.count.get() == 0L) {
            return null;
        }
        return this.minimum.get();
    }

    public ValueStatistic<Long> minStatistic() {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return MinMaxAverage.this.min();
            }
        };
    }

    public Double mean() {
        if (this.count.get() == 0L) {
            return null;
        }
        return Double.longBitsToDouble(this.summation.get()) / (double)this.count.get();
    }

    public ValueStatistic<Double> meanStatistic() {
        return new ValueStatistic<Double>(){

            @Override
            public Double value() {
                return MinMaxAverage.this.mean();
            }
        };
    }

    public Long max() {
        if (this.count.get() == 0L) {
            return null;
        }
        return this.maximum.get();
    }

    public ValueStatistic<Long> maxStatistic() {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return MinMaxAverage.this.max();
            }
        };
    }
}

