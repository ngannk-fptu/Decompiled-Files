/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.noop.NoopTimeGauge;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

class CompositeTimeGauge<T>
extends AbstractCompositeMeter<TimeGauge>
implements TimeGauge {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;
    private final TimeUnit fUnit;

    CompositeTimeGauge(Meter.Id id, @Nullable T obj, TimeUnit fUnit, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
        this.fUnit = fUnit;
    }

    @Override
    public double value() {
        return ((TimeGauge)this.firstChild()).value();
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return ((TimeGauge)this.firstChild()).baseTimeUnit();
    }

    @Override
    TimeGauge newNoopMeter() {
        return new NoopTimeGauge(this.getId());
    }

    @Override
    TimeGauge registerNewMeter(MeterRegistry registry) {
        Object obj = this.ref.get();
        if (obj == null) {
            return null;
        }
        return TimeGauge.builder(this.getId().getName(), obj, this.fUnit, this.f).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).register(registry);
    }
}

