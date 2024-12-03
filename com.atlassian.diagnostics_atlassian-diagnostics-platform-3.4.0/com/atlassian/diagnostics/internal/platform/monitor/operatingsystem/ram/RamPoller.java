/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.ram;

import com.atlassian.diagnostics.internal.platform.analytics.ram.LowRamEventFactory;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.ram.RamInformationProvider;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.Nonnull;

public class RamPoller
extends DiagnosticPoller<OperatingSystemMonitorConfiguration> {
    private final OperatingSystemMonitor operatingSystemMonitor;
    private final EventPublisher eventPublisher;
    private final RamInformationProvider ramInformationProvider;
    private final LowRamEventFactory lowRamEventFactory;

    public RamPoller(@Nonnull OperatingSystemMonitor operatingSystemMonitor, @Nonnull OperatingSystemMonitorConfiguration operatingSystemMonitorConfiguration, @Nonnull EventPublisher eventPublisher, @Nonnull RamInformationProvider ramInformationProvider, @Nonnull LowRamEventFactory lowRamEventFactory) {
        super(RamPoller.class.getName(), operatingSystemMonitorConfiguration);
        this.operatingSystemMonitor = operatingSystemMonitor;
        this.eventPublisher = eventPublisher;
        this.ramInformationProvider = ramInformationProvider;
        this.lowRamEventFactory = lowRamEventFactory;
    }

    @Override
    protected void execute() {
        long minimumMegabytesOfRam;
        long freeMemoryInMegabytes = this.ramInformationProvider.freeMemory();
        if (freeMemoryInMegabytes < (minimumMegabytesOfRam = ((OperatingSystemMonitorConfiguration)this.monitorConfiguration).minimumMegabytesOfRam())) {
            long totalMemoryInMegabytes = this.ramInformationProvider.totalMemory();
            this.operatingSystemMonitor.alertLowFreeMemory(freeMemoryInMegabytes, totalMemoryInMegabytes, minimumMegabytesOfRam);
            if (this.lowRamEventFactory.isEnabled()) {
                this.eventPublisher.publish((Object)this.lowRamEventFactory.create(freeMemoryInMegabytes, totalMemoryInMegabytes, minimumMegabytesOfRam));
            }
        }
    }
}

