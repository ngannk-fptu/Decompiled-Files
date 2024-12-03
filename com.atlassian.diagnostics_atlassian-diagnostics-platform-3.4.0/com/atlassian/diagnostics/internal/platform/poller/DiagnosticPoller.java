/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.MonitorConfiguration
 *  com.atlassian.diagnostics.internal.AlertProvider
 */
package com.atlassian.diagnostics.internal.platform.poller;

import com.atlassian.diagnostics.MonitorConfiguration;
import com.atlassian.diagnostics.internal.AlertProvider;

public abstract class DiagnosticPoller<T extends MonitorConfiguration>
extends AlertProvider<T> {
    protected DiagnosticPoller(String key, T monitorConfiguration) {
        super(key, monitorConfiguration);
    }

    protected abstract void execute();
}

