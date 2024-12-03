/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.util.profiling.CounterSnapshot;
import com.atlassian.confluence.util.profiling.TimerSnapshot;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface ConfluenceMonitoringControl {
    public boolean isMonitoringEnabled();

    public void enableMonitoring();

    public void disableMonitoring();

    public boolean isCpuTimingEnabled();

    public void enableCpuTiming();

    public void disableCpuTiming();

    public void enableHibernateMonitoring();

    public void disableHibernateMonitoring();

    public void clear();

    public @NonNull List<CounterSnapshot> snapshotCounters();

    public @NonNull List<TimerSnapshot> snapshotTimers();
}

