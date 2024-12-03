/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory;

import com.atlassian.diagnostics.internal.platform.analytics.directory.LowDirectorySpaceEventFactory;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.OperatingSystemMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory.Directory;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory.DirectorySpaceInformationProvider;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import com.atlassian.event.api.EventPublisher;
import javax.annotation.Nonnull;

public class DirectorySpacePoller
extends DiagnosticPoller<OperatingSystemMonitorConfiguration> {
    private final OperatingSystemMonitor operatingSystemMonitor;
    private final EventPublisher eventPublisher;
    private final Directory directory;
    private final DirectorySpaceInformationProvider directorySpaceInformationProvider;
    private final LowDirectorySpaceEventFactory lowDirectorySpaceEventFactory;

    public DirectorySpacePoller(@Nonnull String key, @Nonnull OperatingSystemMonitor operatingSystemMonitor, @Nonnull OperatingSystemMonitorConfiguration operatingSystemMonitorConfiguration, @Nonnull EventPublisher eventPublisher, @Nonnull Directory directory, @Nonnull DirectorySpaceInformationProvider directorySpaceInformationProvider, @Nonnull LowDirectorySpaceEventFactory lowDirectorySpaceEventFactory) {
        super(key, operatingSystemMonitorConfiguration);
        this.operatingSystemMonitor = operatingSystemMonitor;
        this.eventPublisher = eventPublisher;
        this.directory = directory;
        this.directorySpaceInformationProvider = directorySpaceInformationProvider;
        this.lowDirectorySpaceEventFactory = lowDirectorySpaceEventFactory;
    }

    @Override
    protected void execute() {
        long freeSpace = this.directorySpaceInformationProvider.freeSpace();
        long minimumMegabytesOfFreeSpace = ((OperatingSystemMonitorConfiguration)this.monitorConfiguration).directoryMinimumMegabytesOfFreeDiskSpace();
        if (freeSpace < 0L) {
            this.operatingSystemMonitor.alertFileSystemInaccessible(this.directory.getFile());
        } else if (freeSpace < minimumMegabytesOfFreeSpace) {
            long totalSpace = this.directorySpaceInformationProvider.totalSpace();
            this.operatingSystemMonitor.alertLowFreeDiskSpace(this.directory.getFile(), freeSpace, totalSpace, minimumMegabytesOfFreeSpace);
            if (this.lowDirectorySpaceEventFactory.isEnabled()) {
                this.eventPublisher.publish((Object)this.lowDirectorySpaceEventFactory.create(this.directory.getType(), freeSpace, totalSpace, minimumMegabytesOfFreeSpace));
            }
        }
    }
}

