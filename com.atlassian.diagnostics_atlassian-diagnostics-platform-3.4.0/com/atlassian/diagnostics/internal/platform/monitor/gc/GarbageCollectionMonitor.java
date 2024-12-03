/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  com.atlassian.diagnostics.internal.InitializingMonitor
 *  com.atlassian.diagnostics.internal.jmx.JmxService
 *  com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocation
 *  com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocationService
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import com.atlassian.diagnostics.internal.jmx.JmxService;
import com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocation;
import com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocationService;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GarbageCollectionMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.gc.HighGCTimeDetails;
import com.atlassian.diagnostics.internal.platform.plugin.PluginFinder;
import com.google.common.collect.ImmutableMap;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class GarbageCollectionMonitor
extends InitializingMonitor {
    private static final String KEY_PREFIX = "diagnostics.gc.issue";
    private static final int HIGH_GARBAGE_COLLECTION_TIME_WARNING = 2001;
    private static final int HIGH_GARBAGE_COLLECTION_TIME_ERROR = 3001;
    private static final Map<Severity, Integer> SEVERITY_TO_ID = ImmutableMap.of((Object)Severity.WARNING, (Object)2001, (Object)Severity.ERROR, (Object)3001);
    private final GarbageCollectionMonitorConfiguration garbageCollectionMonitorConfiguration;
    private final JmxService jmxService;
    private final ThreadMemoryAllocationService threadMemoryAllocationService;
    private final PluginFinder pluginFinder;
    private final ThreadDumpProducer threadDumpProducer;

    public GarbageCollectionMonitor(GarbageCollectionMonitorConfiguration garbageCollectionMonitorConfiguration, JmxService jmxService, ThreadMemoryAllocationService threadMemoryAllocationService, PluginFinder pluginFinder, ThreadDumpProducer threadDumpProducer) {
        this.garbageCollectionMonitorConfiguration = garbageCollectionMonitorConfiguration;
        this.jmxService = jmxService;
        this.threadMemoryAllocationService = threadMemoryAllocationService;
        this.pluginFinder = pluginFinder;
        this.threadDumpProducer = threadDumpProducer;
    }

    public void init(@Nonnull MonitoringService monitoringService) {
        this.monitor = monitoringService.createMonitor("GC", "diagnostics.gc.name", (MonitorConfiguration)this.garbageCollectionMonitorConfiguration);
        this.defineIssue(KEY_PREFIX, 2001, Severity.WARNING);
        this.defineIssue(KEY_PREFIX, 3001, Severity.ERROR);
    }

    public void raiseAlertForHighGarbageCollectionTime(HighGCTimeDetails highGCTimeDetails) {
        this.alert(SEVERITY_TO_ID.getOrDefault(highGCTimeDetails.getSeverity(), 2001), builder -> builder.timestamp(highGCTimeDetails.getTimestamp()).details(() -> this.getHighGarbageCollectionTimeAlertDetails(highGCTimeDetails)));
    }

    private Map<String, Object> getHighGarbageCollectionTimeAlertDetails(HighGCTimeDetails highGCTimeDetails) {
        ImmutableMap.Builder alertDetails = new ImmutableMap.Builder().put((Object)"timeWindow", (Object)highGCTimeDetails.getTimeWindow().getSeconds()).put((Object)"garbageCollectionTimeInMillis", (Object)highGCTimeDetails.getGarbageCollectionTime().toMillis()).put((Object)"garbageCollectionCount", (Object)highGCTimeDetails.getGarbageCollectionCount()).put((Object)"garbageCollectorName", (Object)highGCTimeDetails.getGarbageCollectorName());
        this.addMemoryPoolDetails((ImmutableMap.Builder<String, Object>)alertDetails);
        if (this.garbageCollectionMonitorConfiguration.shouldIncludeTopThreadMemoryAllocationsInDetails()) {
            this.addTopFiveThreadAllocations((ImmutableMap.Builder<String, Object>)alertDetails);
        }
        return alertDetails.build();
    }

    private void addMemoryPoolDetails(ImmutableMap.Builder<String, Object> alertDetails) {
        for (MemoryPoolMXBean memoryPoolMXBean : this.jmxService.getMemoryPoolMXBeans()) {
            MemoryUsage memoryUsage = memoryPoolMXBean.getUsage();
            alertDetails.put((Object)memoryPoolMXBean.getName(), (Object)memoryUsage);
        }
    }

    private void addTopFiveThreadAllocations(ImmutableMap.Builder<String, Object> alertDetails) {
        List topFiveThreadMemoryAllocations = this.threadMemoryAllocationService.getTopThreadMemoryAllocations(5);
        ImmutableMap<String, Object> topFiveThreadMemoryAllocationDetails = this.topThreadMemoryAllocationDetails(topFiveThreadMemoryAllocations);
        if (!topFiveThreadMemoryAllocationDetails.isEmpty()) {
            alertDetails.put((Object)"topFiveThreadMemoryAllocations", topFiveThreadMemoryAllocationDetails);
        }
    }

    private ImmutableMap<String, Object> topThreadMemoryAllocationDetails(List<ThreadMemoryAllocation> threadMemoryAllocations) {
        ImmutableMap.Builder topThreadAllocationDetails = new ImmutableMap.Builder();
        for (ThreadMemoryAllocation threadMemoryAllocation : threadMemoryAllocations) {
            topThreadAllocationDetails.put((Object)threadMemoryAllocation.getThreadName(), (Object)this.threadMemoryAllocationDetails(threadMemoryAllocation));
        }
        return topThreadAllocationDetails.build();
    }

    private String threadMemoryAllocationDetails(ThreadMemoryAllocation threadMemoryAllocation) {
        Collection<String> plugins;
        ImmutableMap.Builder threadMemoryAllocationDetails = new ImmutableMap.Builder();
        threadMemoryAllocationDetails.put((Object)"memoryAllocationInBytes", (Object)threadMemoryAllocation.getMemoryAllocationInBytes());
        String stackTrace = this.threadDumpProducer.toStackTraceString(Arrays.asList(threadMemoryAllocation.getStackTrace()));
        if (StringUtils.isNotEmpty((CharSequence)stackTrace)) {
            threadMemoryAllocationDetails.put((Object)"stackTrace", (Object)stackTrace);
        }
        if (!(plugins = this.getPluginNamesFromStackTrace(threadMemoryAllocation)).isEmpty()) {
            threadMemoryAllocationDetails.put((Object)"plugins", (Object)String.join((CharSequence)" -> ", plugins));
        }
        return threadMemoryAllocationDetails.build().toString();
    }

    private Collection<String> getPluginNamesFromStackTrace(ThreadMemoryAllocation threadMemoryAllocation) {
        return this.pluginFinder.getPluginNamesFromStackTrace(threadMemoryAllocation.getStackTrace());
    }
}

