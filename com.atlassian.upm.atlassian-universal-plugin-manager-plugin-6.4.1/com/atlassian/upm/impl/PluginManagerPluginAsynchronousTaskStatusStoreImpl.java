/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.upm.impl;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStoreImpl;

public class PluginManagerPluginAsynchronousTaskStatusStoreImpl
extends AsynchronousTaskStatusStoreImpl {
    public PluginManagerPluginAsynchronousTaskStatusStoreImpl(PluginSettingsFactory pluginSettingsFactory, ClusterLockService lockService) {
        super(pluginSettingsFactory, lockService);
    }

    @Override
    protected String getPluginSettingsKeyPrefix() {
        return PluginManagerPluginAsynchronousTaskStatusStoreImpl.class.getName();
    }
}

