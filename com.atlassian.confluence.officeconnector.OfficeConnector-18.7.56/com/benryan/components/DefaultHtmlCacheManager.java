/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.convert.html.HtmlConversionResult
 *  com.benryan.components.OcSettingsManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.benryan.components.AbstractConversionCacheManager;
import com.benryan.components.AttachmentCacheKey;
import com.benryan.components.HtmlCacheManager;
import com.benryan.components.OcSettingsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="htmlCacheManager")
public class DefaultHtmlCacheManager
extends AbstractConversionCacheManager<AttachmentCacheKey, HtmlConversionResult>
implements HtmlCacheManager {
    @Autowired
    DefaultHtmlCacheManager(@ComponentImport PageManager pageManager, @ComponentImport AttachmentManager fileManager, OcSettingsManager ocSettingsManager, @ComponentImport PluginAccessor pluginAccessor) {
        super(pageManager, fileManager, ocSettingsManager, pluginAccessor);
        this.initCache();
    }

    @Override
    public HtmlConversionResult getHtmlConversionData(AttachmentCacheKey key) {
        return (HtmlConversionResult)this.getFromCache(key);
    }

    @Override
    public void addHtmlConversionData(AttachmentCacheKey key, HtmlConversionResult data) {
        this.putToCache(key, data);
    }
}

