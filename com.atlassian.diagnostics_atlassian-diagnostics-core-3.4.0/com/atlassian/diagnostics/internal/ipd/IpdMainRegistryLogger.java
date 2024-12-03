/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService
 */
package com.atlassian.diagnostics.internal.ipd;

import com.atlassian.diagnostics.internal.ipd.DefaultLoggingService;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.ipd.internal.spi.IpdLoggingService;

public class IpdMainRegistryLogger {
    private final IpdMainRegistry ipdMainRegistry;
    private final IpdLoggingService ipdLoggingService;

    public IpdMainRegistryLogger(IpdMainRegistry ipdMainRegistry, IpdLoggingService ipdLoggingService) {
        this.ipdMainRegistry = ipdMainRegistry;
        this.ipdLoggingService = ipdLoggingService;
    }

    public void logRegisteredMetrics(boolean includeExtraLogging) {
        String timestamp = DefaultLoggingService.getCurrentTimestamp();
        this.ipdMainRegistry.getMetrics().stream().filter(metric -> metric.getOptions().isRegularLogging()).forEach(metric -> this.ipdLoggingService.logMetric(metric, timestamp, includeExtraLogging));
    }
}

