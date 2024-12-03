/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.plugin;

public interface PluginSystemMonitoringConfig {
    public boolean classNameToPluginKeyStoreDisabled();

    public int classContextTraversalLimit();

    public int stackTraceTraversalLimit();
}

