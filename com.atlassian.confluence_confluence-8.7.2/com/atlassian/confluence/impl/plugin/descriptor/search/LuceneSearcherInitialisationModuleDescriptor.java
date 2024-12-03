/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.plugin.descriptor.search;

import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class LuceneSearcherInitialisationModuleDescriptor
extends AbstractModuleDescriptor<SearcherInitialisation> {
    private PluginModuleHolder<SearcherInitialisation> searcherInitializer;

    public LuceneSearcherInitialisationModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.searcherInitializer = PluginModuleHolder.getInstanceWithDefaultFactory(this);
    }

    public SearcherInitialisation getModule() {
        return this.searcherInitializer.getModule();
    }

    public void enabled() {
        super.enabled();
        this.searcherInitializer.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.searcherInitializer.disabled();
        super.disabled();
    }
}

