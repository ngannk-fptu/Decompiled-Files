/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformer;
import java.util.Map;

@Deprecated
public class WebResourceTransformerModuleDescriptor
extends AbstractModuleDescriptor<WebResourceTransformer> {
    private final WebResourceIntegration webResourceIntegration;

    public WebResourceTransformerModuleDescriptor(ModuleFactory moduleFactory, WebResourceIntegration webResourceIntegration) {
        super(moduleFactory);
        this.webResourceIntegration = webResourceIntegration;
    }

    public WebResourceTransformer getModule() {
        String moduleAttributeName = this.getQualifiedAttributeName(WebResourceTransformerModuleDescriptor.class, this.moduleClass.toString());
        Map<String, Object> requestCache = this.webResourceIntegration.getRequestCache();
        WebResourceTransformer module = (WebResourceTransformer)requestCache.get(moduleAttributeName);
        if (module == null) {
            module = (WebResourceTransformer)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
            requestCache.put(moduleAttributeName, module);
        }
        return module;
    }

    private String getQualifiedAttributeName(Class enclosingClass, String attributeName) {
        return enclosingClass.getName() + "." + attributeName;
    }
}

