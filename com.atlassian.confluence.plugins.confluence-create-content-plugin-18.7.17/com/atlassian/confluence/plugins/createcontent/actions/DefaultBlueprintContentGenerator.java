/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.pages.templates.TemplateHandler
 *  com.atlassian.confluence.pages.templates.variables.StringVariable
 *  com.atlassian.confluence.pages.templates.variables.Variable
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.NoOpContextProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.pages.templates.TemplateHandler;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.extensions.ContentTemplateModuleDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.NoOpContextProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={BlueprintContentGenerator.class})
public class DefaultBlueprintContentGenerator
implements BlueprintContentGenerator {
    private static final Logger log = LoggerFactory.getLogger(DefaultBlueprintContentGenerator.class);
    public static final String CONTENT_PAGE_TITLE_CONTEXT_KEY = "ContentPageTitle";
    public static final String PAGE_TITLE_PREFIX_CONTEXT_KEY = "ParentPageTitle";
    public static final String CONTENT_TEMPLATE_REF_ID_CONTEXT_KEY = "contentTemplateRefId";
    public static final String USE_PAGE_TEMPLATE_TITLE_CONTEXT_KEY = "UsePageTemplateNameForTitle";
    private final PluginAccessor pluginAccessor;
    private final PluginPageTemplateHelper pluginPageTemplateHelper;
    private final TemplateHandler templateHandler;
    private final I18nResolver i18nResolver;

    @Autowired
    public DefaultBlueprintContentGenerator(@ComponentImport PluginAccessor pluginAccessor, PluginPageTemplateHelper pluginPageTemplateHelper, @ComponentImport TemplateHandler templateHandler, @ComponentImport I18nResolver i18nResolver) {
        this.pluginAccessor = pluginAccessor;
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
        this.templateHandler = templateHandler;
        this.i18nResolver = i18nResolver;
    }

    @Override
    @Deprecated
    public Page generateBlueprintPageObject(PluginTemplateReference pluginTemplateReference, Map<String, ?> context) {
        ModuleCompleteKey contentTemplateKey = pluginTemplateReference.getModuleCompleteKey();
        ContentTemplateModuleDescriptor contentTemplateModule = this.getContentTemplateModuleDescriptor(contentTemplateKey.getCompleteKey());
        Map<String, Object> combinedContext = this.getContentTemplateContext(contentTemplateModule, context);
        PageTemplate contentTemplate = this.pluginPageTemplateHelper.getPageTemplate(pluginTemplateReference);
        Page page = new Page();
        page.setTitle(this.getContentPageTitle(contentTemplate, combinedContext));
        page.setBodyAsString(this.renderTemplate(contentTemplate, combinedContext));
        page.setSpace(pluginTemplateReference.getSpace());
        return page;
    }

    @Override
    public Page generateBlueprintPageObject(CreateBlueprintPageRequest createRequest) {
        Page page = this.generateBlueprintPageObject(createRequest.getContentTemplateRef(), createRequest.getSpace(), createRequest.getContext());
        String blueprintPageCustomTitle = createRequest.getTitle();
        if (StringUtils.isBlank((CharSequence)blueprintPageCustomTitle)) {
            blueprintPageCustomTitle = (String)createRequest.getContext().get("title");
        }
        if (StringUtils.isNotBlank((CharSequence)blueprintPageCustomTitle)) {
            page.setTitle(blueprintPageCustomTitle);
        }
        return page;
    }

    @Override
    public Page generateBlueprintPageObject(ContentTemplateRef contentTemplateRef, Space space, Map<String, Object> context) {
        Map<String, Object> combinedContext;
        String moduleCompleteKey = contentTemplateRef.getModuleCompleteKey();
        if (StringUtils.isNotBlank((CharSequence)moduleCompleteKey)) {
            ContentTemplateModuleDescriptor contentTemplateModule = this.getContentTemplateModuleDescriptor(moduleCompleteKey);
            combinedContext = this.getContentTemplateContext(contentTemplateModule, context);
        } else {
            combinedContext = context;
        }
        PageTemplate contentTemplate = this.pluginPageTemplateHelper.getPageTemplate(contentTemplateRef);
        Page page = new Page();
        page.setTitle(this.getContentPageTitle(contentTemplate, combinedContext));
        page.setBodyAsString(this.renderTemplate(contentTemplate, combinedContext));
        page.setSpace(space);
        return page;
    }

    private String getContentPageTitle(PageTemplate contentTemplate, Map<String, Object> combinedContext) {
        String titlePrefix;
        String title = (String)combinedContext.get(CONTENT_PAGE_TITLE_CONTEXT_KEY);
        if (StringUtils.isBlank((CharSequence)title)) {
            Object useTemplateNameObj = combinedContext.get(USE_PAGE_TEMPLATE_TITLE_CONTEXT_KEY);
            Boolean useTemplateName = useTemplateNameObj instanceof Boolean ? (Boolean)useTemplateNameObj : Boolean.parseBoolean((String)useTemplateNameObj);
            if (useTemplateName != null && useTemplateName.booleanValue()) {
                title = this.i18nResolver.getText(contentTemplate.getTitle());
            } else {
                return "";
            }
        }
        if (StringUtils.isNotBlank((CharSequence)(titlePrefix = (String)combinedContext.get(PAGE_TITLE_PREFIX_CONTEXT_KEY)))) {
            return titlePrefix + " - " + title;
        }
        return title;
    }

    private String renderTemplate(PageTemplate contentTemplate, Map<String, Object> context) {
        List<Variable> variables = DefaultBlueprintContentGenerator.buildTemplateVariables(context);
        String templateContent = contentTemplate.getContent();
        return this.templateHandler.insertVariables((Reader)new StringReader(templateContent), variables);
    }

    private static List<Variable> buildTemplateVariables(Map<String, Object> contextMap) {
        LinkedList variables = Lists.newLinkedList();
        for (Map.Entry<String, Object> contextEntry : contextMap.entrySet()) {
            String value = contextEntry.getValue() != null ? contextEntry.getValue().toString() : "";
            variables.add(new StringVariable(contextEntry.getKey(), value));
        }
        return variables;
    }

    private Map<String, Object> getContentTemplateContext(ContentTemplateModuleDescriptor moduleDescriptor, Map<String, ?> context) {
        ContextProvider contextProvider = moduleDescriptor.getContextProvider();
        if (!(contextProvider instanceof AbstractBlueprintContextProvider) && !(contextProvider instanceof NoOpContextProvider)) {
            log.warn("This Blueprint ContextProvider class should extend AbstractBlueprintContextProvider: " + contextProvider.getClass().getName());
        }
        return contextProvider.getContextMap(context);
    }

    private ContentTemplateModuleDescriptor getContentTemplateModuleDescriptor(String contentTemplateKey) {
        ModuleDescriptor moduleDescriptor = this.pluginAccessor.getEnabledPluginModule(contentTemplateKey);
        Preconditions.checkNotNull((Object)moduleDescriptor, (Object)("module descriptor not found [key='" + contentTemplateKey + "']"));
        return (ContentTemplateModuleDescriptor)moduleDescriptor;
    }

    @Override
    public Page createIndexPageObject(PluginTemplateReference pluginTemplateReference, Map<String, Object> context) {
        PageTemplate contentTemplate = this.pluginPageTemplateHelper.getPageTemplate(pluginTemplateReference);
        String moduleCompleteKey = pluginTemplateReference.getModuleCompleteKey().getCompleteKey();
        return this.createIndexPageObject(pluginTemplateReference.getSpace(), context, contentTemplate, moduleCompleteKey);
    }

    @Override
    public Page createIndexPageObject(ContentTemplateRef contentTemplateRef, Space space, Map<String, Object> context) {
        PageTemplate contentTemplate = this.pluginPageTemplateHelper.getPageTemplate(contentTemplateRef);
        String moduleCompleteKey = contentTemplateRef.getModuleCompleteKey();
        return this.createIndexPageObject(space, context, contentTemplate, moduleCompleteKey);
    }

    private Page createIndexPageObject(Space space, Map<String, Object> context, PageTemplate contentTemplate, String moduleCompleteKey) {
        ContentTemplateModuleDescriptor contentTemplateModule = this.getContentTemplateModuleDescriptor(moduleCompleteKey);
        Map<String, Object> combinedContext = this.getContentTemplateContext(contentTemplateModule, context);
        String indexPageContent = this.renderTemplate(contentTemplate, combinedContext);
        Page indexPage = new Page();
        indexPage.setBodyAsString(indexPageContent);
        indexPage.setSpace(space);
        Page spaceHomePage = space.getHomePage();
        if (spaceHomePage != null) {
            spaceHomePage.addChild(indexPage);
        }
        return indexPage;
    }
}

