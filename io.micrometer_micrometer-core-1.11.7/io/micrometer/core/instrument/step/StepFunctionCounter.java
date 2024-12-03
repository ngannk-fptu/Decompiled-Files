/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepDouble;
import io.micrometer.core.instrument.step.StepMeter;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;

public class StepFunctionCounter<T>
extends AbstractMeter
implements FunctionCounter,
StepMeter {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;
    private volatile double last;
    private StepDouble count;

    public StepFunctionCounter(Meter.Id id, Clock clock, long stepMillis, T obj, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
        this.count = new StepDouble(clock, stepMillis);
    }

    @Override
    public double count() {
        Object obj2 = this.ref.get();
        if (obj2 != null) {
            double prevLast = this.last;
            this.last = this.f.applyAsDouble(obj2);
            this.count.getCurrent().add(this.last - prevLast);
        }
        return (Double)this.count.poll();
    }

    @Override
    public void _closingRollover() {
        this.count();
        this.count._closingRollover();
    }
}

