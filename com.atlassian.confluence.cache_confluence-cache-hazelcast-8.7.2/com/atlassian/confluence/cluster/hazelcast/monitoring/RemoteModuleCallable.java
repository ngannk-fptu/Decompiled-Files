/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast.monitoring;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class RemoteModuleCallable
implements Callable<Table>,
Serializable {
    private static final Logger log = LoggerFactory.getLogger(RemoteModuleCallable.class);
    private final String completeKey;

    public RemoteModuleCallable(ModuleCompleteKey key) {
        this.completeKey = (String)Preconditions.checkNotNull((Object)((ModuleCompleteKey)Preconditions.checkNotNull((Object)key)).getCompleteKey());
    }

    @Override
    public Table call() throws Exception {
        log.debug("Calling module: " + this.completeKey);
        return this.getModule().get();
    }

    private Supplier<Table> getModule() {
        PluginAccessor accessor = (PluginAccessor)ContainerManager.getComponent((String)"pluginAccessor");
        return (Supplier)accessor.getEnabledPluginModule(this.completeKey).getModule();
    }
}

