/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import java.util.EventListener;

public interface MetricRegistryListener
extends EventListener {
    public void onGaugeAdded(String var1, Gauge<?> var2);

    public void onGaugeRemoved(String var1);

    public void onCounterAdded(String var1, Counter var2);

    public void onCounterRemoved(String var1);

    public void onHistogramAdded(String var1, Histogram var2);

    public void onHistogramRemoved(String var1);

    public void onMeterAdded(String var1, Meter var2);

    public void onMeterRemoved(String var1);

    public void onTimerAdded(String var1, Timer var2);

    public void onTimerRemoved(String var1);

    public static abstract class Base
    implements MetricRegistryListener {
        @Override
        public void onGaugeAdded(String name, Gauge<?> gauge) {
        }

        @Override
        public void onGaugeRemoved(String name) {
        }

        @Override
        public void onCounterAdded(String name, Counter counter) {
        }

        @Override
        public void onCounterRemoved(String name) {
        }

        @Override
        public void onHistogramAdded(String name, Histogram histogram) {
        }

        @Override
        public void onHistogramRemoved(String name) {
        }

        @Override
        public void onMeterAdded(String name, Meter meter) {
        }

        @Override
        public void onMeterRemoved(String name) {
        }

        @Override
        public void onTimerAdded(String name, Timer timer) {
        }

        @Override
        public void onTimerRemoved(String name) {
        }
    }
}

