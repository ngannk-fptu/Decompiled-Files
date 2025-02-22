/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

public class JvmInfoMetrics
implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("jvm.info", () -> 1L).description("JVM version info").tags("version", System.getProperty("java.runtime.version", "unknown"), "vendor", System.getProperty("java.vm.vendor", "unknown"), "runtime", System.getProperty("java.runtime.name", "unknown")).strongReference(true).register(registry);
    }
}

