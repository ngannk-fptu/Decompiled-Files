/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.TemplateUpdater;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultTemplateUpdater
implements TemplateUpdater {
    private final ContentBlueprintManager contentBlueprintManager;

    @Autowired
    public DefaultTemplateUpdater(ContentBlueprintManager contentBlueprintManager) {
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public void updateContentTemplateRef(PageTemplate template) {
        this.updateBlueprintForTemplate(template, false);
    }

    @Override
    public void revertContentTemplateRef(PageTemplate template) {
        this.updateBlueprintForTemplate(template, true);
    }

    private void updateBlueprintForTemplate(PageTemplate template, boolean revertTemplateRef) {
        String blueprintPluginKey = template.getReferencingPluginKey();
        String blueprintModuleKey = template.getReferencingModuleKey();
        if (StringUtils.isBlank((CharSequence)blueprintPluginKey)) {
            return;
        }
        if (StringUtils.isBlank((CharSequence)blueprintModuleKey)) {
            throw new IllegalStateException("PageTemplate is in invalid state, no module key but plugin key is: " + blueprintPluginKey);
        }
        ModuleCompleteKey blueprintModuleCompleteKey = new ModuleCompleteKey(blueprintPluginKey, blueprintModuleKey);
        ContentBlueprint contentBlueprint = this.contentBlueprintManager.getOrCreateCustomBlueprint(blueprintModuleCompleteKey, template.getSpace());
        ContentTemplateRef contentTemplateRef = this.findUpdatedContentTemplateRef(template.getModuleCompleteKey(), contentBlueprint);
        long templateId = revertTemplateRef ? 0L : template.getId();
        contentTemplateRef.setTemplateId(templateId);
        this.contentBlueprintManager.update(contentBlueprint);
    }

    private ContentTemplateRef findUpdatedContentTemplateRef(ModuleCompleteKey moduleCompleteKey, ContentBlueprint contentBlueprint) {
        String updatedTemplateModuleKey = moduleCompleteKey.getCompleteKey();
        List<ContentTemplateRef> contentTemplateRefs = contentBlueprint.getContentTemplateRefs();
        for (ContentTemplateRef contentTemplateRef : contentTemplateRefs) {
            String existingKey = contentTemplateRef.getModuleCompleteKey();
            if (!existingKey.equals(updatedTemplateModuleKey)) continue;
            return contentTemplateRef;
        }
        ContentTemplateRef indexPageTemplateRef = contentBlueprint.getIndexPageTemplateRef();
        if (indexPageTemplateRef != null && indexPageTemplateRef.getModuleCompleteKey().equals(updatedTemplateModuleKey)) {
            return indexPageTemplateRef;
        }
        throw new IllegalStateException("No matching content-template ref found for blueprint: " + contentBlueprint.getModuleCompleteKey());
    }
}

