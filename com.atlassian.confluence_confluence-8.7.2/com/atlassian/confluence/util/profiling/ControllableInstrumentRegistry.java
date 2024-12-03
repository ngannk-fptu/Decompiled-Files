/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.InstrumentRegistry
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.instrumentation.InstrumentRegistry;

public interface ControllableInstrumentRegistry
extends InstrumentRegistry {
    public boolean isMonitoringEnabled();

    public void enableMonitoring();

    public void disableMonitoring();

    public void clear();
}

