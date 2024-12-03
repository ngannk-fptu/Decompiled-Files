/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocation
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.platform.monitor.thread;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocation;
import com.atlassian.diagnostics.internal.platform.monitor.thread.ThreadMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.plugin.PluginFinder;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ThreadMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.thread.issue";
    private static final int HIGH_THREAD_MEMORY_USAGE = 2001;
    private final ThreadMonitorConfiguration threadMonitorConfiguration;
    private final PluginFinder pluginFinder;
    private final ThreadDumpProducer threadDumpProducer;

    public ThreadMonitor(ThreadMonitorConfiguration threadMonitorConfiguration, PluginFinder pluginFinder, ThreadDumpProducer threadDumpProducer) {
        this.threadMonitorConfiguration = threadMonitorConfiguration;
        this.pluginFinder = pluginFinder;
        this.threadDumpProducer = threadDumpProducer;
    }

    public void init(MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("THREAD", "diagnostics.thread.name", (MonitorConfiguration)this.threadMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
    }

    public void raiseAlertForHighThreadMemoryUsage(Instant now, List<ThreadMemoryAllocation> threadMemoryAllocations) {
        this.alert(2001, builder -> builder.timestamp(now).details(() -> this.highThreadMemoryUsageAlertDetails(threadMemoryAllocations)));
    }

    private Map<String, Object> highThreadMemoryUsageAlertDetails(List<ThreadMemoryAllocation> threadMemoryAllocations) {
        List threadsWithHighMemoryUsageAlertDetails = threadMemoryAllocations.stream().map(this.toAlertDetails()).collect(Collectors.toList());
        return new ImmutableMap.Builder().put((Object)"threadsWithHighMemoryUsage", threadsWithHighMemoryUsageAlertDetails).build();
    }

    private Function<ThreadMemoryAllocation, Map<String, Object>> toAlertDetails() {
        return threadMemoryAllocation -> {
            Collection<String> plugins;
            ImmutableMap.Builder builder = new ImmutableMap.Builder().put((Object)"memoryAllocationInBytes", (Object)threadMemoryAllocation.getMemoryAllocationInBytes()).put((Object)"threadName", (Object)threadMemoryAllocation.getThreadName());
            String stackTrace = this.threadDumpProducer.toStackTraceString(Arrays.asList(threadMemoryAllocation.getStackTrace()));
            if (StringUtils.isNotEmpty((CharSequence)stackTrace)) {
                builder.put((Object)"stackTrace", (Object)stackTrace);
            }
            if (!(plugins = this.pluginFinder.getPluginNamesFromStackTrace(threadMemoryAllocation.getStackTrace())).isEmpty()) {
                builder.put((Object)"plugins", (Object)String.join((CharSequence)" -> ", plugins));
            }
            return builder.build();
        };
    }
}

