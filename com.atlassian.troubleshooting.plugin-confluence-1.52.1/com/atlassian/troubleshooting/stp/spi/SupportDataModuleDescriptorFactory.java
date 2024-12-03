/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.troubleshooting.stp.spi.SupportDataModuleDescriptor;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportDataModuleDescriptorFactory
extends SingleModuleDescriptorFactory<SupportDataModuleDescriptor> {
    @VisibleForTesting
    static final String SUPPORT_DATA_ELEM_NAME = "support-data";

    @Autowired
    public SupportDataModuleDescriptorFactory(final ModuleFactory moduleFactory) {
        super(new HostContainer(){

            public <T> T create(Class<T> moduleClass) throws IllegalArgumentException {
                if (SupportDataModuleDescriptor.class.isAssignableFrom(moduleClass)) {
                    return (T)new SupportDataModuleDescriptor(moduleFactory);
                }
                throw new UnsupportedOperationException("Cannot use this ModuleDescriptorFactory to instantiate " + moduleClass.getName());
            }
        }, SUPPORT_DATA_ELEM_NAME, SupportDataModuleDescriptor.class);
    }
}

