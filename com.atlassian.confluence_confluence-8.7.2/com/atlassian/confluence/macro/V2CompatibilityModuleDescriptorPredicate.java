/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 *  com.atlassian.renderer.v2.macro.Macro
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.plugin.descriptor.CustomMacroModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.renderer.v2.macro.Macro;

public class V2CompatibilityModuleDescriptorPredicate
implements ModuleDescriptorPredicate<Macro> {
    public boolean matches(ModuleDescriptor moduleDescriptor) {
        return moduleDescriptor instanceof CustomMacroModuleDescriptor && !((CustomMacroModuleDescriptor)moduleDescriptor).hasBody();
    }
}

