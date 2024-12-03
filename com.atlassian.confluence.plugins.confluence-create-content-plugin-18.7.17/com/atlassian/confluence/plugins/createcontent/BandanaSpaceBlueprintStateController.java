/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
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
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BandanaSpaceBlueprintStateController
extends AbstractBandanaBlueprintStateController
implements SpaceBlueprintStateController {
    private static final String DISABLED_SPACE_BLUEPRINT_ITEMS = "com.atlassian.confluence.space.blueprints.disabled";
    private SpaceBlueprintManager spaceBlueprintManager;

    @Autowired
    public BandanaSpaceBlueprintStateController(@ComponentImport BandanaManager bandanaManager, SpaceBlueprintManager spaceBlueprintManager, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport PluginAccessor pluginAccessor) {
        super(bandanaManager, webInterfaceManager, pluginAccessor);
        this.spaceBlueprintManager = spaceBlueprintManager;
    }

    @Override
    public void enableSpaceBlueprint(UUID blueprintId) {
        this.enableBlueprint(blueprintId, null, DISABLED_SPACE_BLUEPRINT_ITEMS);
    }

    @Override
    public void disableSpaceBlueprint(UUID blueprintId) {
        this.disableBlueprint(blueprintId, null, DISABLED_SPACE_BLUEPRINT_ITEMS);
    }

    @Override
    public void disableSpaceBlueprints(Set<UUID> blueprintIds) {
        this.disableBlueprints(blueprintIds, null, DISABLED_SPACE_BLUEPRINT_ITEMS);
    }

    @Override
    public Set<String> getDisabledSpaceBlueprintModuleCompleteKeys() {
        return this.getDisabledSpaceBlueprintModuleCompleteKeys(null, DISABLED_SPACE_BLUEPRINT_ITEMS, this.spaceBlueprintManager);
    }

    @Override
    public Map<UUID, BlueprintState> getAllSpaceBlueprintState(@Nonnull String section, @Nullable ConfluenceUser user) {
        List blueprints = this.spaceBlueprintManager.getAll();
        Map<UUID, BlueprintState> blueprintStateMap = this.buildBlueprintStateMap(section, user, null, DISABLED_SPACE_BLUEPRINT_ITEMS, blueprints);
        return ImmutableMap.copyOf(blueprintStateMap);
    }

    @Override
    public Set<UUID> getDisabledSpaceBlueprintIds() {
        return this.getDisabledBlueprintIds(null, DISABLED_SPACE_BLUEPRINT_ITEMS);
    }
}

