/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.internal.platform.ConsecutiveAlertGate;
import com.atlassian.diagnostics.internal.platform.ConsecutiveAlertGateFactory;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseMonitor;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabaseMonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.db.DatabasePoolDiagnosticProvider;
import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import java.time.Clock;
import javax.annotation.Nonnull;

public class DatabasePoolPoller
extends DiagnosticPoller<DatabaseMonitorConfiguration> {
    private final DatabaseMonitor databaseMonitor;
    private final Clock clock;
    private final ConsecutiveAlertGate alertGate;
    private final DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider;

    public DatabasePoolPoller(@Nonnull DatabaseMonitorConfiguration config, @Nonnull DatabaseMonitor databaseMonitor, @Nonnull Clock clock, @Nonnull ConsecutiveAlertGateFactory alertGateFactory, @Nonnull DatabasePoolDiagnosticProvider databasePoolDiagnosticProvider) {
        super(DatabasePoolPoller.class.getName(), config);
        this.databaseMonitor = databaseMonitor;
        this.clock = clock;
        this.alertGate = alertGateFactory.createAlertGate(config::poolUtilizationTimeWindow, clock);
        this.databasePoolDiagnosticProvider = databasePoolDiagnosticProvider;
    }

    @Override
    protected void execute() {
        this.raiseAlertIfPoolUtilizationHasExceededThreshold(this.databasePoolDiagnosticProvider.getDiagnostic());
    }

    private void raiseAlertIfPoolUtilizationHasExceededThreshold(DatabasePoolDiagnostic databasePoolDiagnostic) {
        if (this.alertGate.shouldRaiseAlert(() -> this.hasReachedDbPoolUtilisationThreshold(databasePoolDiagnostic)) && !databasePoolDiagnostic.isEmpty()) {
            this.databaseMonitor.raiseAlertForHighPoolUtilization(this.clock.instant(), databasePoolDiagnostic);
        }
    }

    private boolean hasReachedDbPoolUtilisationThreshold(DatabasePoolDiagnostic databasePoolDiagnostic) {
        return (double)databasePoolDiagnostic.getActiveConnections() / (double)databasePoolDiagnostic.getMaxConnections() * 100.0 >= ((DatabaseMonitorConfiguration)this.monitorConfiguration).poolUtilizationPercentageLimit();
    }
}

