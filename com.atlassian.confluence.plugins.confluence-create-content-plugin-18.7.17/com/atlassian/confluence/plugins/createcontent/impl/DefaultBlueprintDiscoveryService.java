/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintDiscoveryService;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBlueprintDiscoveryService
implements BlueprintDiscoveryService {
    private final FeatureDiscoveryService featureDiscoveryService;

    @Autowired
    public DefaultBlueprintDiscoveryService(@ComponentImport FeatureDiscoveryService featureDiscoveryService) {
        this.featureDiscoveryService = featureDiscoveryService;
    }

    @Override
    public List<CreateDialogWebItemEntity> discoverRecentlyInstalled(List<CreateDialogWebItemEntity> pluginItems) {
        List<ModuleCompleteKey> blueprintsModuleCompleteKeys = this.getBlueprintModuleCompleteKeys(pluginItems);
        List recentBlueprintsModuleCompleteKeys = this.featureDiscoveryService.getNew(blueprintsModuleCompleteKeys);
        this.updateRecentBlueprintItems(pluginItems, recentBlueprintsModuleCompleteKeys);
        return pluginItems;
    }

    private List<ModuleCompleteKey> getBlueprintModuleCompleteKeys(List<CreateDialogWebItemEntity> pluginItems) {
        ArrayList blueprintKeys = Lists.newArrayList();
        for (CreateDialogWebItemEntity createDialogWebItemEntity : pluginItems) {
            String blueprintModuleCompleteKey = createDialogWebItemEntity.getItemModuleCompleteKey();
            if (!StringUtils.isNotBlank((CharSequence)blueprintModuleCompleteKey)) continue;
            blueprintKeys.add(new ModuleCompleteKey(blueprintModuleCompleteKey));
        }
        return blueprintKeys;
    }

    private void updateRecentBlueprintItems(List<CreateDialogWebItemEntity> pluginItems, List<ModuleCompleteKey> newBlueprintModuleKeys) {
        HashSet newBlueprintModuleKeySet = Sets.newHashSet(newBlueprintModuleKeys);
        for (CreateDialogWebItemEntity createDialogWebItemEntity : pluginItems) {
            String blueprintModuleCompleteKey = createDialogWebItemEntity.getItemModuleCompleteKey();
            if (StringUtils.isBlank((CharSequence)blueprintModuleCompleteKey) || !newBlueprintModuleKeySet.contains(new ModuleCompleteKey(blueprintModuleCompleteKey))) continue;
            createDialogWebItemEntity.setNew(true);
        }
    }
}

