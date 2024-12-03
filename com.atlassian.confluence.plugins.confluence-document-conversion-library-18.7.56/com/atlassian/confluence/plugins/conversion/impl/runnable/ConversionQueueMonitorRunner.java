/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.plugins.conversion.impl.ConfigurationProperties;
import com.atlassian.confluence.plugins.conversion.impl.LocalFileSystemConversionResultSupplier;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUInfo;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryCPUService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@Component
public class ConversionQueueMonitorRunner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(ConversionQueueMonitorRunner.class);
    private static final int CONVERSION_QUEUE_THRESHOLD = ConfigurationProperties.getInt(ConfigurationProperties.PROP_CONVERSION_QUEUE_THRESHOLD);
    private final LocalFileSystemConversionResultSupplier localFileSystemConversionResultSupplier;
    private final MemoryCPUService memoryCPUService;

    @Autowired
    public ConversionQueueMonitorRunner(LocalFileSystemConversionResultSupplier localFileSystemConversionResultSupplier, MemoryCPUService memoryCPUService) {
        this.localFileSystemConversionResultSupplier = localFileSystemConversionResultSupplier;
        this.memoryCPUService = memoryCPUService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            this.runJobInternal(request);
            return JobRunnerResponse.success();
        }
        catch (IllegalArgumentException e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    private void runJobInternal(JobRunnerRequest request) {
        String debugMessage;
        MemoryCPUInfo memoryCPUInfo = this.memoryCPUService.getMemoryCPUInfo();
        int conversionQueueSize = this.localFileSystemConversionResultSupplier.getConversionQueueSize();
        int originalThreadPoolSize = this.localFileSystemConversionResultSupplier.getJvmThreadPoolSize();
        int maxConversionThreads = ConfigurationProperties.getInt(ConfigurationProperties.PROP_NUM_THREADS);
        int adjustedThreadPoolSize = originalThreadPoolSize;
        if (conversionQueueSize >= CONVERSION_QUEUE_THRESHOLD) {
            ++adjustedThreadPoolSize;
            debugMessage = "conversion queue growing scaling up conversion threads.";
        } else {
            debugMessage = "conversion queue shrunk scaling down conversion threads.";
            --adjustedThreadPoolSize;
            adjustedThreadPoolSize = Math.max(1, adjustedThreadPoolSize);
        }
        if (memoryCPUInfo.isLowMemory()) {
            debugMessage = "not enough memory to run conversions with parallel.";
            --adjustedThreadPoolSize;
            adjustedThreadPoolSize = Math.max(1, adjustedThreadPoolSize);
        } else if (memoryCPUInfo.isCPUBusy()) {
            debugMessage = "system load is too high to run conversions with parallel.";
            --adjustedThreadPoolSize;
            adjustedThreadPoolSize = Math.max(1, adjustedThreadPoolSize);
        }
        if (originalThreadPoolSize == adjustedThreadPoolSize || adjustedThreadPoolSize > maxConversionThreads) {
            return;
        }
        this.localFileSystemConversionResultSupplier.setJvmThreadPoolSize(adjustedThreadPoolSize);
        if (log.isInfoEnabled()) {
            long freeHeapMb = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
            long maxHeapMb = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
            log.info("Adjusted in-JVM file conversion thread pool to " + adjustedThreadPoolSize + " threads because " + debugMessage + "(Max Conversion Threads: " + maxConversionThreads + ", Free heap: " + freeHeapMb + "MB/" + maxHeapMb + "MB, Cores: " + memoryCPUInfo.getAvailableProcessors() + ", System Load Avg: " + memoryCPUInfo.getSystemLoadAverage() + ", Conversion Queue Size: " + conversionQueueSize + ", Used Mem Ratio: " + MemoryCPUInfo.getUsedMemoryRatio() + ", System Load Ratio: " + MemoryCPUInfo.getSystemLoadRatio() + ", Queue Threshold: " + CONVERSION_QUEUE_THRESHOLD + ")");
        }
    }
}

