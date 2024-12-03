/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.WarnThenDebugLogger
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.WarnThenDebugLogger;
import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import java.lang.ref.WeakReference;
import java.util.function.ToDoubleFunction;

public class DefaultGauge<T>
extends AbstractMeter
implements Gauge {
    private static final WarnThenDebugLogger logger = new WarnThenDebugLogger(DefaultGauge.class);
    private final WeakReference<T> ref;
    private final ToDoubleFunction<T> value;

    public DefaultGauge(Meter.Id id, @Nullable T obj, ToDoubleFunction<T> value) {
        super(id);
        this.ref = new WeakReference<T>(obj);
        this.value = value;
    }

    @Override
    public double value() {
        Object obj = this.ref.get();
        if (obj != null) {
            try {
                return this.value.applyAsDouble(obj);
            }
            catch (Throwable ex) {
                logger.log(() -> "Failed to apply the value function for the gauge '" + this.getId().getName() + "'.", ex);
            }
        }
        return Double.NaN;
    }
}

