/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.analytics.jobs;

import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEventSupplier;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

public class PeriodicEventSupplierModuleDescriptor
extends AbstractModuleDescriptor<PeriodicEventSupplier> {
    private PeriodicEventSupplier supplier;

    @Autowired
    public PeriodicEventSupplierModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
    }

    public void enabled() {
        super.enabled();
        this.supplier = (PeriodicEventSupplier)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public void disabled() {
        super.disabled();
        this.supplier = null;
    }

    public PeriodicEventSupplier getModule() {
        return this.supplier;
    }
}

