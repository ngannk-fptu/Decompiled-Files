/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ModuleType
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl.provider;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ModuleType;
import com.atlassian.whisper.plugin.impl.provider.MessagesProviderModuleDescriptor;
import javax.inject.Inject;
import javax.inject.Named;

@ModuleType(value={ListableModuleDescriptorFactory.class})
@Named
public class MessagesProviderModuleTypeFactory
extends SingleModuleDescriptorFactory<MessagesProviderModuleDescriptor> {
    @Inject
    public MessagesProviderModuleTypeFactory(HostContainer hostContainer) {
        super(hostContainer, "whisper-messages-provider", MessagesProviderModuleDescriptor.class);
    }
}

