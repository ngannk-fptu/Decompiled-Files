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
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class AttachmentTextExtractorModuleDescriptor
extends AbstractModuleDescriptor<AttachmentTextExtractor>
implements Comparable<AttachmentTextExtractorModuleDescriptor>,
PluginModuleFactory<AttachmentTextExtractor> {
    private static final Logger log = LoggerFactory.getLogger(AttachmentTextExtractorModuleDescriptor.class);
    private PluginModuleHolder<AttachmentTextExtractor> module;
    private int priority;

    public AttachmentTextExtractorModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.priority = this.determinePriority(element);
        if (this.priority <= 0) {
            log.warn("Unable to determine priority for extractor module " + this.getCompleteKey());
            this.priority = 10;
        }
        this.module = PluginModuleHolder.getInstance(this);
    }

    private int determinePriority(Element element) {
        String value;
        Attribute att = element.attribute("priority");
        if (att != null && StringUtils.isNotBlank((CharSequence)(value = att.getValue()))) {
            return Integer.parseInt(value);
        }
        return 0;
    }

    public AttachmentTextExtractor getModule() {
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

    @Override
    public int compareTo(AttachmentTextExtractorModuleDescriptor other) {
        if (other.priority != this.priority) {
            return other.priority - this.priority;
        }
        return this.getCompleteKey().compareTo(other.getCompleteKey());
    }

    @Override
    public AttachmentTextExtractor createModule() {
        return (AttachmentTextExtractor)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

