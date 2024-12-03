/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.MonitorConfiguration
 */
package com.atlassian.diagnostics.internal.platform.monitor;

import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.util.concurrent.TimeUnit;

public class DefaultMonitorConfiguration
implements MonitorConfiguration {
    private final DiagnosticsConfiguration diagnosticsConfiguration;

    public DefaultMonitorConfiguration(DiagnosticsConfiguration diagnosticsConfiguration) {
        this.diagnosticsConfiguration = diagnosticsConfiguration;
    }

    public boolean isEnabled() {
        return this.diagnosticsConfiguration.isEnabled();
    }

    protected ScheduleInterval defaultScheduleInterval() {
        return ScheduleInterval.of(5, TimeUnit.SECONDS);
    }
}

