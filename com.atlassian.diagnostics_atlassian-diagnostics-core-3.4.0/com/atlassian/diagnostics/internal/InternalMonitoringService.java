/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCount
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.PageCallback
 *  com.atlassian.diagnostics.PageRequest
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.PageCallback;
import com.atlassian.diagnostics.PageRequest;
import javax.annotation.Nonnull;

public interface InternalMonitoringService
extends MonitoringService {
    public <T> T internalStreamAlertCounts(@Nonnull AlertCriteria var1, @Nonnull PageCallback<? super AlertCount, T> var2, @Nonnull PageRequest var3);
}

