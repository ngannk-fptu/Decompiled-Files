/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin.descriptor.backup;

import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import com.atlassian.confluence.plugin.descriptor.DefaultFactoryModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;

public class BackupRestoreProviderModuleDescriptor
extends DefaultFactoryModuleDescriptor<BackupRestoreProvider> {
    public BackupRestoreProviderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public BackupRestoreProvider getModule() {
        return (BackupRestoreProvider)this.getModuleFromProvider();
    }
}

