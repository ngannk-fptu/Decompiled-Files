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

import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public abstract class DefaultFactoryModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    private PluginModuleHolder<T> provider;

    protected DefaultFactoryModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.provider = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public abstract T getModule();

    protected T getModuleFromProvider() {
        return this.provider.getModule();
    }

    public void enabled() {
        super.enabled();
        this.provider.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.provider.disabled();
        super.disabled();
    }
}

