/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 */
package io.micrometer.core.instrument.binder.jvm;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmMemory;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;

@NonNullApi
@NonNullFields
public class JvmGcMetrics
implements MeterBinder,
AutoCloseable {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(JvmGcMetrics.class);
    private final boolean managementExtensionsPresent = JvmGcMetrics.isManagementExtensionsPresent();
    final boolean isGenerationalGc = this.isGenerationalGcConfigured();
    private final Iterable<Tag> tags;
    @Nullable
    private String allocationPoolName;
    private final Set<String> longLivedPoolNames = new HashSet<String>();
    private final List<Runnable> notificationListenerCleanUpRunnables = new CopyOnWriteArrayList<Runnable>();
    private Counter allocatedBytes;
    @Nullable
    private Counter promotedBytes;
    private AtomicLong allocationPoolSizeAfter;
    private AtomicLong liveDataSize;
    private AtomicLong maxDataSize;
    GcMetricsNotificationListener gcNotificationListener;

    public JvmGcMetrics() {
        this(Collections.emptyList());
    }

    public JvmGcMetrics(Iterable<Tag> tags) {
        for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            String name = mbean.getName();
            if (JvmMemory.isAllocationPool(name)) {
                this.allocationPoolName = name;
            }
            if (!JvmMemory.isLongLivedPool(name)) continue;
            this.longLivedPoolNames.add(name);
        }
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (!this.managementExtensionsPresent) {
            return;
        }
        this.gcNotificationListener = new GcMetricsNotificationListener(registry);
        double maxLongLivedPoolBytes = JvmMemory.getLongLivedHeapPools().mapToDouble(mem -> JvmMemory.getUsageValue(mem, MemoryUsage::getMax)).sum();
        this.maxDataSize = new AtomicLong((long)maxLongLivedPoolBytes);
        Gauge.builder("jvm.gc.max.data.size", this.maxDataSize, AtomicLong::get).tags(this.tags).description("Max size of long-lived heap memory pool").baseUnit("bytes").register(registry);
        this.liveDataSize = new AtomicLong();
        Gauge.builder("jvm.gc.live.data.size", this.liveDataSize, AtomicLong::get).tags(this.tags).description("Size of long-lived heap memory pool after reclamation").baseUnit("bytes").register(registry);
        this.allocatedBytes = Counter.builder("jvm.gc.memory.allocated").tags(this.tags).baseUnit("bytes").description("Incremented for an increase in the size of the (young) heap memory pool after one GC to before the next").register(registry);
        this.promotedBytes = this.isGenerationalGc ? Counter.builder("jvm.gc.memory.promoted").tags(this.tags).baseUnit("bytes").description("Count of positive increases in the size of the old generation memory pool before GC to after GC").register(registry) : null;
        this.allocationPoolSizeAfter = new AtomicLong(0L);
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (!(gcBean instanceof NotificationEmitter)) continue;
            NotificationEmitter notificationEmitter = (NotificationEmitter)((Object)gcBean);
            notificationEmitter.addNotificationListener(this.gcNotificationListener, notification -> notification.getType().equals("com.sun.management.gc.notification"), null);
            this.notificationListenerCleanUpRunnables.add(() -> {
                try {
                    notificationEmitter.removeNotificationListener(this.gcNotificationListener);
                }
                catch (ListenerNotFoundException listenerNotFoundException) {
                    // empty catch block
                }
            });
        }
    }

    private boolean isGenerationalGcConfigured() {
        int nonTenuredPools = 0;
        for (MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
            if (!JvmMemory.isHeap(bean)) continue;
            String name = bean.getName();
            if (!name.contains("tenured") && ++nonTenuredPools == 2) {
                return true;
            }
            if (!name.contains("GPGC")) continue;
            return true;
        }
        return false;
    }

    private static boolean isManagementExtensionsPresent() {
        if (ManagementFactory.getMemoryPoolMXBeans().isEmpty()) {
            log.warn("GC notifications will not be available because MemoryPoolMXBeans are not provided by the JVM");
            return false;
        }
        try {
            Class.forName("com.sun.management.GarbageCollectionNotificationInfo", false, MemoryPoolMXBean.class.getClassLoader());
            return true;
        }
        catch (Throwable e) {
            log.warn("GC notifications will not be available because com.sun.management.GarbageCollectionNotificationInfo is not present");
            return false;
        }
    }

    @Override
    public void close() {
        this.notificationListenerCleanUpRunnables.forEach(Runnable::run);
    }

    class GcMetricsNotificationListener
    implements NotificationListener {
        private final MeterRegistry registry;

        GcMetricsNotificationListener(MeterRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void handleNotification(Notification notification, Object ref) {
            long delta;
            CompositeData cd = (CompositeData)notification.getUserData();
            GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from(cd);
            String gcName = notificationInfo.getGcName();
            String gcCause = notificationInfo.getGcCause();
            String gcAction = notificationInfo.getGcAction();
            GcInfo gcInfo = notificationInfo.getGcInfo();
            long duration = gcInfo.getDuration();
            Tags gcTags = Tags.of("gc", gcName, "action", gcAction, "cause", gcCause).and(JvmGcMetrics.this.tags);
            if (JvmMemory.isConcurrentPhase(gcCause, gcName)) {
                ((Timer.Builder)Timer.builder("jvm.gc.concurrent.phase.time").tags((Iterable)gcTags)).description("Time spent in concurrent phase").register(this.registry).record(duration, TimeUnit.MILLISECONDS);
            } else {
                ((Timer.Builder)Timer.builder("jvm.gc.pause").tags((Iterable)gcTags)).description("Time spent in GC pause").register(this.registry).record(duration, TimeUnit.MILLISECONDS);
            }
            Map<String, MemoryUsage> before = gcInfo.getMemoryUsageBeforeGc();
            Map<String, MemoryUsage> after = gcInfo.getMemoryUsageAfterGc();
            this.countPoolSizeDelta(before, after);
            long longLivedBefore = JvmGcMetrics.this.longLivedPoolNames.stream().mapToLong(pool -> ((MemoryUsage)before.get(pool)).getUsed()).sum();
            long longLivedAfter = JvmGcMetrics.this.longLivedPoolNames.stream().mapToLong(pool -> ((MemoryUsage)after.get(pool)).getUsed()).sum();
            if (JvmGcMetrics.this.isGenerationalGc && (delta = longLivedAfter - longLivedBefore) > 0L) {
                JvmGcMetrics.this.promotedBytes.increment(delta);
            }
            if (longLivedAfter < longLivedBefore || this.shouldUpdateDataSizeMetrics(gcName)) {
                JvmGcMetrics.this.liveDataSize.set(longLivedAfter);
                JvmGcMetrics.this.maxDataSize.set(JvmGcMetrics.this.longLivedPoolNames.stream().mapToLong(pool -> ((MemoryUsage)after.get(pool)).getMax()).sum());
            }
        }

        private void countPoolSizeDelta(Map<String, MemoryUsage> before, Map<String, MemoryUsage> after) {
            if (JvmGcMetrics.this.allocationPoolName == null) {
                return;
            }
            long beforeBytes = before.get(JvmGcMetrics.this.allocationPoolName).getUsed();
            long afterBytes = after.get(JvmGcMetrics.this.allocationPoolName).getUsed();
            long delta = beforeBytes - JvmGcMetrics.this.allocationPoolSizeAfter.get();
            JvmGcMetrics.this.allocationPoolSizeAfter.set(afterBytes);
            if (delta > 0L) {
                JvmGcMetrics.this.allocatedBytes.increment(delta);
            }
        }

        private boolean shouldUpdateDataSizeMetrics(String gcName) {
            return this.nonGenerationalGcShouldUpdateDataSize(gcName) || this.isMajorGenerationalGc(gcName);
        }

        private boolean isMajorGenerationalGc(String gcName) {
            return GcGenerationAge.fromGcName(gcName) == GcGenerationAge.OLD;
        }

        private boolean nonGenerationalGcShouldUpdateDataSize(String gcName) {
            return !JvmGcMetrics.this.isGenerationalGc && !gcName.endsWith("Pauses");
        }
    }

    @NonNullApi
    static enum GcGenerationAge {
        OLD,
        YOUNG,
        UNKNOWN;

        private static final Map<String, GcGenerationAge> knownCollectors;

        static GcGenerationAge fromGcName(String gcName) {
            return knownCollectors.getOrDefault(gcName, UNKNOWN);
        }

        static {
            knownCollectors = new HashMap<String, GcGenerationAge>(){
                {
                    this.put("ConcurrentMarkSweep", OLD);
                    this.put("Copy", YOUNG);
                    this.put("G1 Old Generation", OLD);
                    this.put("G1 Young Generation", YOUNG);
                    this.put("MarkSweepCompact", OLD);
                    this.put("PS MarkSweep", OLD);
                    this.put("PS Scavenge", YOUNG);
                    this.put("ParNew", YOUNG);
                    this.put("global", OLD);
                    this.put("scavenge", YOUNG);
                    this.put("partial gc", YOUNG);
                    this.put("global garbage collect", OLD);
                    this.put("Epsilon", OLD);
                    this.put("GPGC New", YOUNG);
                    this.put("GPGC Old", OLD);
                    this.put("GPGC New Cycles", YOUNG);
                    this.put("GPGC Old Cycles", OLD);
                    this.put("GPGC New Pauses", YOUNG);
                    this.put("GPGC Old Pauses", OLD);
                }
            };
        }
    }
}

