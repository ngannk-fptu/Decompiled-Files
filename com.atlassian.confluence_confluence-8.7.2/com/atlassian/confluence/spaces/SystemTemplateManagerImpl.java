/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.spaces.SystemTemplateManager;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemTemplateManagerImpl
implements SystemTemplateManager {
    private static final Logger log = LoggerFactory.getLogger(SystemTemplateManagerImpl.class);
    private final PageTemplateManager pageTemplateManager;
    private final FormatConverter formatConverter;
    private final PluginAccessor pluginAccessor;

    public SystemTemplateManagerImpl(PageTemplateManager pageTemplateManager, FormatConverter formatConverter, PluginAccessor pluginAccessor) {
        this.pageTemplateManager = pageTemplateManager;
        this.formatConverter = formatConverter;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public String getTemplate(String templateKey, List<Variable> vars) {
        return this.getTemplate(templateKey, vars, null);
    }

    @Override
    public String getTemplate(String templateKey, List<Variable> vars, Page page) {
        String templateContent = "";
        PluginTemplateReference systemTemplateReference = PluginTemplateReference.systemTemplateReference(new ModuleCompleteKey(templateKey));
        PageTemplate systemTemplate = this.pageTemplateManager.getPageTemplate(systemTemplateReference);
        if (systemTemplate == null) {
            systemTemplate = this.getPluginTemplate(templateKey);
        }
        if (systemTemplate != null) {
            templateContent = this.pageTemplateManager.insertVariables(systemTemplate, vars);
        }
        PageContext pageContext = new PageContext(page);
        String editorFormat = this.formatConverter.convertToEditorFormat(templateContent, pageContext);
        try {
            return this.formatConverter.convertToStorageFormat(editorFormat, pageContext);
        }
        catch (XhtmlException e) {
            log.warn("Couldn't convert new space homepage template from editor to storage format", (Throwable)e);
            return "";
        }
    }

    private PageTemplate getPluginTemplate(String templateKey) {
        ModuleDescriptor pluginModule = this.pluginAccessor.getEnabledPluginModule(templateKey);
        return pluginModule != null ? (PageTemplate)pluginModule.getModule() : null;
    }

    @Override
    public void saveTemplate(String templateName, String templateKey, String content) {
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(templateKey);
        PluginTemplateReference systemTemplateReference = PluginTemplateReference.systemTemplateReference(moduleCompleteKey);
        PageTemplate originalPageTemplate = this.pageTemplateManager.getPageTemplate(systemTemplateReference);
        PageTemplate oldTemplate = null;
        if (originalPageTemplate != null) {
            if (content == null) {
                this.pageTemplateManager.removePageTemplate(originalPageTemplate);
                return;
            }
            try {
                oldTemplate = (PageTemplate)originalPageTemplate.clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        } else {
            originalPageTemplate = new PageTemplate();
            originalPageTemplate.setName(templateName);
            originalPageTemplate.setBodyType(BodyType.XHTML);
            originalPageTemplate.setModuleCompleteKey(moduleCompleteKey);
        }
        originalPageTemplate.setContent(content);
        this.pageTemplateManager.savePageTemplate(originalPageTemplate, oldTemplate);
    }
}

