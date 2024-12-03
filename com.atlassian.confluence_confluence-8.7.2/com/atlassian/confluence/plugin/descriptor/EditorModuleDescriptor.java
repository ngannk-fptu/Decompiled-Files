/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.plugin.editor.Editor;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class EditorModuleDescriptor
extends AbstractModuleDescriptor<Editor> {
    private PluginModuleHolder<Editor> editor;

    public EditorModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.editor = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public Editor getModule() {
        return this.editor.getModule();
    }

    public void enabled() {
        super.enabled();
        this.editor.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.editor.disabled();
        super.disabled();
    }
}

