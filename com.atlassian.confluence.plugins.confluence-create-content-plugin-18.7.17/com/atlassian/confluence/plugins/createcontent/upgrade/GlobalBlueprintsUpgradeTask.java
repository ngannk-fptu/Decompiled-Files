/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.upgrade;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.services.TemplateUpdater;
import com.atlassian.confluence.plugins.createcontent.template.BlueprintsDisabledPredicate;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class GlobalBlueprintsUpgradeTask
implements PluginUpgradeTask {
    private final PluginAccessor pluginAccessor;
    private final ContentBlueprintManager contentBlueprintManager;
    private final PluginController pluginController;
    private final TemplateUpdater templateUpdater;
    private final BlueprintStateController blueprintStateController;
    private final PluginPageTemplateHelper pluginPageTemplateHelper;

    @Autowired
    public GlobalBlueprintsUpgradeTask(@ComponentImport PluginAccessor pluginAccessor, ContentBlueprintManager contentBlueprintManager, @ComponentImport PluginController pluginController, TemplateUpdater templateUpdater, BlueprintStateController blueprintStateController, PluginPageTemplateHelper pluginPageTemplateHelper) {
        this.pluginAccessor = pluginAccessor;
        this.contentBlueprintManager = contentBlueprintManager;
        this.pluginController = pluginController;
        this.templateUpdater = templateUpdater;
        this.blueprintStateController = blueprintStateController;
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Persists in Bandana all global disabled blueprints, enabled them back in the plugin system.";
    }

    public Collection<Message> doUpgrade() {
        Collection globallyDisabledBlueprintWebItems = this.pluginAccessor.getModuleDescriptors((Predicate)new BlueprintsDisabledPredicate(this.pluginAccessor));
        ArrayList globallyDisabledBlueprintModuleKeys = Lists.newArrayList();
        for (WebItemModuleDescriptor webItemModuleDescriptor : globallyDisabledBlueprintWebItems) {
            this.pluginController.enablePluginModule(webItemModuleDescriptor.getCompleteKey());
            String blueprintModuleKey = this.getBlueprintModuleKey((ModuleDescriptor)webItemModuleDescriptor);
            ModuleCompleteKey blueprintModuleCompleteKey = new ModuleCompleteKey(webItemModuleDescriptor.getPluginKey(), blueprintModuleKey);
            globallyDisabledBlueprintModuleKeys.add(blueprintModuleCompleteKey);
            this.contentBlueprintManager.getPluginBlueprint(blueprintModuleCompleteKey);
        }
        this.updateGlobalTemplates();
        this.saveGlobalDisabledBlueprintState(globallyDisabledBlueprintModuleKeys);
        return null;
    }

    private void saveGlobalDisabledBlueprintState(List<ModuleCompleteKey> globallyDisabledBlueprintModuleKeys) {
        HashSet globallyDisabledBlueprintIds = Sets.newHashSet();
        for (ModuleCompleteKey disabledBlueprintModuleKey : globallyDisabledBlueprintModuleKeys) {
            ContentBlueprint disabledBlueprint = this.contentBlueprintManager.getPluginBackedContentBlueprint(disabledBlueprintModuleKey, null);
            if (disabledBlueprint == null) continue;
            globallyDisabledBlueprintIds.add(disabledBlueprint.getId());
        }
        this.blueprintStateController.disableBlueprints(globallyDisabledBlueprintIds, null);
    }

    private void updateGlobalTemplates() {
        List<PageTemplate> globallyCustomisedTemplates = this.pluginPageTemplateHelper.getPageTemplates(null);
        this.updateBlueprintContentTemplates(globallyCustomisedTemplates);
    }

    private String getBlueprintModuleKey(ModuleDescriptor webItemModuleDescriptor) {
        Map moduleDescriptorParams = webItemModuleDescriptor.getParams();
        return (String)moduleDescriptorParams.get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key());
    }

    private void updateBlueprintContentTemplates(List<PageTemplate> pageTemplates) {
        for (PageTemplate pageTemplate : pageTemplates) {
            this.templateUpdater.updateContentTemplateRef(pageTemplate);
        }
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    }
}

