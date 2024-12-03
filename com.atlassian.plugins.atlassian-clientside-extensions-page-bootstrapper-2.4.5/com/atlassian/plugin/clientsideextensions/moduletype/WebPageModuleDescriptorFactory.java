/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ModuleType
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugin.clientsideextensions.moduletype;

import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.clientsideextensions.moduletype.WebPageModuleDescriptor;
import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import org.springframework.stereotype.Component;

@Component
@ModuleType(value={ListableModuleDescriptorFactory.class, ModuleDescriptorFactory.class})
public class WebPageModuleDescriptorFactory
extends SingleModuleDescriptorFactory<WebPageModuleDescriptor> {
    public WebPageModuleDescriptorFactory(HostContainer hostContainer) {
        super(hostContainer, "web-page", WebPageModuleDescriptor.class);
    }
}

