/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.diagnostics.internal.platform.monitor.gc;

import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCDetailsCalculator;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCMXBeanPoller;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCMXBeanPollerFactory;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCRead;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GCReadsStore;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GarbageCollectionMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.gc.GarbageCollectionMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.gc.HighGCTimeDetails;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

@ParametersAreNonnullByDefault
@NotThreadSafe
public class GarbageCollectionPoller
extends DiagnosticPoller<GarbageCollectionMonitorConfiguration> {
    private final GarbageCollectionMonitorConfiguration garbageCollectionMonitorConfiguration;
    private final GCMXBeanPoller gcMXBeanPoller;
    private final Clock clock;
    private final GCReadsStore gcReadsStore;
    private final GarbageCollectionMonitor garbageCollectionMonitor;

    public GarbageCollectionPoller(GarbageCollectionMonitor garbageCollectionMonitor, GarbageCollectionMonitorConfiguration garbageCollectionMonitorConfiguration, GCMXBeanPollerFactory gcMXBeanPollerFactory, GCReadsStore gcReadsStore, Clock clock) {
        super(GarbageCollectionPoller.class.getName(), garbageCollectionMonitorConfiguration);
        this.garbageCollectionMonitor = Objects.requireNonNull(garbageCollectionMonitor);
        this.garbageCollectionMonitorConfiguration = Objects.requireNonNull(garbageCollectionMonitorConfiguration);
        this.clock = Objects.requireNonNull(clock);
        this.gcReadsStore = Objects.requireNonNull(gcReadsStore);
        this.gcMXBeanPoller = gcMXBeanPollerFactory.getGCMXBeansPoller().orElse(null);
    }

    @Override
    protected void execute() {
        if (this.gcMXBeanPollerPresent()) {
            this.raiseAlertIfGarbageCollectionTimeIsAboveThreshold();
        }
    }

    private boolean gcMXBeanPollerPresent() {
        return this.gcMXBeanPoller != null;
    }

    private void raiseAlertIfGarbageCollectionTimeIsAboveThreshold() {
        GCRead currentGCRead = this.gcMXBeanPoller.doRead();
        this.gcReadsStore.storeRead(currentGCRead);
        Optional<GCRead> readFromBeforeTheTimeWindow = this.gcReadsStore.getReadIfHappenedBefore(this.timeWindowStart());
        readFromBeforeTheTimeWindow.ifPresent(previousRead -> this.raiseAlertIfGarbageCollectionTimeIsAboveThreshold((GCRead)previousRead, currentGCRead));
    }

    private Instant timeWindowStart() {
        return Instant.now(this.clock).minus(this.garbageCollectionMonitorConfiguration.slidingWindowSize());
    }

    private void raiseAlertIfGarbageCollectionTimeIsAboveThreshold(GCRead previousRead, GCRead currentRead) {
        GCDetailsCalculator alertDetailsCalculator = new GCDetailsCalculator(previousRead, currentRead);
        if (this.shouldRaiseErrorAlert(alertDetailsCalculator)) {
            this.garbageCollectionMonitor.raiseAlertForHighGarbageCollectionTime(this.getDetailsBuilder(alertDetailsCalculator).severity(Severity.ERROR).build());
        } else if (this.shouldRaiseWarningAlert(alertDetailsCalculator)) {
            this.garbageCollectionMonitor.raiseAlertForHighGarbageCollectionTime(this.getDetailsBuilder(alertDetailsCalculator).severity(Severity.WARNING).build());
        }
    }

    public boolean shouldRaiseErrorAlert(GCDetailsCalculator alertDetailsCalculator) {
        return this.garbageCollectionTimeExceedsThreshold(alertDetailsCalculator, ((GarbageCollectionMonitorConfiguration)this.monitorConfiguration).getErrorThreshold());
    }

    public boolean shouldRaiseWarningAlert(GCDetailsCalculator alertDetailsCalculator) {
        return this.garbageCollectionTimeExceedsThreshold(alertDetailsCalculator, ((GarbageCollectionMonitorConfiguration)this.monitorConfiguration).getWarningThreshold());
    }

    private boolean garbageCollectionTimeExceedsThreshold(GCDetailsCalculator alertDetailsCalculator, double percentageThreshold) {
        return alertDetailsCalculator.getPercentageOfTimeInGarbageCollection() >= percentageThreshold;
    }

    private HighGCTimeDetails.HighGCTimeAlertBuilder getDetailsBuilder(GCDetailsCalculator alertCalculator) {
        return HighGCTimeDetails.builder().timestamp(Instant.now(this.clock)).addAlertInfo(alertCalculator).garbageCollectorName(this.gcMXBeanPoller.getGarbageCollectorName());
    }
}

