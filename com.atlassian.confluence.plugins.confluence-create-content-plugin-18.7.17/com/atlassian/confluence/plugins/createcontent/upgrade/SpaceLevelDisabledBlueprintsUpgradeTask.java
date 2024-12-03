/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.upgrade;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBandanaContext;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class SpaceLevelDisabledBlueprintsUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(SpaceLevelDisabledBlueprintsUpgradeTask.class);
    private static final String CREATE_DIALOG_CONTENT_SECTION = "system.create.dialog/content";
    private final BandanaManager bandanaManager;
    private final ContentBlueprintManager contentBlueprintManager;
    private final SpaceManager spaceManager;
    private final WebInterfaceManager webInterfaceManager;
    private final BlueprintStateController blueprintStateController;

    @Autowired
    public SpaceLevelDisabledBlueprintsUpgradeTask(@ComponentImport BandanaManager bandanaManager, ContentBlueprintManager contentBlueprintManager, @ComponentImport SpaceManager spaceManager, @ComponentImport WebInterfaceManager webInterfaceManager, BlueprintStateController blueprintStateController) {
        this.bandanaManager = bandanaManager;
        this.contentBlueprintManager = contentBlueprintManager;
        this.spaceManager = spaceManager;
        this.webInterfaceManager = webInterfaceManager;
        this.blueprintStateController = blueprintStateController;
    }

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Updates the Bandana entries for space-level disabled blueprints, so they are referenced using UUIDs.";
    }

    public Collection<Message> doUpgrade() {
        List allSpaces = this.spaceManager.getAllSpaces();
        Iterable enabledWebItems = this.webInterfaceManager.getItems(CREATE_DIALOG_CONTENT_SECTION).stream().filter(webItem -> {
            if (webItem == null) {
                return false;
            }
            String moduleKey = (String)webItem.getParams().get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key());
            if (moduleKey == null) {
                log.warn("Can't find module key for web item {}", webItem);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        for (Space space : allSpaces) {
            this.updateSpaceDisabledBlueprints(space, enabledWebItems);
        }
        return null;
    }

    private void updateSpaceDisabledBlueprints(Space space, Iterable<WebItemModuleDescriptor> enabledWebItems) {
        SpaceBandanaContext bandanaContext = new SpaceBandanaContext(space);
        Iterable spaceDisabledWebItemModuleCompleteKeys = this.bandanaManager.getKeys((BandanaContext)bandanaContext);
        if (!spaceDisabledWebItemModuleCompleteKeys.iterator().hasNext()) {
            return;
        }
        HashSet spaceDisabledBlueprintIds = Sets.newHashSet();
        for (String disabledWebItemModuleCompleteKey : spaceDisabledWebItemModuleCompleteKeys) {
            ModuleCompleteKey blueprintModuleCompleteKey = this.findBlueprintModuleCompleteKey(enabledWebItems, disabledWebItemModuleCompleteKey);
            if (blueprintModuleCompleteKey != null) {
                ContentBlueprint contentBlueprint = this.contentBlueprintManager.getOrCreateCustomBlueprint(blueprintModuleCompleteKey, space);
                spaceDisabledBlueprintIds.add(contentBlueprint.getId());
            }
            this.bandanaManager.removeValue((BandanaContext)bandanaContext, disabledWebItemModuleCompleteKey);
        }
        this.blueprintStateController.disableBlueprints(spaceDisabledBlueprintIds, space);
    }

    private ModuleCompleteKey findBlueprintModuleCompleteKey(Iterable<WebItemModuleDescriptor> webItems, String webItemModuleCompleteKey) {
        for (WebItemModuleDescriptor webItem : webItems) {
            if (!webItemModuleCompleteKey.equals(webItem.getCompleteKey())) continue;
            String moduleKey = (String)webItem.getParams().get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key());
            return new ModuleCompleteKey(webItem.getPluginKey(), moduleKey);
        }
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    }
}

