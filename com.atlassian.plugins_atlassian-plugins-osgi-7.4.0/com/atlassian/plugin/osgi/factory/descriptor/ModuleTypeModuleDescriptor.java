/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.RequirePermission
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.descriptors.CannotDisable
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.osgi.factory.descriptor;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.RequirePermission;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.module.ModuleFactory;

@CannotDisable
@RequirePermission(value={"execute_java"})
public class ModuleTypeModuleDescriptor
extends AbstractModuleDescriptor<ModuleDescriptor<?>> {
    public ModuleTypeModuleDescriptor() {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
    }

    public ModuleDescriptor<?> getModule() {
        throw new UnsupportedOperationException();
    }
}

