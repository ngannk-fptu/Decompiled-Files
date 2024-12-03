/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.renderer.template;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class TemplateRendererModuleDescriptor
extends AbstractModuleDescriptor<TemplateRenderer>
implements PluginModuleFactory<TemplateRenderer> {
    private PluginModuleHolder<TemplateRenderer> module;
    private Collection<String> supportedFileExtensions;

    public TemplateRendererModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.supportedFileExtensions = Collections.unmodifiableCollection(this.getContentsOfChildElements(element, "extension"));
        this.module = PluginModuleHolder.getInstance(this);
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    public TemplateRenderer getModule() {
        return this.module.getModule();
    }

    @Override
    public TemplateRenderer createModule() {
        return (TemplateRenderer)ConfluencePluginUtils.instantiatePluginModule(this.plugin, this.getModuleClass());
    }

    private List<String> getContentsOfChildElements(Element element, String elementName) {
        ArrayList<String> strings = new ArrayList<String>();
        for (Element e : element.elements(elementName)) {
            strings.add(e.getTextTrim());
        }
        return strings;
    }

    public Collection<String> getSupportedFileExtensions() {
        return this.supportedFileExtensions;
    }
}

