/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugins.avatar.AvatarProvider
 *  com.atlassian.plugins.avatar.AvatarProviderModuleDescriptor
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user.avatar;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.avatar.AvatarProvider;
import com.atlassian.plugins.avatar.AvatarProviderModuleDescriptor;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Optional;

public class AvatarProviderAccessor {
    private final PluginAccessor pluginAccessor;

    public AvatarProviderAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public AvatarProvider<User, String> getAvatarProvider() {
        Optional<AvatarProviderModuleDescriptor> avatarProviderModuleDescriptor = this.pluginAccessor.getEnabledModuleDescriptorsByClass(AvatarProviderModuleDescriptor.class).stream().filter(descriptor -> descriptor.getCondition() == null || descriptor.getCondition().shouldDisplay(Collections.emptyMap())).sorted().findFirst();
        if (!avatarProviderModuleDescriptor.isPresent()) {
            throw new IllegalStateException("There should be at least one AvatarProvider module registered in the plugin system.");
        }
        return avatarProviderModuleDescriptor.get().getModule();
    }
}

