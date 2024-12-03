/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDump
 *  com.atlassian.diagnostics.internal.detail.SimpleThreadDump
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.ConfluenceMonitor;
import com.atlassian.confluence.internal.diagnostics.EventListeningDarkFeatureSetting;
import com.atlassian.confluence.internal.diagnostics.GarbageCollectorTimeAnalyticsEvent;
import com.atlassian.confluence.internal.diagnostics.SlidingWindowSeries;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDump;
import com.atlassian.diagnostics.internal.detail.SimpleThreadDump;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class JavaMemoryMonitor
extends ConfluenceMonitor {
    private static final int JVM_MEMORY_CHECK_SECS = Integer.getInteger("diagnostics.jvm.memory.check.period.secs", 10);
    static final long MAX_MEMORY_ALLOCATION_RATE_PERCENT = Integer.getInteger("diagnostics.jvm.memory.allocation.rate.percent", 5).intValue();
    static final long MEMORY_ALLOCATION_MONITORING_PERIOD = Integer.getInteger("diagnostics.jvm.memory.allocation.monitoring.period.secs", 20).intValue();
    static final long MAX_GARBAGE_COLLECTOR_PERCENT = Integer.getInteger("diagnostics.jvm.garbage.collector.percent", 10).intValue();
    static final long GARBAGE_COLLECTOR_MONITORING_PERIOD = Integer.getInteger("diagnostics.jvm.garbage.collector.monitoring.period.secs", 20).intValue();
    static final long INITIAL_ALLOCATED_MEMORY = Integer.getInteger("diagnostics.jvm.initial.memory.allocation.gigabytes", 32).intValue();
    private static final int MEMORY_ALLOCATION_RATE_EXCEEDS_LIMIT_ID = 1001;
    private static final int GARBAGE_COLLECTOR_TIME_EXCEEDS_LIMIT_ID = 1002;
    private static final Logger logger = LoggerFactory.getLogger(JavaMemoryMonitor.class);
    private static final String MONITOR_ID = "JVM";
    private final Supplier<Long> timeSource;
    private final long maxAllocationRatePercent;
    private final long maxGarbageCollectorPercent;
    private final long initialAllocatedMemory;
    private final ThreadMXBean threadMXBean;
    private final ThreadDumpProducer threadDumpProducer;
    private final Map<Long, SlidingWindowSeries> windowThreadMemoryAllocation;
    private final SlidingWindowSeries garbageCollectorDurations;
    private final Map<Long, Long> totalThreadMemoryAllocation;
    private final AtomicBoolean startUpInProgress;
    private final EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled;
    private final EventPublisher eventPublisher;

    public JavaMemoryMonitor(EventPublisher eventPublisher, EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled) {
        this(eventPublisher, riskyDiagnosticMonitorsEnabled, System::nanoTime, (ThreadMXBean)ManagementFactory.getThreadMXBean(), MAX_MEMORY_ALLOCATION_RATE_PERCENT, MAX_GARBAGE_COLLECTOR_PERCENT, INITIAL_ALLOCATED_MEMORY * 1024L * 1024L * 1024L);
        this.startMonitorThread();
    }

    @VisibleForTesting
    JavaMemoryMonitor(EventPublisher eventPublisher, EventListeningDarkFeatureSetting riskyDiagnosticMonitorsEnabled, Supplier<Long> timeSource, ThreadMXBean threadMXBean, long maxAllocationRatePercent, long maxGarbageCollectorPercent, long initialAllocatedMemory) {
        this.eventPublisher = eventPublisher;
        this.riskyDiagnosticMonitorsEnabled = riskyDiagnosticMonitorsEnabled;
        this.timeSource = timeSource;
        this.threadMXBean = threadMXBean;
        this.threadMXBean.setThreadAllocatedMemoryEnabled(true);
        this.threadDumpProducer = new ThreadDumpProducer(threadMXBean);
        this.windowThreadMemoryAllocation = new ConcurrentHashMap<Long, SlidingWindowSeries>();
        this.totalThreadMemoryAllocation = new ConcurrentHashMap<Long, Long>();
        this.maxAllocationRatePercent = maxAllocationRatePercent;
        this.maxGarbageCollectorPercent = maxGarbageCollectorPercent;
        this.initialAllocatedMemory = initialAllocatedMemory;
        this.garbageCollectorDurations = new SlidingWindowSeries(timeSource, Duration.ofSeconds(GARBAGE_COLLECTOR_MONITORING_PERIOD));
        this.startUpInProgress = new AtomicBoolean(true);
    }

    @Override
    public void init(MonitoringService monitoringService) {
        super.init(monitoringService);
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.jvm.name", () -> true);
        this.defineIssue("diagnostics.jvm.issue", 1001, Severity.INFO);
        this.defineIssue("diagnostics.jvm.issue", 1002, Severity.WARNING);
        logger.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @Override
    protected String getMonitorId() {
        return MONITOR_ID;
    }

    @VisibleForTesting
    void updateThreadAllocatedMemory() {
        long[] threadIds = this.threadMXBean.getAllThreadIds();
        long[] allocated = this.threadMXBean.getThreadAllocatedBytes(threadIds);
        if (this.startUpInProgress.get()) {
            for (int i = 0; i < threadIds.length; ++i) {
                this.totalThreadMemoryAllocation.put(threadIds[i], allocated[i]);
            }
            long totalAllocatedMemory = 0L;
            for (long amount : this.totalThreadMemoryAllocation.values()) {
                totalAllocatedMemory += amount;
            }
            logger.debug("total allocated memory = {}, limit = {}", (Object)totalAllocatedMemory, (Object)this.initialAllocatedMemory);
            if (totalAllocatedMemory > this.initialAllocatedMemory) {
                this.totalThreadMemoryAllocation.clear();
                this.startUpInProgress.set(false);
                logger.debug("start thread allocated memory rate monitoring");
            }
        }
        if (!this.startUpInProgress.get()) {
            for (int i = 0; i < threadIds.length; ++i) {
                if (!this.windowThreadMemoryAllocation.containsKey(threadIds[i])) {
                    this.windowThreadMemoryAllocation.put(threadIds[i], new SlidingWindowSeries(this.timeSource, Duration.ofSeconds(MEMORY_ALLOCATION_MONITORING_PERIOD)));
                }
                SlidingWindowSeries series = this.windowThreadMemoryAllocation.get(threadIds[i]);
                series.add(allocated[i]);
            }
        }
    }

    void updateGarbageCollectorDuration(Duration duration) {
        this.garbageCollectorDurations.add(duration.toMillis());
    }

    @VisibleForTesting
    void checkThreadAllocatedMemory() {
        this.forEachThread(x -> {
            Double rate = (double)x.getAmount() / (double)x.getDuration() * 1.0E9;
            long allocationRate = rate.longValue();
            long maxAllocationRate = Runtime.getRuntime().totalMemory() * this.maxAllocationRatePercent / 100L;
            logger.debug("thread {} allocation rate = {}, max = {}", new Object[]{x.getThreadInfo().getThreadName(), allocationRate, maxAllocationRate});
            if (allocationRate > maxAllocationRate) {
                this.windowThreadMemoryAllocation.remove(x.getThreadInfo().getThreadId());
                this.alertThreadMemoryAllocation(x.getThreadInfo(), allocationRate, maxAllocationRate);
            }
        });
    }

    @VisibleForTesting
    void checkGarbageCollectorDuration() {
        long total = this.garbageCollectorDurations.sumValue();
        Double garbageCollectorPercent = (double)total / (double)Duration.ofSeconds(GARBAGE_COLLECTOR_MONITORING_PERIOD).toMillis() * 100.0;
        logger.debug("gc time {} ms, duration {} ms", (Object)total, (Object)Duration.ofSeconds(GARBAGE_COLLECTOR_MONITORING_PERIOD).toMillis());
        if (garbageCollectorPercent > (double)this.maxGarbageCollectorPercent) {
            this.alertGarbageCollectorDuration(Duration.ofMillis(total), Duration.ofSeconds(GARBAGE_COLLECTOR_MONITORING_PERIOD), this.maxGarbageCollectorPercent);
            this.garbageCollectorDurations.clear();
        } else {
            this.garbageCollectorDurations.removeOldElements();
        }
    }

    private void forEachThread(Consumer<MemoryAllocation> consumer) {
        Iterator<Map.Entry<Long, SlidingWindowSeries>> iterator = this.windowThreadMemoryAllocation.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SlidingWindowSeries> entry = iterator.next();
            ThreadInfo threadInfo = this.threadMXBean.getThreadInfo(entry.getKey());
            if (threadInfo == null) {
                iterator.remove();
                continue;
            }
            long duration = entry.getValue().getWindow();
            long amount = entry.getValue().getDistance();
            if (amount == 0L) continue;
            consumer.accept(new MemoryAllocation(threadInfo, amount, duration));
        }
    }

    private void alertThreadMemoryAllocation(ThreadInfo info, long allocationRate, long maxAllocationRate) {
        this.alert(1001, builder -> builder.timestamp(Instant.now()).details(() -> ImmutableMap.builder().put((Object)"threadId", (Object)info.getThreadId()).put((Object)"threadName", (Object)info.getThreadName()).put((Object)"threadState", (Object)info.getThreadState()).put((Object)"windowInSecs", (Object)MEMORY_ALLOCATION_MONITORING_PERIOD).put((Object)"allocatedMemoryInMegabytesPerSeconds", (Object)(allocationRate / 0x100000L)).put((Object)"allocatedMemoryLimitInMegabytesPerSeconds", (Object)(maxAllocationRate / 0x100000L)).put((Object)"threadDump", this.threadDumpProducer.produce(new long[]{info.getThreadId()})).build()));
    }

    private void alertGarbageCollectorDuration(Duration duration, Duration window, long limit) {
        this.alert(1002, builder -> {
            ArrayList allocations = new ArrayList();
            this.forEachThread(allocations::add);
            MemoryAllocation[] top5 = (MemoryAllocation[])allocations.stream().sorted((a, b) -> Long.compare(b.getAmount(), a.getAmount())).limit(5L).toArray(MemoryAllocation[]::new);
            builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"durationInMillis", (Object)duration.toMillis(), (Object)"windowInMillis", (Object)window.toMillis(), (Object)"limitPercent", (Object)limit, (Object)"threadMemoryAllocations", (Object)Stream.of(top5).map(MemoryAllocation::toString).collect(Collectors.joining("\n")), (Object)"threadDump", this.threadDumpProducer.produce(Stream.of(top5).mapToLong(x -> x.getThreadInfo().getThreadId()).toArray())));
            this.eventPublisher.publish((Object)new GarbageCollectorTimeAnalyticsEvent(duration.toMillis(), window.toMillis(), limit));
        });
    }

    private void startMonitorThread() {
        this.startMonitorThread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(JVM_MEMORY_CHECK_SECS);
                    this.checkGarbageCollectorDuration();
                    if (!this.riskyDiagnosticMonitorsEnabled.isEnabled()) continue;
                    this.updateThreadAllocatedMemory();
                    this.checkThreadAllocatedMemory();
                }
            }
            catch (InterruptedException e) {
                return;
            }
        }, "diagnostics-jvm-thread");
    }

    private static class MemoryAllocation {
        private final ThreadInfo threadInfo;
        private final long amount;
        private final long duration;

        MemoryAllocation(ThreadInfo threadInfo, long amount, long duration) {
            this.threadInfo = threadInfo;
            this.amount = amount;
            this.duration = duration;
        }

        long getDuration() {
            return this.duration;
        }

        long getAmount() {
            return this.amount;
        }

        ThreadInfo getThreadInfo() {
            return this.threadInfo;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            sb.append("threadId=").append(this.threadInfo.getThreadId());
            sb.append(", amountInMegabytes=").append(this.amount / 1024L / 1024L);
            sb.append(", durationInSeconds=").append(Duration.ofNanos(this.duration).getSeconds());
            sb.append(", threadName=").append(this.threadInfo.getThreadName());
            sb.append('}');
            return sb.toString();
        }
    }

    private static class ThreadDumpProducer {
        private static final int MAX_STACK_LENGTH = 128000;
        private final ThreadMXBean threadMXBean;

        ThreadDumpProducer(ThreadMXBean threadMXBean) {
            this.threadMXBean = threadMXBean;
        }

        List<ThreadDump> produce(long[] threadIds) {
            return Arrays.stream(this.threadMXBean.getThreadInfo(threadIds, 4000)).map(threadInfo -> new SimpleThreadDump(threadInfo.getThreadId(), threadInfo.getThreadName(), false, threadInfo.getThreadState().name(), this.toStackTraceString(Arrays.asList(threadInfo.getStackTrace())))).collect(Collectors.toList());
        }

        private String toStackTraceString(List<StackTraceElement> elements) {
            if (elements.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            elements.stream().map(Object::toString).forEach(elementLine -> {
                if (sb.length() < 128000) {
                    sb.append((String)elementLine);
                    if (sb.length() < 128000) {
                        sb.append('\n');
                    }
                }
            });
            return StringUtils.trimToNull((String)sb.toString());
        }
    }
}

