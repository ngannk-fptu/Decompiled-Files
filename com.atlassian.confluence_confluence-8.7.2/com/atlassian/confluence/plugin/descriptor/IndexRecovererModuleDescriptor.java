/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.index.IndexRecoverer
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.dom4j.Element;

public class IndexRecovererModuleDescriptor
extends AbstractModuleDescriptor<IndexRecoverer>
implements PluginModuleFactory<IndexRecoverer> {
    private PluginModuleHolder<IndexRecoverer> moduleHolder;
    private JournalIdentifier journalId;
    private String indexDirName;
    private String indexName;

    public IndexRecovererModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.moduleHolder = PluginModuleHolder.getInstance(this);
        this.journalId = new JournalIdentifier(element.elementText("journal-id"));
        this.indexDirName = element.elementText("index-dir-name");
        this.indexName = element.element("index-name") == null ? this.indexDirName : element.elementText("index-name");
    }

    public IndexRecoverer getModule() {
        return this.moduleHolder.getModule();
    }

    public void enabled() {
        super.enabled();
        this.moduleHolder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.moduleHolder.disabled();
        super.disabled();
    }

    public String getIndexName() {
        return this.indexName;
    }

    public String getIndexDirName() {
        return this.indexDirName;
    }

    public JournalIdentifier getJournalId() {
        return this.journalId;
    }

    @Override
    public IndexRecoverer createModule() {
        this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        return (IndexRecoverer)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }
}

