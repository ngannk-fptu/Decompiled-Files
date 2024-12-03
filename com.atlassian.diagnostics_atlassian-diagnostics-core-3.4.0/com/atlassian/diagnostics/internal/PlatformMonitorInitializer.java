/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitoringService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.internal.InitializingMonitor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformMonitorInitializer {
    private static final Logger log = LoggerFactory.getLogger(PlatformMonitorInitializer.class);

    public PlatformMonitorInitializer(MonitoringService monitoringService, List<InitializingMonitor> coreMonitors) {
        coreMonitors.forEach(monitor -> this.init(monitoringService, (InitializingMonitor)monitor));
    }

    private void init(MonitoringService monitoringService, InitializingMonitor monitor) {
        if (monitor != null) {
            try {
                monitor.init(monitoringService);
                log.debug("Initialized monitor '{}'", (Object)monitor.getClass().getName());
            }
            catch (NoClassDefFoundError | RuntimeException e) {
                log.warn("Failed to initialize monitor {}", (Object)monitor.getClass().getName(), (Object)e);
            }
        }
    }
}

