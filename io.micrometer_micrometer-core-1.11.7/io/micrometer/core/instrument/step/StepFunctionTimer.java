/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepMeter;
import io.micrometer.core.instrument.step.StepTuple2;
import io.micrometer.core.instrument.util.TimeUtils;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class StepFunctionTimer<T>
implements FunctionTimer,
StepMeter {
    private final Meter.Id id;
    private final WeakReference<T> ref;
    private final ToLongFunction<T> countFunction;
    private final ToDoubleFunction<T> totalTimeFunction;
    private final TimeUnit totalTimeFunctionUnit;
    private final TimeUnit baseTimeUnit;
    private final Clock clock;
    private volatile long lastUpdateTime = -2000000L;
    private volatile long lastCount;
    private volatile double lastTime;
    private final LongAdder count = new LongAdder();
    private final DoubleAdder total = new DoubleAdder();
    private final StepTuple2<Long, Double> countTotal;

    public StepFunctionTimer(Meter.Id id, Clock clock, long stepMillis, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit baseTimeUnit) {
        this.id = id;
        this.clock = clock;
        this.ref = new WeakReference<T>(obj);
        this.countFunction = countFunction;
        this.totalTimeFunction = totalTimeFunction;
        this.totalTimeFunctionUnit = totalTimeFunctionUnit;
        this.baseTimeUnit = baseTimeUnit;
        this.countTotal = new StepTuple2<Long, Double>(clock, stepMillis, 0L, 0.0, this.count::sumThenReset, this.total::sumThenReset);
    }

    @Override
    public double count() {
        this.accumulateCountAndTotal();
        return this.countTotal.poll1().longValue();
    }

    @Override
    public double totalTime(TimeUnit unit) {
        this.accumulateCountAndTotal();
        return TimeUtils.convert(this.countTotal.poll2(), this.baseTimeUnit(), unit);
    }

    private void accumulateCountAndTotal() {
        Object obj2 = this.ref.get();
        if (obj2 != null && (double)(this.clock.monotonicTime() - this.lastUpdateTime) > 1000000.0) {
            long prevLastCount = this.lastCount;
            this.lastCount = Math.max(this.countFunction.applyAsLong(obj2), 0L);
            this.count.add(this.lastCount - prevLastCount);
            double prevLastTime = this.lastTime;
            this.lastTime = Math.max(TimeUtils.convert(this.totalTimeFunction.applyAsDouble(obj2), this.totalTimeFunctionUnit, this.baseTimeUnit()), 0.0);
            this.total.add(this.lastTime - prevLastTime);
            this.lastUpdateTime = this.clock.monotonicTime();
        }
    }

    @Override
    public Meter.Id getId() {
        return this.id;
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }

    public Meter.Type type() {
        return Meter.Type.TIMER;
    }

    @Override
    public void _closingRollover() {
        this.accumulateCountAndTotal();
        this.countTotal._closingRollover();
    }
}

