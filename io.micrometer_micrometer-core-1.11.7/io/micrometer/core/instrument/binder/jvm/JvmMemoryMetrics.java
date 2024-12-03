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
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmMemory;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Collections;

@NonNullApi
@NonNullFields
public class JvmMemoryMetrics
implements MeterBinder {
    private final Iterable<Tag> tags;

    public JvmMemoryMetrics() {
        this(Collections.emptyList());
    }

    public JvmMemoryMetrics(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        for (BufferPoolMXBean bufferPoolBean : ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)) {
            Tags tagsWithId = Tags.concat(this.tags, "id", bufferPoolBean.getName());
            Gauge.builder("jvm.buffer.count", bufferPoolBean, BufferPoolMXBean::getCount).tags(tagsWithId).description("An estimate of the number of buffers in the pool").baseUnit("buffers").register(registry);
            Gauge.builder("jvm.buffer.memory.used", bufferPoolBean, BufferPoolMXBean::getMemoryUsed).tags(tagsWithId).description("An estimate of the memory that the Java virtual machine is using for this buffer pool").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.buffer.total.capacity", bufferPoolBean, BufferPoolMXBean::getTotalCapacity).tags(tagsWithId).description("An estimate of the total capacity of the buffers in this pool").baseUnit("bytes").register(registry);
        }
        for (MemoryPoolMXBean memoryPoolBean : ManagementFactory.getPlatformMXBeans(MemoryPoolMXBean.class)) {
            String area = MemoryType.HEAP.equals((Object)memoryPoolBean.getType()) ? "heap" : "nonheap";
            Tags tagsWithId = Tags.concat(this.tags, "id", memoryPoolBean.getName(), "area", area);
            Gauge.builder("jvm.memory.used", memoryPoolBean, mem -> JvmMemory.getUsageValue(mem, MemoryUsage::getUsed)).tags(tagsWithId).description("The amount of used memory").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.memory.committed", memoryPoolBean, mem -> JvmMemory.getUsageValue(mem, MemoryUsage::getCommitted)).tags(tagsWithId).description("The amount of memory in bytes that is committed for the Java virtual machine to use").baseUnit("bytes").register(registry);
            Gauge.builder("jvm.memory.max", memoryPoolBean, mem -> JvmMemory.getUsageValue(mem, MemoryUsage::getMax)).tags(tagsWithId).description("The maximum amount of memory in bytes that can be used for memory management").baseUnit("bytes").register(registry);
        }
    }
}

