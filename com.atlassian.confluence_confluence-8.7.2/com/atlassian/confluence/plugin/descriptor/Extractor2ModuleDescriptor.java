/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.EnumUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class Extractor2ModuleDescriptor
extends AbstractModuleDescriptor<Extractor2>
implements Comparable<Extractor2ModuleDescriptor>,
PluginModuleFactory<Extractor2> {
    private static final Logger log = LoggerFactory.getLogger(Extractor2ModuleDescriptor.class);
    private PluginModuleHolder<Extractor2> module;
    private int priority;
    private SearchIndex searchIndex;
    private boolean requiresLatestVersion;

    public Extractor2ModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.priority = this.determinePriority(element);
        if (this.priority <= 0) {
            log.info("Unable to determine priority for extractor module " + this.getCompleteKey());
            this.priority = 10;
        }
        this.searchIndex = this.determineSearchIndex(element);
        this.requiresLatestVersion = this.determineIfRequiresLatestVersion(element);
        this.module = PluginModuleHolder.getInstance(this);
    }

    private int determinePriority(Element element) {
        return Extractor2ModuleDescriptor.resolveAttribute(element.attribute("priority"), Integer::parseInt, 0);
    }

    private SearchIndex determineSearchIndex(Element element) {
        return Extractor2ModuleDescriptor.resolveAttribute(element.attribute("index"), enumName -> (SearchIndex)EnumUtils.getEnumIgnoreCase(SearchIndex.class, (String)enumName), SearchIndex.CONTENT);
    }

    private boolean determineIfRequiresLatestVersion(Element element) {
        return Extractor2ModuleDescriptor.resolveAttribute(element.attribute("requires-latest-version"), Boolean::valueOf, true);
    }

    private static <T> T resolveAttribute(Attribute attribute, Function<String, T> mapper, T defaultValue) {
        return Optional.ofNullable(attribute).map(Attribute::getValue).filter(StringUtils::isNotBlank).map(mapper).orElse(defaultValue);
    }

    public Extractor2 getModule() {
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

    public int getPriority() {
        return this.priority;
    }

    public SearchIndex getSearchIndex() {
        return this.searchIndex;
    }

    public boolean requiresLatestVersion() {
        return this.requiresLatestVersion;
    }

    @Override
    public int compareTo(Extractor2ModuleDescriptor other) {
        if (other.priority != this.priority) {
            return other.priority - this.priority;
        }
        return this.getCompleteKey().compareTo(other.getCompleteKey());
    }

    @Override
    public Extractor2 createModule() {
        return (Extractor2)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

