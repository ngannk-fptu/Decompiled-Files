/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.cumulative;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.util.TimeUtils;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

public class CumulativeFunctionTimer<T>
extends AbstractMeter
implements FunctionTimer {
    private final WeakReference<T> ref;
    private final ToLongFunction<T> countFunction;
    private final ToDoubleFunction<T> totalTimeFunction;
    private final TimeUnit totalTimeFunctionUnit;
    private final TimeUnit baseTimeUnit;
    private volatile long lastCount;
    private volatile double lastTime;

    public CumulativeFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction, ToDoubleFunction<T> totalTimeFunction, TimeUnit totalTimeFunctionUnit, TimeUnit baseTimeUnit) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.countFunction = countFunction;
        this.totalTimeFunction = totalTimeFunction;
        this.totalTimeFunctionUnit = totalTimeFunctionUnit;
        this.baseTimeUnit = baseTimeUnit;
    }

    @Override
    public double count() {
        double d;
        Object obj2 = this.ref.get();
        if (obj2 != null) {
            this.lastCount = Math.max(this.countFunction.applyAsLong(obj2), 0L);
            d = this.lastCount;
        } else {
            d = this.lastCount;
        }
        return d;
    }

    @Override
    public double totalTime(TimeUnit unit) {
        Object obj2 = this.ref.get();
        if (obj2 != null) {
            this.lastTime = Math.max(TimeUtils.convert(this.totalTimeFunction.applyAsDouble(obj2), this.totalTimeFunctionUnit, this.baseTimeUnit()), 0.0);
        }
        return TimeUtils.convert(this.lastTime, this.baseTimeUnit(), unit);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }

    public Meter.Type type() {
        return Meter.Type.TIMER;
    }
}

