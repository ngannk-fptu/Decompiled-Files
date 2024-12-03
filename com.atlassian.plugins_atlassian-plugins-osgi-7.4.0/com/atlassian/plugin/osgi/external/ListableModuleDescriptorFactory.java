/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 */
package com.atlassian.plugin.osgi.external;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import java.util.Set;

public interface ListableModuleDescriptorFactory
extends ModuleDescriptorFactory {
    public Iterable<String> getModuleDescriptorKeys();

    public Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses();
}

