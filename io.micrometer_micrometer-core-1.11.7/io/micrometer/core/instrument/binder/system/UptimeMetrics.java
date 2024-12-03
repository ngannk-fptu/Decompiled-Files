/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.system;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@NonNullApi
@NonNullFields
public class UptimeMetrics
implements MeterBinder {
    private final RuntimeMXBean runtimeMXBean;
    private final Iterable<Tag> tags;

    public UptimeMetrics() {
        this(Collections.emptyList());
    }

    public UptimeMetrics(Iterable<Tag> tags) {
        this(ManagementFactory.getRuntimeMXBean(), tags);
    }

    UptimeMetrics(RuntimeMXBean runtimeMXBean, Iterable<Tag> tags) {
        this.runtimeMXBean = runtimeMXBean;
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        TimeGauge.builder("process.uptime", this.runtimeMXBean, TimeUnit.MILLISECONDS, RuntimeMXBean::getUptime).tags(this.tags).description("The uptime of the Java virtual machine").register(registry);
        TimeGauge.builder("process.start.time", this.runtimeMXBean, TimeUnit.MILLISECONDS, RuntimeMXBean::getStartTime).tags(this.tags).description("Start time of the process since unix epoch.").register(registry);
    }
}

