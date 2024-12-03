/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory
 */
package com.atlassian.plugins.avatar;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory;
import com.atlassian.plugins.avatar.AvatarProviderModuleDescriptor;

public class AvatarProviderModuleDescriptorFactory
extends SingleModuleDescriptorFactory<AvatarProviderModuleDescriptor> {
    public AvatarProviderModuleDescriptorFactory(HostContainer hostContainer) {
        super(hostContainer, "avatar-provider", AvatarProviderModuleDescriptor.class);
    }
}

