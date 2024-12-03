/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginParseException
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.renderer.v2.components.RendererComponent;

public interface PluggableRendererComponentFactory {
    public void init(ModuleDescriptor var1) throws PluginParseException;

    public RendererComponent getRendererComponent();
}

