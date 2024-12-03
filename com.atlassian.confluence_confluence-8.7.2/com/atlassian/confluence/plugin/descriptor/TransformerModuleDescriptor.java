/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class TransformerModuleDescriptor
extends AbstractModuleDescriptor<Transformer>
implements PluginModuleFactory<Transformer> {
    private static final String ATTR_CHAIN = "chain";
    private static final String ATTR_WEIGHT = "weight";
    private String transformerChain;
    private int transformerWeight;
    private final PluginModuleHolder<Transformer> module = PluginModuleHolder.getInstance(this);

    public TransformerModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.transformerChain = this.getAttribute(element, ATTR_CHAIN);
        try {
            this.transformerWeight = Integer.parseInt(this.getAttribute(element, ATTR_WEIGHT));
        }
        catch (NumberFormatException e) {
            throw new PluginParseException("The module attribute 'weight' must be an integer.", (Throwable)e);
        }
    }

    public void enabled() {
        super.enabled();
        this.module.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.module.disabled();
        super.disabled();
    }

    private String getAttribute(Element element, String attributeName) throws PluginParseException {
        String attribute = element.attributeValue(attributeName);
        if (StringUtils.isBlank((CharSequence)attribute)) {
            throw new PluginParseException("The module attribute '" + attributeName + "' must be supplied and must be non-blank.");
        }
        return attribute;
    }

    public String getTransformerChain() {
        return this.transformerChain;
    }

    public int getTransformerWeight() {
        return this.transformerWeight;
    }

    @Override
    public Transformer createModule() {
        return (Transformer)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public Transformer getModule() {
        return this.module.getModule();
    }
}

