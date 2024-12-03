/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.rest.IconUrlProvider;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultIconUrlProvider
implements IconUrlProvider {
    private final WebResourceUrlProvider webResourceUrlProvider;

    @Autowired
    public DefaultIconUrlProvider(@ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public String getDefaultIconUrl() {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl("com.atlassian.confluence.plugins.confluence-create-content-plugin:resources", "images/preview-default-template.png", UrlMode.AUTO);
    }

    @Override
    public String getIconURL(WebItemModuleDescriptor webItemModuleDescriptor) {
        ResourceLocation resourceLocation = webItemModuleDescriptor.getResourceLocation("download", "icon");
        if (resourceLocation == null) {
            return this.getDefaultIconUrl();
        }
        String iconFileName = resourceLocation.getName();
        if (StringUtils.isBlank((CharSequence)iconFileName)) {
            return this.getDefaultIconUrl();
        }
        String moduleKey = webItemModuleDescriptor.getCompleteKey();
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleKey, iconFileName, UrlMode.AUTO);
    }
}

