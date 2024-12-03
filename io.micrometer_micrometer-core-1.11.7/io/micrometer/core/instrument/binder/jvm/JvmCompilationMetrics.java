/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.jvm;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;

@NonNullApi
@NonNullFields
public class JvmCompilationMetrics
implements MeterBinder {
    private final Iterable<Tag> tags;

    public JvmCompilationMetrics() {
        this(Collections.emptyList());
    }

    public JvmCompilationMetrics(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        CompilationMXBean compilationBean = ManagementFactory.getCompilationMXBean();
        if (compilationBean != null && compilationBean.isCompilationTimeMonitoringSupported()) {
            FunctionCounter.builder("jvm.compilation.time", compilationBean, CompilationMXBean::getTotalCompilationTime).tags(Tags.concat(this.tags, "compiler", compilationBean.getName())).description("The approximate accumulated elapsed time spent in compilation").baseUnit("ms").register(registry);
        }
    }
}

