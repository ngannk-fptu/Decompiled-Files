/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import java.util.Set;

public class OutputDeviceTypeMacroModuleDescriptorPredicate
implements ModuleDescriptorPredicate<Macro> {
    private final Set<String> deviceTypes;

    public OutputDeviceTypeMacroModuleDescriptorPredicate(Set<String> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public boolean matches(ModuleDescriptor<? extends Macro> moduleDescriptor) {
        if (!(moduleDescriptor instanceof XhtmlMacroModuleDescriptor)) {
            return false;
        }
        XhtmlMacroModuleDescriptor descriptor = (XhtmlMacroModuleDescriptor)moduleDescriptor;
        boolean matched = false;
        for (String deviceType : this.deviceTypes) {
            if (!descriptor.isOutputDeviceTypeSupported(deviceType)) continue;
            matched = true;
            break;
        }
        return matched && descriptor.getPlugin().getPluginState() == PluginState.ENABLED;
    }
}

