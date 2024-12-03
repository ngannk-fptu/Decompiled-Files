/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.AbstractCompositeMeter;
import io.micrometer.core.instrument.noop.NoopGauge;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;

class CompositeGauge<T>
extends AbstractCompositeMeter<Gauge>
implements Gauge {
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> f;

    CompositeGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> f) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.f = f;
    }

    @Override
    public double value() {
        return ((Gauge)this.firstChild()).value();
    }

    @Override
    Gauge newNoopMeter() {
        return new NoopGauge(this.getId());
    }

    @Override
    Gauge registerNewMeter(MeterRegistry registry) {
        Object obj = this.ref.get();
        if (obj == null) {
            return null;
        }
        return Gauge.builder(this.getId().getName(), obj, this.f).tags(this.getId().getTagsAsIterable()).description(this.getId().getDescription()).baseUnit(this.getId().getBaseUnit()).register(registry);
    }
}

