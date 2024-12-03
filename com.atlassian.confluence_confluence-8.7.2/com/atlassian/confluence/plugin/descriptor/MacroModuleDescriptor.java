/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.renderer.v2.macro.Macro
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.renderer.v2.macro.Macro;

public interface MacroModuleDescriptor
extends ModuleDescriptor<Macro> {
    public MacroMetadata getMacroMetadata();
}

