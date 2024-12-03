/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Snapshot
 *  com.codahale.metrics.Timer
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.core.instrument.dropwizard.DropwizardRate;
import io.micrometer.core.instrument.util.TimeUtils;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class DropwizardFunctionTimer<T>
extends AbstractMeter
implements FunctionTimer {
    private final WeakReference<T> ref;
    private final ToLongFunction<T> countFunction;
    private final ToDoubleFunction<T> totalTimeFunction;
    private final TimeUnit totalTimeFunctionUnit;
    private final AtomicLong lastCount = new AtomicLong();
    private final DropwizardRate rate;
    private final Timer dropwizardMeter;
    private final TimeUnit registryBaseTimeUnit;
    private volatile double lastTime;

    DropwizardFunctionTimer(Meter.Id id, Clock clock, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit registryBaseTimeUnit) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.countFunction = countFunction;
        this.totalTimeFunction = totalTimeFunction;
        this.totalTimeFunctionUnit = totalTimeFunctionUnit;
        this.rate = new DropwizardRate(clock);
        this.registryBaseTimeUnit = registryBaseTimeUnit;
        this.dropwizardMeter = new Timer(null, new DropwizardClock(clock)){

            public double getFifteenMinuteRate() {
                DropwizardFunctionTimer.this.count();
                return DropwizardFunctionTimer.this.rate.getFifteenMinuteRate();
            }

            public double getFiveMinuteRate() {
                DropwizardFunctionTimer.this.count();
                return DropwizardFunctionTimer.this.rate.getFiveMinuteRate();
            }

            public double getOneMinuteRate() {
                DropwizardFunctionTimer.this.count();
                return DropwizardFunctionTimer.this.rate.getOneMinuteRate();
            }

            public long getCount() {
                return (long)DropwizardFunctionTimer.this.count();
            }

            public Snapshot getSnapshot() {
                return new Snapshot(){

                    public double getValue(double quantile) {
                        return quantile == 0.5 ? this.getMean() : 0.0;
                    }

                    public long[] getValues() {
                        return new long[0];
                    }

                    public int size() {
                        return 1;
                    }

                    public long getMax() {
                        return 0L;
                    }

                    public double getMean() {
                        double count = DropwizardFunctionTimer.this.count();
                        return count == 0.0 ? 0.0 : DropwizardFunctionTimer.this.totalTime(TimeUnit.NANOSECONDS) / count;
                    }

                    public long getMin() {
                        return 0L;
                    }

                    public double getStdDev() {
                        return 0.0;
                    }

                    public void dump(OutputStream output) {
                    }
                };
            }
        };
    }

    public Timer getDropwizardMeter() {
        return this.dropwizardMeter;
    }

    @Override
    public double count() {
        Object obj = this.ref.get();
        if (obj == null) {
            return this.lastCount.get();
        }
        return this.lastCount.updateAndGet(prev -> {
            long newCount = this.countFunction.applyAsLong(obj);
            long diff = newCount - prev;
            this.rate.increment(diff);
            return newCount;
        });
    }

    @Override
    public double totalTime(TimeUnit unit) {
        Object obj2 = this.ref.get();
        if (obj2 != null) {
            this.lastTime = TimeUtils.convert(this.totalTimeFunction.applyAsDouble(obj2), this.totalTimeFunctionUnit, this.baseTimeUnit());
        }
        return TimeUtils.convert(this.lastTime, this.baseTimeUnit(), unit);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return this.registryBaseTimeUnit;
    }
}

