/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument;

import io.micrometer.core.instrument.Statistic;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class Measurement {
    private final DoubleSupplier f;
    private final Statistic statistic;

    public Measurement(DoubleSupplier valueFunction, Statistic statistic) {
        this.f = valueFunction;
        this.statistic = statistic;
    }

    public Measurement(Supplier<Double> valueFunction, Statistic statistic) {
        this.f = valueFunction::get;
        this.statistic = statistic;
    }

    public double getValue() {
        return this.f.getAsDouble();
    }

    public Statistic getStatistic() {
        return this.statistic;
    }

    public String toString() {
        return "Measurement{statistic='" + (Object)((Object)this.statistic) + '\'' + ", value=" + this.getValue() + '}';
    }
}

