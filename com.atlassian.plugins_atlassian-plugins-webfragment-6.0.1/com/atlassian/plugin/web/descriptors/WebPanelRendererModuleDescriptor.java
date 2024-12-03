/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.web.renderer.WebPanelRenderer;
import org.dom4j.Element;

public class WebPanelRendererModuleDescriptor
extends AbstractModuleDescriptor<WebPanelRenderer> {
    public static final String XML_ELEMENT_NAME = "web-panel-renderer";
    private WebPanelRenderer rendererModule;

    public WebPanelRendererModuleDescriptor(ModuleFactory moduleClassFactory) {
        super(moduleClassFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("The class is required")});
    }

    public void enabled() {
        super.enabled();
        if (!WebPanelRenderer.class.isAssignableFrom(this.getModuleClass())) {
            throw new PluginParseException(String.format("Supplied module class (%s) is not a %s", this.getModuleClass().getName(), WebPanelRenderer.class.getName()));
        }
    }

    public WebPanelRenderer getModule() {
        if (this.rendererModule == null) {
            this.rendererModule = (WebPanelRenderer)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
        }
        return this.rendererModule;
    }
}

