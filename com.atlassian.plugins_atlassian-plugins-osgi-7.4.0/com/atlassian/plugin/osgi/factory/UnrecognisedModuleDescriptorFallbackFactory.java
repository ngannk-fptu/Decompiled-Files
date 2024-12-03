/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UnrecognisedModuleDescriptorFallbackFactory
implements ModuleDescriptorFactory {
    private static final Logger log = LoggerFactory.getLogger(UnrecognisedModuleDescriptorFallbackFactory.class);
    public static final String DESCRIPTOR_TEXT = "Support for this module is not currently installed.";

    UnrecognisedModuleDescriptorFallbackFactory() {
    }

    public UnrecognisedModuleDescriptor getModuleDescriptor(String type) {
        log.info("Unknown module descriptor of type {} registered as an unrecognised descriptor.", (Object)type);
        UnrecognisedModuleDescriptor descriptor = new UnrecognisedModuleDescriptor();
        descriptor.setErrorText(DESCRIPTOR_TEXT);
        return descriptor;
    }

    public boolean hasModuleDescriptor(String type) {
        return true;
    }

    public Class<? extends ModuleDescriptor<?>> getModuleDescriptorClass(String type) {
        return UnrecognisedModuleDescriptor.class;
    }
}

