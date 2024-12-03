/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.createcontent.AbstractBandanaBlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.plugins.createcontent.template.PageTemplateGrouper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BandanaBlueprintStateController
extends AbstractBandanaBlueprintStateController
implements BlueprintStateController {
    private static final String DISABLED_BLUEPRINT_ITEMS = "com.atlassian.confluence.blueprints.disabled";
    private final ContentBlueprintManager contentBlueprintManager;
    private final PageTemplateGrouper pageTemplateGrouper;

    @Autowired
    public BandanaBlueprintStateController(@ComponentImport BandanaManager bandanaManager, ContentBlueprintManager contentBlueprintManager, @ComponentImport WebInterfaceManager webInterfaceManager, PageTemplateGrouper pageTemplateGrouper, @ComponentImport PluginAccessor pluginAccessor) {
        super(bandanaManager, webInterfaceManager, pluginAccessor);
        this.pageTemplateGrouper = pageTemplateGrouper;
        this.contentBlueprintManager = contentBlueprintManager;
    }

    @Override
    public void enableBlueprint(UUID blueprintId, Space space) {
        this.enableBlueprint(blueprintId, space, DISABLED_BLUEPRINT_ITEMS);
    }

    @Override
    public void disableBlueprint(UUID blueprintId, Space space) {
        this.disableBlueprint(blueprintId, space, DISABLED_BLUEPRINT_ITEMS);
    }

    @Override
    public void disableBlueprints(Set<UUID> blueprintIds, Space space) {
        this.disableBlueprints(blueprintIds, space, DISABLED_BLUEPRINT_ITEMS);
    }

    @Override
    public Set<UUID> getDisabledBlueprintIds(Space space) {
        return this.getDisabledBlueprintIds(space, DISABLED_BLUEPRINT_ITEMS);
    }

    @Override
    public Set<String> getDisabledBlueprintModuleCompleteKeys(Space space) {
        return this.getDisabledSpaceBlueprintModuleCompleteKeys(space, DISABLED_BLUEPRINT_ITEMS, this.contentBlueprintManager);
    }

    @Override
    public Map<UUID, BlueprintState> getAllContentBlueprintState(@Nonnull String section, @Nullable ConfluenceUser user, @Nullable Space space) {
        Collection<ContentBlueprint> blueprints = this.pageTemplateGrouper.getSpaceContentBlueprints(space);
        Map<UUID, BlueprintState> blueprintStateMap = this.buildBlueprintStateMap(section, user, space, DISABLED_BLUEPRINT_ITEMS, blueprints);
        blueprintStateMap.put(BlueprintConstants.BLANK_PAGE_BLUEPRINT.getId(), BlueprintState.FULLY_ENABLED);
        blueprintStateMap.put(BlueprintConstants.BLOG_POST_BLUEPRINT.getId(), BlueprintState.FULLY_ENABLED);
        return ImmutableMap.copyOf(blueprintStateMap);
    }
}

