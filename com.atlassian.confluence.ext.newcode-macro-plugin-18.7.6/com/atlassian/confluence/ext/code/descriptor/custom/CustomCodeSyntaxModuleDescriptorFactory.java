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
package com.atlassian.confluence.ext.code.descriptor.custom;

import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntaxModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ModuleType(value={ListableModuleDescriptorFactory.class})
@Component
public class CustomCodeSyntaxModuleDescriptorFactory
extends SingleModuleDescriptorFactory<CustomCodeSyntaxModuleDescriptor> {
    @Autowired
    public CustomCodeSyntaxModuleDescriptorFactory(HostContainer hostContainer) {
        super(hostContainer, "code-syntax", CustomCodeSyntaxModuleDescriptor.class);
    }
}

