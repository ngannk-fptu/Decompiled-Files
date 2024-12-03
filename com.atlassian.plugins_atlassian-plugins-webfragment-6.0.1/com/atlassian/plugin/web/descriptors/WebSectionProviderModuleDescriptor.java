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
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.api.provider.WebSectionProvider
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.api.provider.WebSectionProvider;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class WebSectionProviderModuleDescriptor
extends AbstractModuleDescriptor<WebSectionProvider> {
    private final WebInterfaceManager webInterfaceManager;
    private Supplier<WebSectionProvider> sectionSupplier;
    private String location;

    public WebSectionProviderModuleDescriptor(ModuleFactory moduleFactory, WebInterfaceManager webInterfaceManager) {
        super(moduleFactory);
        this.webInterfaceManager = webInterfaceManager;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("The web section provider class is required")});
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@location").withError("Must provide a location that sections should be added to.")});
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        final WebSectionProviderModuleDescriptor that = this;
        this.sectionSupplier = Suppliers.memoize((Supplier)new Supplier<WebSectionProvider>(){

            public WebSectionProvider get() {
                return (WebSectionProvider)WebSectionProviderModuleDescriptor.this.moduleFactory.createModule(WebSectionProviderModuleDescriptor.this.moduleClassName, (ModuleDescriptor)that);
            }
        });
        this.location = element.attributeValue("location");
    }

    public String getLocation() {
        return this.location;
    }

    public WebSectionProvider getModule() {
        return (WebSectionProvider)this.sectionSupplier.get();
    }

    public void enabled() {
        super.enabled();
        this.webInterfaceManager.refresh();
    }

    public void disabled() {
        super.disabled();
        this.webInterfaceManager.refresh();
    }
}

