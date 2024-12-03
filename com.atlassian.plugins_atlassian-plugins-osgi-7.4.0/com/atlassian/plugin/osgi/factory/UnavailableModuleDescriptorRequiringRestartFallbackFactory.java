/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.descriptors.RequiresRestart
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptorRequiringRestart
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.RequiresRestart;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptorRequiringRestart;

public class UnavailableModuleDescriptorRequiringRestartFallbackFactory
implements ModuleDescriptorFactory {
    public static final String DESCRIPTOR_TEXT = "Support for this module is not currently installed.";
    private final ModuleDescriptorFactory underlying;

    public UnavailableModuleDescriptorRequiringRestartFallbackFactory(ModuleDescriptorFactory underlying) {
        this.underlying = underlying;
    }

    private boolean requiresRestart(String type) {
        if (this.underlying.hasModuleDescriptor(type)) {
            return this.underlying.getModuleDescriptorClass(type).getAnnotation(RequiresRestart.class) != null;
        }
        return false;
    }

    public UnrecognisedModuleDescriptorRequiringRestart getModuleDescriptor(String type) {
        if (this.hasModuleDescriptor(type)) {
            UnrecognisedModuleDescriptorRequiringRestart descriptor = new UnrecognisedModuleDescriptorRequiringRestart();
            descriptor.setErrorText(DESCRIPTOR_TEXT);
            return descriptor;
        }
        return null;
    }

    public Class<? extends ModuleDescriptor<?>> getModuleDescriptorClass(String type) {
        if (this.hasModuleDescriptor(type)) {
            return UnrecognisedModuleDescriptorRequiringRestart.class;
        }
        return null;
    }

    public boolean hasModuleDescriptor(String type) {
        return this.requiresRestart(type);
    }
}

