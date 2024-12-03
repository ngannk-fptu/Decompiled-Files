/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.renderer.v2.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.renderer.v2.components.TextConverter;
import com.atlassian.renderer.v2.plugin.RendererComponentsAccessor;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import java.util.List;

public class PluggableWysiwygConverter
extends DefaultWysiwygConverter {
    private RendererComponentsAccessor rendererComponentsAccessor;

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.rendererComponentsAccessor = new RendererComponentsAccessor(pluginAccessor);
    }

    @Override
    protected List<TextConverter> getTextConverterComponents() {
        return this.rendererComponentsAccessor.getActiveTextConverterComponents();
    }
}

