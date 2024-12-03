/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.extensions.ContentTemplateModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.I18nPageTemplate;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPluginPageTemplateHelper
implements PluginPageTemplateHelper {
    private static final Logger log = LoggerFactory.getLogger(DefaultPluginPageTemplateHelper.class);
    private final PageTemplateManager pageTemplateManager;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public DefaultPluginPageTemplateHelper(@ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport PluginAccessor pluginAccessor) {
        this.pageTemplateManager = pageTemplateManager;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public PageTemplate getPageTemplate(PluginTemplateReference pluginTemplateReference) {
        PageTemplate result = this.pageTemplateManager.getPageTemplate(pluginTemplateReference);
        if (result != null) {
            return result;
        }
        result = this.pageTemplateManager.getPageTemplate(PluginTemplateReference.globalTemplateReference((ModuleCompleteKey)pluginTemplateReference.getModuleCompleteKey(), (ModuleCompleteKey)pluginTemplateReference.getReferencingModuleCompleteKey()));
        if (result != null) {
            return result;
        }
        PageTemplate pageTemplateByModuleKey = this.getPageTemplateByModuleKey(pluginTemplateReference.getModuleCompleteKey().getCompleteKey());
        if (pageTemplateByModuleKey == null) {
            throw new IllegalStateException("No PageTemplate found matching reference: " + pluginTemplateReference);
        }
        return pageTemplateByModuleKey;
    }

    @Override
    public PageTemplate getPageTemplate(ContentTemplateRef contentTemplateRef) {
        long id = contentTemplateRef.getTemplateId();
        PageTemplate pageTemplate = id != 0L ? this.pageTemplateManager.getPageTemplate(id) : this.getPageTemplateByModuleKey(contentTemplateRef.getModuleCompleteKey());
        if (pageTemplate == null) {
            throw new IllegalStateException("No PageTemplate found for ContentTemplateRef: " + contentTemplateRef);
        }
        return pageTemplate;
    }

    private PageTemplate getPageTemplateByModuleKey(String moduleCompleteKey) {
        PageTemplate result = null;
        ModuleDescriptor contentTemplateModuleDescriptor = this.pluginAccessor.getEnabledPluginModule(moduleCompleteKey);
        if (contentTemplateModuleDescriptor instanceof ContentTemplateModuleDescriptor) {
            try {
                PageTemplate pluginPageTemplate = ((ContentTemplateModuleDescriptor)contentTemplateModuleDescriptor).getModule();
                if (pluginPageTemplate != null) {
                    result = (PageTemplate)pluginPageTemplate.clone();
                }
            }
            catch (Exception e) {
                log.debug("Error constructing a PageTemplate instance from content template descriptor", (Throwable)e);
            }
        }
        return result;
    }

    @Override
    public List<PageTemplate> getPageTemplates(Space space) {
        ImmutableList.Builder result = ImmutableList.builder();
        HashSet moduleKeys = Sets.newHashSet();
        List pluginPageTemplates = this.pluginAccessor.getEnabledModulesByClass(PageTemplate.class);
        for (PageTemplate pluginPageTemplate : pluginPageTemplates) {
            moduleKeys.add(pluginPageTemplate.getModuleCompleteKey());
            result.add((Object)pluginPageTemplate);
        }
        List databasePageTemplates = space != null ? space.getPageTemplates() : this.pageTemplateManager.getGlobalPageTemplates();
        for (PageTemplate pageTemplate : databasePageTemplates) {
            ModuleCompleteKey moduleCompleteKey;
            if (StringUtils.isBlank((CharSequence)pageTemplate.getPluginKey()) || !moduleKeys.contains(moduleCompleteKey = pageTemplate.getModuleCompleteKey())) continue;
            result.add((Object)pageTemplate);
        }
        return result.build();
    }

    @Override
    public List<I18nPageTemplate> getSystemPageTemplates() {
        LinkedHashMap<ModuleCompleteKey, I18nPageTemplate> result = new LinkedHashMap<ModuleCompleteKey, I18nPageTemplate>();
        List pluginPageTemplates = this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTemplateModuleDescriptor.class);
        for (ContentTemplateModuleDescriptor pluginPageTemplate : pluginPageTemplates) {
            String pluginKey = pluginPageTemplate.getPluginKey();
            if (!this.pluginAccessor.isSystemPlugin(pluginKey)) continue;
            PageTemplate module = pluginPageTemplate.getModule();
            result.put(module.getModuleCompleteKey(), new I18nPageTemplate(pluginPageTemplate.getNameKey(), module));
        }
        List databasePageTemplates = this.pageTemplateManager.getGlobalPageTemplates();
        for (PageTemplate pageTemplate : databasePageTemplates) {
            ModuleCompleteKey moduleCompleteKey;
            String pluginKey = pageTemplate.getPluginKey();
            if (StringUtils.isBlank((CharSequence)pluginKey) || !this.pluginAccessor.isSystemPlugin(pluginKey) || !result.containsKey(moduleCompleteKey = pageTemplate.getModuleCompleteKey())) continue;
            I18nPageTemplate old = (I18nPageTemplate)((Object)result.get(moduleCompleteKey));
            result.put(moduleCompleteKey, new I18nPageTemplate(old.getI18nNameKey(), pageTemplate));
        }
        return ImmutableList.copyOf(result.values());
    }
}

