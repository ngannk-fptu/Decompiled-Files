/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

public interface PluginSystemLifecycle {
    public void init();

    public void shutdown();

    public void warmRestart();
}

