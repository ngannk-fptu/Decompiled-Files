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
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collections;

@NonNullApi
@NonNullFields
public class ClassLoaderMetrics
implements MeterBinder {
    private final Iterable<Tag> tags;

    public ClassLoaderMetrics() {
        this(Collections.emptyList());
    }

    public ClassLoaderMetrics(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
        Gauge.builder("jvm.classes.loaded", classLoadingBean, ClassLoadingMXBean::getLoadedClassCount).tags(this.tags).description("The number of classes that are currently loaded in the Java virtual machine").baseUnit("classes").register(registry);
        FunctionCounter.builder("jvm.classes.unloaded", classLoadingBean, ClassLoadingMXBean::getUnloadedClassCount).tags(this.tags).description("The total number of classes unloaded since the Java virtual machine has started execution").baseUnit("classes").register(registry);
    }
}

