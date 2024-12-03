/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.web.descriptors.WeightedDescriptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.languages.TranslationTransform;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.web.descriptors.WeightedDescriptor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationTransformModuleDescriptor
extends AbstractModuleDescriptor<TranslationTransform>
implements WeightedDescriptor {
    private static final Logger log = LoggerFactory.getLogger(TranslationTransformModuleDescriptor.class);
    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    private int order;
    private final PluginModuleHolder<TranslationTransform> module = PluginModuleHolder.getInstance(() -> (TranslationTransform)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this));

    public TranslationTransformModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.order = TranslationTransformModuleDescriptor.getOrderFromElement(element);
    }

    public int getWeight() {
        return this.order;
    }

    public TranslationTransform getModule() {
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

    private static int getOrderFromElement(Element element) {
        int order = Integer.MAX_VALUE;
        if (element.element("order") != null) {
            try {
                order = Integer.parseInt(element.element("order").getTextTrim());
            }
            catch (NumberFormatException e) {
                log.warn("Invalid order specified: " + element.element("order").getTextTrim() + ". Should be an integer.", (Throwable)e);
            }
        }
        return order;
    }
}

