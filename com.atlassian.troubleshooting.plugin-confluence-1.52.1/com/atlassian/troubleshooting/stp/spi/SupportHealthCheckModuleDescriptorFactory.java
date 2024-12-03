/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.stp.spi.SupportHealthCheckModuleDescriptor;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SupportHealthCheckModuleDescriptorFactory
extends SingleModuleDescriptorFactory<SupportHealthCheckModuleDescriptor> {
    @VisibleForTesting
    static final String HEALTHCHECK_ELEM_NAME = "healthcheck";

    @Autowired
    public SupportHealthCheckModuleDescriptorFactory(final ModuleFactory moduleFactory, final ApplicationContext atstApplicationContext, final ApplicationProperties applicationProperties) {
        super(new HostContainer(){

            public <T> T create(Class<T> moduleClass) throws IllegalArgumentException {
                if (SupportHealthCheckModuleDescriptor.class.isAssignableFrom(moduleClass)) {
                    return (T)((Object)new SupportHealthCheckModuleDescriptor(moduleFactory, atstApplicationContext, applicationProperties));
                }
                throw new UnsupportedOperationException("Cannot use this ModuleDescriptorFactory to instantiate " + moduleClass.getName());
            }
        }, HEALTHCHECK_ELEM_NAME, SupportHealthCheckModuleDescriptor.class);
    }
}

