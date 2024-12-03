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
package com.atlassian.confluence.impl.plugin.descriptor.search;

import com.atlassian.confluence.plugin.module.DefaultPluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.dom4j.Element;

public abstract class AbstractLuceneMapperModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    private Collection<String> handledKeys = new HashSet<String>(1);
    private PluginModuleHolder<T> module;

    protected AbstractLuceneMapperModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        if (element.attribute("handles") != null) {
            this.handledKeys.add(element.attributeValue("handles"));
        }
        Iterator it = element.elementIterator("handles");
        while (it.hasNext()) {
            Element handlesElement = (Element)it.next();
            this.handledKeys.add(handlesElement.getText().trim());
        }
        this.module = PluginModuleHolder.getInstance(this.getModuleFactory());
    }

    public boolean handles(String key) {
        return this.handledKeys.contains(key);
    }

    public Collection<String> getHandledKeys() {
        return new HashSet<String>(this.handledKeys);
    }

    public T getModule() {
        return this.module.getModule();
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    protected PluginModuleFactory<T> getModuleFactory() {
        return new DefaultPluginModuleFactory(this);
    }
}

