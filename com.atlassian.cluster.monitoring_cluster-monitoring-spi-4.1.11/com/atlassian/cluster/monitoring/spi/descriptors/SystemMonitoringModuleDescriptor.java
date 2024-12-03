/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.cluster.monitoring.spi.descriptors;

import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.plugin.ModuleDescriptor;
import java.util.function.Supplier;

public interface SystemMonitoringModuleDescriptor
extends ModuleDescriptor<Supplier<Table>> {
    public int getIntervalInMin();
}

