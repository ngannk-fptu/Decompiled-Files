/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.DataSize
 *  com.atlassian.dc.filestore.api.FileStore
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  oshi.SystemInfo
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.confluence.internal.diagnostics.ConfluenceMonitor;
import com.atlassian.confluence.internal.diagnostics.LowDiskSpaceAnalyticsEvent;
import com.atlassian.confluence.internal.diagnostics.LowFileDescriptorCountAnalyticsEvent;
import com.atlassian.confluence.internal.diagnostics.LowMemoryAnalyticsEvent;
import com.atlassian.dc.filestore.api.DataSize;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.sun.management.UnixOperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;

class OperatingSystemMonitor
extends ConfluenceMonitor {
    static final long MIN_FREE_MEMORY = Integer.getInteger("diagnostics.os.min.free.memory.megabytes", 256).intValue();
    static final long MIN_FREE_DISK_SPACE = Integer.getInteger("diagnostics.os.min.free.disk.space.megabytes", 8192).intValue();
    static final long MIN_FREE_FILE_DESCRIPTOR = Integer.getInteger("diagnostics.os.min.free.file.descriptor.count", 128).intValue();
    private static final int OPERATING_SYSTEM_CHECK_SECS = Integer.getInteger("diagnostics.os.check.period.secs", 120);
    static final int LOW_FREE_MEMORY_ID = 1001;
    static final int LOW_FREE_DISK_SPACE_ID = 1002;
    static final int FILE_SYSTEM_INACCESSIBLE_ID = 1003;
    static final int LOW_FREE_FILE_DESCRIPTOR_ID = 1004;
    private static final Logger logger = LoggerFactory.getLogger(OperatingSystemMonitor.class);
    private static final String MONITOR_ID = "OS";
    private final FileStore localHome;
    private final FileStore shareHome;
    private final EventPublisher eventPublisher;
    private final Supplier<Long> availableMemory;
    private final Supplier<Long> totalMemory;
    private final Supplier<Long> maxFileDescriptorCount;
    private final Supplier<Long> openFileDescriptorCount;

    public static OperatingSystemMonitor create(EventPublisher eventPublisher, FileStore sharedHome, FileStore localHome) {
        return new OperatingSystemMonitor(Objects.requireNonNull(eventPublisher), OperatingSystemMonitor::getAvailableMemory, OperatingSystemMonitor::getTotalMemory, OperatingSystemMonitor::getMaxFileDescriptorCount, OperatingSystemMonitor::getOpenFileDescriptorCount, localHome, sharedHome);
    }

    @VisibleForTesting
    public OperatingSystemMonitor(EventPublisher eventPublisher, Supplier<Long> availableMemory, Supplier<Long> totalMemory, Supplier<Long> maxFileDescriptorCount, Supplier<Long> openFileDescriptorCount, FileStore localHome, FileStore shareHome) {
        this.availableMemory = availableMemory;
        this.totalMemory = totalMemory;
        this.maxFileDescriptorCount = maxFileDescriptorCount;
        this.openFileDescriptorCount = openFileDescriptorCount;
        this.localHome = localHome;
        this.shareHome = shareHome;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void init(MonitoringService monitoringService) {
        super.init(monitoringService);
        this.monitor = monitoringService.createMonitor(MONITOR_ID, "diagnostics.os.name", () -> true);
        this.defineIssue("diagnostics.os.issue", 1001, Severity.WARNING);
        this.defineIssue("diagnostics.os.issue", 1002, Severity.ERROR);
        this.defineIssue("diagnostics.os.issue", 1003, Severity.ERROR);
        this.defineIssue("diagnostics.os.issue", 1004, Severity.ERROR);
        this.startMonitorThread();
        logger.debug("{} monitor has been initialized", (Object)MONITOR_ID);
    }

    @Override
    protected String getMonitorId() {
        return MONITOR_ID;
    }

    private void startMonitorThread() {
        this.startMonitorThread(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(OPERATING_SYSTEM_CHECK_SECS);
                    this.checkFreeMemory();
                    this.checkFreeSpace(this.localHome, LowDiskSpaceAnalyticsEvent.DiskType.HOME);
                    this.checkFreeSpace(this.shareHome, LowDiskSpaceAnalyticsEvent.DiskType.SHARED);
                }
            }
            catch (InterruptedException e) {
                return;
            }
        }, "diagnostics-os-thread");
    }

    @VisibleForTesting
    void checkFreeSpace(FileStore fileStore, LowDiskSpaceAnalyticsEvent.DiskType diskType) {
        long free = OperatingSystemMonitor.getUsableSpace(fileStore);
        if (free < 0L) {
            this.alertFileSystemInaccessible(fileStore);
        } else if (free < MIN_FREE_DISK_SPACE * 1024L * 1024L) {
            long total = OperatingSystemMonitor.getTotalSpace(fileStore);
            this.alertLowFreeDiskSpace(fileStore, free / 0x100000L, total / 0x100000L, MIN_FREE_DISK_SPACE, diskType);
        }
    }

    @VisibleForTesting
    void checkFileDescriptorCount() {
        long max = this.maxFileDescriptorCount.get();
        long open = this.openFileDescriptorCount.get();
        if (max > 0L && open > 0L && max - open < MIN_FREE_FILE_DESCRIPTOR) {
            this.alertFileDescriptorCount(max, open, MIN_FREE_FILE_DESCRIPTOR);
        }
    }

    @VisibleForTesting
    void checkFreeMemory() {
        long freeMemoryInMegabytes = this.availableMemory.get() / 0x100000L;
        if (freeMemoryInMegabytes < MIN_FREE_MEMORY) {
            long totalMemoryInMegabytes = this.totalMemory.get() / 0x100000L;
            this.alertLowFreeMemory(freeMemoryInMegabytes, totalMemoryInMegabytes, MIN_FREE_MEMORY);
        }
    }

    private void alertLowFreeMemory(long free, long total, long minimum) {
        this.alert(1001, builder -> {
            builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"freeInMegabytes", (Object)free, (Object)"totalInMegabytes", (Object)total, (Object)"minimumInMegabytes", (Object)minimum));
            this.eventPublisher.publish((Object)new LowMemoryAnalyticsEvent(free, total, minimum));
        });
    }

    private void alertLowFreeDiskSpace(FileStore fileStore, long usableSpace, long totalSpace, long minimum, LowDiskSpaceAnalyticsEvent.DiskType diskType) {
        this.alert(1002, builder -> {
            builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"directory", (Object)fileStore.root().toString(), (Object)"freeInMegabytes", (Object)usableSpace, (Object)"totalInMegabytes", (Object)totalSpace, (Object)"minimumInMegabytes", (Object)minimum));
            this.eventPublisher.publish((Object)new LowDiskSpaceAnalyticsEvent(diskType, usableSpace, totalSpace, minimum));
        });
    }

    private void alertFileSystemInaccessible(FileStore file) {
        this.alert(1003, builder -> builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"directory", (Object)file.root().toString())));
    }

    private void alertFileDescriptorCount(long max, long open, long threshold) {
        this.alert(1004, builder -> {
            builder.timestamp(Instant.now()).details(() -> ImmutableMap.of((Object)"maxCount", (Object)max, (Object)"openCount", (Object)open, (Object)"requiredFreeCount", (Object)threshold));
            this.eventPublisher.publish((Object)new LowFileDescriptorCountAnalyticsEvent(max, open, threshold));
        });
    }

    private static long getAvailableMemory() {
        return new SystemInfo().getHardware().getMemory().getAvailable();
    }

    private static long getTotalMemory() {
        return new SystemInfo().getHardware().getMemory().getTotal();
    }

    private static long getUsableSpace(FileStore fileStore) {
        return fileStore.getAvailableSpace().map(DataSize::getBytes).orElse(-1L);
    }

    private static long getTotalSpace(FileStore fileStore) {
        return fileStore.getTotalSpace().map(DataSize::getBytes).orElse(-1L);
    }

    private static long getMaxFileDescriptorCount() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof UnixOperatingSystemMXBean) {
            UnixOperatingSystemMXBean unixOperatingSystemMXBean = (UnixOperatingSystemMXBean)operatingSystemMXBean;
            return unixOperatingSystemMXBean.getMaxFileDescriptorCount();
        }
        return -1L;
    }

    private static long getOpenFileDescriptorCount() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof UnixOperatingSystemMXBean) {
            UnixOperatingSystemMXBean unixOperatingSystemMXBean = (UnixOperatingSystemMXBean)operatingSystemMXBean;
            return unixOperatingSystemMXBean.getOpenFileDescriptorCount();
        }
        return -1L;
    }
}

