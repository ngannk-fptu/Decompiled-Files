/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

public class PresenceReporterModuleDescriptor
extends AbstractModuleDescriptor<PresenceReporter> {
    private PresenceReporter presenceReporter;

    public PresenceReporterModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PresenceReporter getModule() {
        PresenceReporterModuleDescriptor presenceReporterModuleDescriptor = this;
        synchronized (presenceReporterModuleDescriptor) {
            if (null == this.presenceReporter) {
                this.presenceReporter = (PresenceReporter)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
            }
        }
        return this.presenceReporter;
    }
}

