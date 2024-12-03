/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocationService
 */
package com.atlassian.diagnostics.internal.platform.monitor.thread;

import com.atlassian.diagnostics.internal.jmx.ThreadMemoryAllocationService;
import com.atlassian.diagnostics.internal.platform.monitor.thread.ThreadMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.thread.ThreadMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import java.time.Instant;
import java.util.List;

public class ThreadPoller
extends DiagnosticPoller<ThreadMonitorConfiguration> {
    private final ThreadMonitor threadMonitor;
    private final ThreadMemoryAllocationService threadMemoryAllocationService;

    public ThreadPoller(ThreadMonitor threadMonitor, ThreadMonitorConfiguration monitorConfiguration, ThreadMemoryAllocationService threadMemoryAllocationService) {
        super(ThreadPoller.class.getName(), monitorConfiguration);
        this.threadMonitor = threadMonitor;
        this.threadMemoryAllocationService = threadMemoryAllocationService;
    }

    @Override
    protected void execute() {
        List threadsWithHighMemoryUsage = this.threadMemoryAllocationService.getThreadMemoryAllocations(((ThreadMonitorConfiguration)this.monitorConfiguration).maxThreadMemoryUsageInBytes(), ((ThreadMonitorConfiguration)this.monitorConfiguration).maxStackTraceDepth());
        if (!threadsWithHighMemoryUsage.isEmpty()) {
            this.threadMonitor.raiseAlertForHighThreadMemoryUsage(Instant.now(), threadsWithHighMemoryUsage);
        }
    }
}

