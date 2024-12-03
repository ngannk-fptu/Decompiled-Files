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

@NonNullApi
@NonNullFields
public class FileDescriptorMetrics
implements MeterBinder {
    private static final List<String> UNIX_OPERATING_SYSTEM_BEAN_CLASS_NAMES = Arrays.asList("com.sun.management.UnixOperatingSystemMXBean", "com.ibm.lang.management.UnixOperatingSystemMXBean");
    private final OperatingSystemMXBean osBean;
    private final Iterable<Tag> tags;
    @Nullable
    private final Class<?> osBeanClass;
    @Nullable
    private final Method openFilesMethod;
    @Nullable
    private final Method maxFilesMethod;

    public FileDescriptorMetrics() {
        this(Collections.emptyList());
    }

    public FileDescriptorMetrics(Iterable<Tag> tags) {
        this(ManagementFactory.getOperatingSystemMXBean(), tags);
    }

    FileDescriptorMetrics(OperatingSystemMXBean osBean, Iterable<Tag> tags) {
        this.osBean = osBean;
        this.tags = tags;
        this.osBeanClass = this.getFirstClassFound(UNIX_OPERATING_SYSTEM_BEAN_CLASS_NAMES);
        this.openFilesMethod = this.detectMethod("getOpenFileDescriptorCount");
        this.maxFilesMethod = this.detectMethod("getMaxFileDescriptorCount");
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (this.openFilesMethod != null) {
            Gauge.builder("process.files.open", this.osBean, x -> this.invoke(this.openFilesMethod)).tags(this.tags).description("The open file descriptor count").baseUnit("files").register(registry);
        }
        if (this.maxFilesMethod != null) {
            Gauge.builder("process.files.max", this.osBean, x -> this.invoke(this.maxFilesMethod)).tags(this.tags).description("The maximum file descriptor count").baseUnit("files").register(registry);
        }
    }

    private double invoke(@Nullable Method method) {
        try {
            return method != null ? (double)((Long)method.invoke((Object)this.osBean, new Object[0])).longValue() : Double.NaN;
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return Double.NaN;
        }
    }

    @Nullable
    private Method detectMethod(String name) {
        if (this.osBeanClass == null) {
            return null;
        }
        try {
            this.osBeanClass.cast(this.osBean);
            return this.osBeanClass.getDeclaredMethod(name, new Class[0]);
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

