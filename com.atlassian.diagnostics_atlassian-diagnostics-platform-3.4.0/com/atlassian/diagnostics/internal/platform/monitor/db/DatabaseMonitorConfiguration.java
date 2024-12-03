/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.time.Duration;
import javax.annotation.Nonnull;

public interface DatabaseMonitorConfiguration
extends MonitorConfiguration {
    public static final String DB_STATIC_METHOD_INVOKER_ENABLE = "com.atlassian.diagnostics.db.static.method.invoker.enable";
    public static final String DB_STATIC_METHOD_INVOKER_IMPROVED_ACCURACY_ENABLE = "atlassian.diagnostics.db.static.method.invoker.improved.accuracy.enable";

    @Nonnull
    public Duration poolConnectionLeakTimeout();

    public double poolUtilizationPercentageLimit();

    @Nonnull
    public Duration poolUtilizationTimeWindow();

    @Nonnull
    public Duration longRunningOperationLimit();

    public boolean includeSqlQueryInAlerts();

    @Nonnull
    public ScheduleInterval databasePoolPollerScheduleInterval();

    public boolean findStaticMethodInvoker();

    public boolean staticMethodInvokerImprovedAccuracy();
}

