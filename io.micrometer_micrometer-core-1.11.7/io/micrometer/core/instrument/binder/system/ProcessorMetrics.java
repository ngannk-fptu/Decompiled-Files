/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument.binder.system;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NonNullApi
@NonNullFields
public class ProcessorMetrics
implements MeterBinder {
    private static final List<String> OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList("com.ibm.lang.management.OperatingSystemMXBean", "com.sun.management.OperatingSystemMXBean");
    private final Iterable<Tag> tags;
    private final OperatingSystemMXBean operatingSystemBean;
    @Nullable
    private final Class<?> operatingSystemBeanClass;
    @Nullable
    private final Method systemCpuUsage;
    @Nullable
    private final Method processCpuUsage;

    public ProcessorMetrics() {
        this(Collections.emptyList());
    }

    public ProcessorMetrics(Iterable<Tag> tags) {
        this.tags = tags;
        this.operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
        this.operatingSystemBeanClass = this.getFirstClassFound(OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        Method getCpuLoad = this.detectMethod("getCpuLoad");
        this.systemCpuUsage = getCpuLoad != null ? getCpuLoad : this.detectMethod("getSystemCpuLoad");
        this.processCpuUsage = this.detectMethod("getProcessCpuLoad");
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Runtime runtime = Runtime.getRuntime();
        Gauge.builder("system.cpu.count", runtime, Runtime::availableProcessors).tags(this.tags).description("The number of processors available to the Java virtual machine").register(registry);
        if (this.operatingSystemBean.getSystemLoadAverage() >= 0.0) {
            Gauge.builder("system.load.average.1m", this.operatingSystemBean, OperatingSystemMXBean::getSystemLoadAverage).tags(this.tags).description("The sum of the number of runnable entities queued to available processors and the number of runnable entities running on the available processors averaged over a period of time").register(registry);
        }
        if (this.systemCpuUsage != null) {
            Gauge.builder("system.cpu.usage", this.operatingSystemBean, x -> this.invoke(this.systemCpuUsage)).tags(this.tags).description("The \"recent cpu usage\" of the system the application is running in").register(registry);
        }
        if (this.processCpuUsage != null) {
            Gauge.builder("process.cpu.usage", this.operatingSystemBean, x -> this.invoke(this.processCpuUsage)).tags(this.tags).description("The \"recent cpu usage\" for the Java Virtual Machine process").register(registry);
        }
    }

    private double invoke(@Nullable Method method) {
        try {
            return method != null ? (Double)method.invoke((Object)this.operatingSystemBean, new Object[0]) : Double.NaN;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Double.NaN;
        }
    }

    @Nullable
    private Method detectMethod(String name) {
        Objects.requireNonNull(name);
        if (this.operatingSystemBeanClass == null) {
            return null;
        }
        try {
            this.operatingSystemBeanClass.cast(this.operatingSystemBean);
            return this.operatingSystemBeanClass.getMethod(name, new Class[0]);
        }
        catch (ClassCastException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    @Nullable
    private Class<?> getFirstClassFound(List<String> classNames) {
        for (String className : classNames) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException classNotFoundException) {
            }
        }
        return null;
    }
}

