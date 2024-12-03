/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerDiagnostic;

public interface SchedulerDiagnosticProvider {
    public SchedulerDiagnostic getDiagnostic();
}

