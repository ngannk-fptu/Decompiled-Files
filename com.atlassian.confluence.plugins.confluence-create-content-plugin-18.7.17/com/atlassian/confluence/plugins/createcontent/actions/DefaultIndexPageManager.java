/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.actions.IndexPageManager;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={IndexPageManager.class})
public class DefaultIndexPageManager
implements IndexPageManager {
    private static final String INDEX_PAGE_LABEL = "blueprint-index-page";
    private final PageManager pageManager;
    private final BlueprintContentGenerator contentGenerator;
    private final LabelManager labelManager;
    private final I18nResolver i18nResolver;
    private final PluginPageTemplateHelper pluginPageTemplateHelper;

    @Autowired
    public DefaultIndexPageManager(@ComponentImport PageManager pageManager, BlueprintContentGenerator contentGenerator, @ComponentImport LabelManager labelManager, @ComponentImport I18nResolver i18nResolver, PluginPageTemplateHelper pluginPageTemplateHelper) {
        this.pageManager = pageManager;
        this.contentGenerator = contentGenerator;
        this.labelManager = labelManager;
        this.i18nResolver = i18nResolver;
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
    }

    @Override
    @Deprecated
    public Page getOrCreateIndexPage(BlueprintDescriptor blueprintDescriptor, Space space, String desiredTitle) {
        Page existingIndexPage = this.findIndexPage(blueprintDescriptor.getBlueprintKey(), space);
        if (existingIndexPage != null) {
            return existingIndexPage;
        }
        return this.createIndexPage(blueprintDescriptor, space, desiredTitle);
    }

    @Override
    public Page getOrCreateIndexPage(ContentBlueprint blueprint, Space space, String desiredTitle) {
        Page existingIndexPage = this.findIndexPage(new ModuleCompleteKey(blueprint.getModuleCompleteKey()), space);
        if (existingIndexPage != null) {
            return existingIndexPage;
        }
        return this.createIndexPage(blueprint, space, desiredTitle);
    }

    @Deprecated
    private Page createIndexPage(BlueprintDescriptor blueprintDescriptor, Space space, String desiredTitle) {
        ModuleCompleteKey indexTemplate = blueprintDescriptor.getIndexTemplate();
        PluginTemplateReference pluginTemplateReference = PluginTemplateReference.spaceTemplateReference((ModuleCompleteKey)indexTemplate, (ModuleCompleteKey)blueprintDescriptor.getBlueprintKey(), (Space)space);
        PageTemplate pageTemplate = this.pluginPageTemplateHelper.getPageTemplate(pluginTemplateReference);
        ContentTemplateRef indexTemplateRef = new ContentTemplateRef(null, pageTemplate.getId(), indexTemplate.getCompleteKey(), pageTemplate.getName(), false, null);
        ContentBlueprint bp = new ContentBlueprint();
        bp.setIndexPageTemplateRef(indexTemplateRef);
        bp.setIndexKey(blueprintDescriptor.getIndexKey());
        bp.setModuleCompleteKey(blueprintDescriptor.getBlueprintKey().getCompleteKey());
        bp.setCreateResult(blueprintDescriptor.getCreateResult());
        return this.createIndexPage(bp, space, desiredTitle);
    }

    @Override
    public Page createIndexPage(ContentBlueprint blueprint, Space space, String desiredTitle) {
        ModuleCompleteKey blueprintKey = new ModuleCompleteKey(blueprint.getModuleCompleteKey());
        int maxAttempts = 3;
        int attempt = 0;
        while (++attempt <= 3) {
            Page newIndexPage = this.createIndexPageObject(blueprint, space, desiredTitle);
            Label label = DefaultIndexPageManager.getIndexPageSystemLabel(blueprintKey);
            this.labelManager.addLabel((Labelable)newIndexPage, label);
            String title = DefaultIndexPageManager.getIndexPageTitle(attempt, desiredTitle);
            if (this.pageManager.getPage(space.getKey(), title) != null) continue;
            newIndexPage.setTitle(title);
            this.pageManager.saveContentEntity((ContentEntityObject)newIndexPage, (SaveContext)new DefaultSaveContext(true, true, false, PageUpdateTrigger.SPACE_CREATE));
            return newIndexPage;
        }
        throw new IllegalStateException("Failed to create index page for " + blueprintKey + " after 3 attempts");
    }

    private static String getIndexPageTitle(int attempt, String desiredTitle) {
        if (attempt == 1) {
            return desiredTitle;
        }
        return String.format("%s (%s)", desiredTitle, attempt);
    }

    @Override
    public Page findIndexPage(ContentBlueprint blueprint, Space space) {
        return this.findIndexPage(new ModuleCompleteKey(blueprint.getModuleCompleteKey()), space);
    }

    private Page findIndexPage(ModuleCompleteKey moduleCompleteKey, Space space) {
        Label label = DefaultIndexPageManager.getIndexPageSystemLabel(moduleCompleteKey);
        List content = this.labelManager.getCurrentContentForLabelAndSpace(label, space.getKey());
        for (Labelable labelable : content) {
            if (!(labelable instanceof Page)) continue;
            return (Page)labelable;
        }
        return null;
    }

    private static Label getIndexPageSystemLabel(ModuleCompleteKey moduleCompleteKey) {
        return new Label(INDEX_PAGE_LABEL, DefaultIndexPageManager.getBlueprintLabelNamespace(moduleCompleteKey));
    }

    private static Namespace getBlueprintLabelNamespace(ModuleCompleteKey moduleCompleteKey) {
        return Namespace.getNamespace((String)moduleCompleteKey.getCompleteKey());
    }

    private Page createIndexPageObject(ContentBlueprint blueprint, Space space, String desiredTitle) {
        String indexKey = blueprint.getIndexKey();
        ContentTemplateRef contentTemplateRef = blueprint.getIndexPageTemplateRef();
        ModuleCompleteKey blueprintKey = new ModuleCompleteKey(blueprint.getModuleCompleteKey());
        String createFromTemplateLabel = this.i18nResolver.getText("com.atlassian.confluence.plugins.confluence-create-content-plugin.create-from-template.default-index.label", new Serializable[]{desiredTitle});
        BlueprintContext context = new BlueprintContext();
        context.setTemplateLabel(indexKey);
        context.setAnalyticsKey(indexKey);
        context.setSpaceKey(space.getKey());
        context.setBlueprintId(blueprint.getId());
        context.setBlueprintModuleCompleteKey(blueprintKey);
        context.setCreateFromTemplateLabel(createFromTemplateLabel);
        context.put("indexKey", (Object)contentTemplateRef.getModuleCompleteKey());
        return this.contentGenerator.createIndexPageObject(contentTemplateRef, space, context.getMap());
    }
}

