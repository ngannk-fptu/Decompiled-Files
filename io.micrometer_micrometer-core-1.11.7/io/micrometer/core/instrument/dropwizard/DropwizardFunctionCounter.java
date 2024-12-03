/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Meter
 */
package io.micrometer.core.instrument.dropwizard;

import com.codahale.metrics.Meter;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.core.instrument.dropwizard.DropwizardRate;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.ToDoubleFunction;

public class DropwizardFunctionCounter<T>
extends AbstractMeter
implements FunctionCounter {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;
    private final AtomicLong last = new AtomicLong();
    private final DropwizardRate rate;
    private final Meter dropwizardMeter;

    DropwizardFunctionCounter(Meter.Id id, Clock clock, T obj, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
        this.rate = new DropwizardRate(clock);
        this.dropwizardMeter = new Meter(new DropwizardClock(clock)){

            public double getFifteenMinuteRate() {
                DropwizardFunctionCounter.this.count();
                return DropwizardFunctionCounter.this.rate.getFifteenMinuteRate();
            }

            public double getFiveMinuteRate() {
                DropwizardFunctionCounter.this.count();
                return DropwizardFunctionCounter.this.rate.getFiveMinuteRate();
            }

            public double getOneMinuteRate() {
                DropwizardFunctionCounter.this.count();
                return DropwizardFunctionCounter.this.rate.getOneMinuteRate();
            }

            public long getCount() {
                return (long)DropwizardFunctionCounter.this.count();
            }
        };
    }

    public Meter getDropwizardMeter() {
        return this.dropwizardMeter;
    }

    @Override
    public double count() {
        Object obj = this.ref.get();
        if (obj == null) {
            return this.last.get();
        }
        return this.last.updateAndGet(prev -> {
            long newCount = (long)this.f.applyAsDouble(obj);
            long diff = newCount - prev;
            this.rate.increment(diff);
            return newCount;
        });
    }
}

