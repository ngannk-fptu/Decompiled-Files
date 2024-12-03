/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.upgrade;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.exceptions.BlueprintPluginNotFoundException;
import com.atlassian.confluence.plugins.createcontent.services.TemplateUpdater;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class SpaceEditedTemplatesUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(SpaceEditedTemplatesUpgradeTask.class);
    private final SpaceManager spaceManager;
    private final PluginPageTemplateHelper pluginPageTemplateHelper;
    private final TemplateUpdater templateUpdater;

    @Autowired
    public SpaceEditedTemplatesUpgradeTask(SpaceManager spaceManager, PluginPageTemplateHelper pluginPageTemplateHelper, TemplateUpdater templateUpdater) {
        this.spaceManager = spaceManager;
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
        this.templateUpdater = templateUpdater;
    }

    public int getBuildNumber() {
        return 4;
    }

    public String getShortDescription() {
        return "Updates the content template reference of the content blueprint for user-edited templates.";
    }

    public Collection<Message> doUpgrade() {
        List allSpaces = this.spaceManager.getAllSpaces();
        for (Space space : allSpaces) {
            List<PageTemplate> templates = this.pluginPageTemplateHelper.getPageTemplates(space);
            this.updateBlueprintContentTemplates(templates);
        }
        return null;
    }

    private void updateBlueprintContentTemplates(List<PageTemplate> pageTemplates) {
        for (PageTemplate pageTemplate : pageTemplates) {
            try {
                this.templateUpdater.updateContentTemplateRef(pageTemplate);
            }
            catch (BlueprintPluginNotFoundException e) {
                log.warn("Page template '{}' (ID: {}) could not be migrated because a plugin was disabled. Cause: {}", new Object[]{pageTemplate.getTitle(), pageTemplate.getId(), e.getMessage()});
            }
        }
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    }
}

