/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.template.PageTemplateGrouper;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPageTemplateGrouper
implements PageTemplateGrouper {
    private final ContentBlueprintManager contentBlueprintManager;

    @Autowired
    public DefaultPageTemplateGrouper(ContentBlueprintManager contentBlueprintManager) {
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public Collection<ContentBlueprint> getSpaceContentBlueprints(@Nullable Space space) {
        List<ContentBlueprint> allGlobalContentBlueprints = this.contentBlueprintManager.getAll(null);
        HashMap result = Maps.newHashMap();
        for (ContentBlueprint blueprint : allGlobalContentBlueprints) {
            String moduleCompleteKey = blueprint.getModuleCompleteKey();
            if (result.containsKey(moduleCompleteKey) && blueprint.isPluginClone()) continue;
            result.put(moduleCompleteKey, blueprint);
        }
        if (space == null) {
            return result.values();
        }
        List<ContentBlueprint> spaceContentBlueprints = this.contentBlueprintManager.getAll(space);
        for (ContentBlueprint blueprint : spaceContentBlueprints) {
            result.put(blueprint.getModuleCompleteKey(), blueprint);
        }
        return result.values();
    }
}

