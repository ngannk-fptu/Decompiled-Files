/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import com.atlassian.analytics.client.eventfilter.whitelist.AnalyticsWhitelistModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class AnalyticsWhitelistModuleDescriptorFactory
implements ListableModuleDescriptorFactory {
    public static final String XML_ELEMENT_NAME = "analytics-whitelist";
    private final ModuleFactory moduleFactory;

    public AnalyticsWhitelistModuleDescriptorFactory(ModuleFactory moduleFactory) {
        this.moduleFactory = Objects.requireNonNull(moduleFactory);
    }

    public Iterable<String> getModuleDescriptorKeys() {
        return Collections.singleton(XML_ELEMENT_NAME);
    }

    public Set<Class<? extends ModuleDescriptor>> getModuleDescriptorClasses() {
        return Collections.singleton(AnalyticsWhitelistModuleDescriptor.class);
    }

    public ModuleDescriptor<?> getModuleDescriptor(String s) {
        return new AnalyticsWhitelistModuleDescriptor(this.moduleFactory);
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(String s) {
        return AnalyticsWhitelistModuleDescriptor.class;
    }

    public boolean hasModuleDescriptor(String type) {
        return XML_ELEMENT_NAME.equals(type);
    }
}

