/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.PluginSystemLifecycle
 */
package com.atlassian.plugin;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugin.PluginSystemLifecycle;

@ExperimentalApi
public interface SplitStartupPluginSystemLifecycle
extends PluginSystemLifecycle {
    public void earlyStartup();

    public void lateStartup();
}

