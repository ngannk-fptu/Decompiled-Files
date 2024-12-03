/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ModuleType
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher;

import com.atlassian.gadgets.publisher.GadgetModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ModuleType(value={ListableModuleDescriptorFactory.class})
@Component
public class GadgetModuleDescriptorFactory
extends SingleModuleDescriptorFactory<GadgetModuleDescriptor> {
    @Autowired
    public GadgetModuleDescriptorFactory(HostContainer hostContainer) {
        super(hostContainer, "gadget", GadgetModuleDescriptor.class);
    }
}

