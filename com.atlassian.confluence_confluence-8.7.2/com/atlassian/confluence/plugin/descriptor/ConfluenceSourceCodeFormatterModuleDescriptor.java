/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.macro.code.SourceCodeFormatter
 *  com.atlassian.renderer.v2.macro.code.SourceCodeFormatterModuleDescriptor
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import com.atlassian.renderer.v2.macro.code.SourceCodeFormatterModuleDescriptor;

public class ConfluenceSourceCodeFormatterModuleDescriptor
extends SourceCodeFormatterModuleDescriptor {
    protected SourceCodeFormatter makeFormatterFromClass() {
        return (SourceCodeFormatter)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }
}

