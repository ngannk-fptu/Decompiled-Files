/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 */
package io.micrometer.core.instrument.binder.jvm;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmMemory;
import io.micrometer.core.instrument.distribution.TimeWindowSum;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

public class JvmHeapPressureMetrics
implements MeterBinder,
AutoCloseable {
    private final Iterable<Tag> tags;
    private final List<Runnable> notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<Runnable>();
    private final long startOfMonitoring = System.nanoTime();
    private final Duration lookback;
    private final TimeWindowSum gcPauseSum;
    private final AtomicReference<Double> lastLongLivedPoolUsageAfterGc = new AtomicReference<Double>(0.0);
    private final Set<String> longLivedPoolNames;

    public JvmHeapPressureMetrics() {
        this(Collections.emptyList(), Duration.ofMinutes(5L), Duration.ofMinutes(1L));
    }

    public JvmHeapPressureMetrics(Iterable<Tag> tags, Duration lookback, Duration testEvery) {
        this.tags = tags;
        this.lookback = lookback;
        this.gcPauseSum = new TimeWindowSum((int)lookback.dividedBy(testEvery.toMillis()).toMillis(), testEvery);
        this.longLivedPoolNames = JvmMemory.getLongLivedHeapPools().map(MemoryPoolMXBean::getName).collect(Collectors.toSet());
        this.monitor();
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        if (!this.longLivedPoolNames.isEmpty()) {
            Gauge.builder("jvm.memory.usage.after.gc", this.lastLongLivedPoolUsageAfterGc, AtomicReference::get).tags(this.tags).tag("area", "heap").tag("pool", "long-lived").description("The percentage of long-lived heap pool used after the last GC event, in the range [0..1]").baseUnit("percent").register(registry);
        }
        Gauge.builder("jvm.gc.overhead", this.gcPauseSum, pauseSum -> {
            double overIntervalMillis = (double)Math.min(System.nanoTime() - this.startOfMonitoring, this.lookback.toNanos()) / 1000000.0;
            return this.gcPauseSum.poll() / overIntervalMillis;
        }).tags(this.tags).description("An approximation of the percent of CPU time used by GC activities over the last lookback period or since monitoring began, whichever is shorter, in the range [0..1]").baseUnit("percent").register(registry);
    }

    private void monitor() {
        for (GarbageCollectorMXBean mbean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!(mbean instanceof NotificationEmitter)) continue;
            NotificationListener notificationListener = (notification, ref) -> {
                CompositeData cd = (CompositeData)notification.getUserData();
                GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);
                String gcCause = notificationInfo.getGcCause();
                GcInfo gcInfo = notificationInfo.getGcInfo();
                long duration = gcInfo.getDuration();
                if (!JvmMemory.isConcurrentPhase(gcCause, notificationInfo.getGcName())) {
                    this.gcPauseSum.record(duration);
                }
                Map<String, MemoryUsage> after = gcInfo.getMemoryUsageAfterGc();
                if (!this.longLivedPoolNames.isEmpty()) {
                    long usedAfter = this.longLivedPoolNames.stream().mapToLong(pool -> ((MemoryUsage)after.get(pool)).getUsed()).sum();
                    double maxAfter = this.longLivedPoolNames.stream().mapToLong(pool -> ((MemoryUsage)after.get(pool)).getMax()).sum();
                    this.lastLongLivedPoolUsageAfterGc.set((double)usedAfter / maxAfter);
                }
            };
            NotificationEmitter notificationEmitter = (NotificationEmitter)((Object)mbean);
            notificationEmitter.addNotificationListener(notificationListener, notification -> notification.getType().equals("com.sun.management.gc.notification"), null);
            this.notificationListenerCleanUpRunnables.add(() -> {
                try {
                    notificationEmitter.removeNotificationListener(notificationListener);
                }
                catch (ListenerNotFoundException listenerNotFoundException) {
                    // empty catch block
                }
            });
        }
    }

    @Override
    public void close() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
    }
}

