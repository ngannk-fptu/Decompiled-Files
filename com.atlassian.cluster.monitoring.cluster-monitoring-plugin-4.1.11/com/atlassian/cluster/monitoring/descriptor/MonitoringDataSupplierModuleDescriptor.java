/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cluster.monitoring.spi.descriptors.SystemMonitoringModuleDescriptor
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.cluster.monitoring.descriptor;

import com.atlassian.cluster.monitoring.spi.descriptors.SystemMonitoringModuleDescriptor;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.Optional;
import java.util.function.Supplier;

public class MonitoringDataSupplierModuleDescriptor
extends AbstractModuleDescriptor<Supplier<Table>>
implements SystemMonitoringModuleDescriptor {
    private static final String PRIORITY_PARAM_NAME = "priority";
    private static final int DEFAULT_PRIORITY = 50;
    private static final String INTERVAL_IN_MIN_PARAM_NAME = "intervalInMin";
    private static final int DEFAULT_INTERVAL_IN_MIN = 5;

    public MonitoringDataSupplierModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public int getPriority() {
        return this.getIntegerParam(PRIORITY_PARAM_NAME).orElse(50);
    }

    public int getIntervalInMin() {
        return this.getIntegerParam(INTERVAL_IN_MIN_PARAM_NAME).orElse(5);
    }

    public Supplier<Table> getModule() {
        return (Supplier)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    private Optional<Integer> getIntegerParam(String name) {
        return Optional.ofNullable((String)this.getParams().get(name)).map(Integer::parseInt);
    }
}

