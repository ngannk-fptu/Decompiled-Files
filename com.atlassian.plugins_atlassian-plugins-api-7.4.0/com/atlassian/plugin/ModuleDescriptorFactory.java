/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import com.atlassian.plugin.ModuleDescriptor;

public interface ModuleDescriptorFactory {
    public ModuleDescriptor<?> getModuleDescriptor(String var1) throws IllegalAccessException, InstantiationException, ClassNotFoundException;

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String var1);

    public boolean hasModuleDescriptor(String var1);
}

