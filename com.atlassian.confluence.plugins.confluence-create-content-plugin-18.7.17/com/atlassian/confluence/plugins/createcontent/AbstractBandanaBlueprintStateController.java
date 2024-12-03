/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.bandana.KeyedBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.google.common.base.Strings
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugins.createcontent.AoBackedManager;
import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.confluence.plugins.createcontent.SpaceBandanaContext;
import com.atlassian.confluence.plugins.createcontent.activeobjects.PluginBackedBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractBandanaBlueprintStateController {
    private final BandanaManager bandanaManager;
    private final WebInterfaceManager webInterfaceManager;
    private final PluginAccessor pluginAccessor;
    private static final Function<String, UUID> getUuidFromString = UUID::fromString;
    private static final Predicate<UUID> isUuidNull = Objects::nonNull;
    private static final Function<UUID, String> getStringFromUuid = UUID::toString;

    protected AbstractBandanaBlueprintStateController(BandanaManager bandanaManager, WebInterfaceManager webInterfaceManager, PluginAccessor pluginAccessor) {
        this.bandanaManager = bandanaManager;
        this.webInterfaceManager = webInterfaceManager;
        this.pluginAccessor = pluginAccessor;
    }

    protected void enableBlueprint(UUID blueprintId, Space space, String bandanaKey) {
        if (blueprintId == null) {
            throw new IllegalArgumentException("Blueprint UUID is required.");
        }
        Object bandanaContext = space == null ? new ConfluenceBandanaContext() : new SpaceBandanaContext(space);
        Set disabledBlueprintsIds = (Set)this.bandanaManager.getValue((BandanaContext)bandanaContext, bandanaKey);
        if (disabledBlueprintsIds != null) {
            disabledBlueprintsIds.remove(blueprintId.toString());
        }
        this.bandanaManager.setValue((BandanaContext)bandanaContext, bandanaKey, (Object)disabledBlueprintsIds);
    }

    protected void disableBlueprint(UUID blueprintId, Space space, String bandanaKey) {
        if (blueprintId == null) {
            throw new IllegalArgumentException("blueprint UUID is required.");
        }
        if (blueprintId.equals(BlueprintConstants.UUID_BLANK_PAGE) || blueprintId.equals(BlueprintConstants.UUID_BLOG_POST)) {
            throw new IllegalArgumentException("You cannot disable this blueprint.");
        }
        Object bandanaContext = space == null ? new ConfluenceBandanaContext() : new SpaceBandanaContext(space);
        Set disabledBlueprintsIds = (Set)this.bandanaManager.getValue((BandanaContext)bandanaContext, bandanaKey);
        if (disabledBlueprintsIds == null) {
            disabledBlueprintsIds = Sets.newHashSet();
        }
        disabledBlueprintsIds.add(blueprintId.toString());
        this.bandanaManager.setValue((BandanaContext)bandanaContext, bandanaKey, (Object)disabledBlueprintsIds);
    }

    protected void disableBlueprints(Set<UUID> blueprintIds, Space space, String bandanaKey) {
        if (blueprintIds == null || blueprintIds.isEmpty()) {
            return;
        }
        Object bandanaContext = space == null ? new ConfluenceBandanaContext() : new SpaceBandanaContext(space);
        Set disabledBlueprintsIds = (Set)this.bandanaManager.getValue((BandanaContext)bandanaContext, bandanaKey);
        if (disabledBlueprintsIds == null) {
            disabledBlueprintsIds = Sets.newHashSet();
        }
        Collection disableBlueprintIds = Collections2.transform(blueprintIds, getStringFromUuid::apply);
        disabledBlueprintsIds.addAll(disableBlueprintIds);
        this.bandanaManager.setValue((BandanaContext)bandanaContext, bandanaKey, (Object)disabledBlueprintsIds);
    }

    protected Set<UUID> getDisabledBlueprintIds(Space space, String bandanaKey) {
        HashSet disabledBlueprintIds = Sets.newHashSet();
        disabledBlueprintIds.addAll(this.getDisabledPluginIds((KeyedBandanaContext)new ConfluenceBandanaContext(), bandanaKey));
        if (space != null) {
            disabledBlueprintIds.addAll(this.getDisabledPluginIds(new SpaceBandanaContext(space), bandanaKey));
        }
        return disabledBlueprintIds;
    }

    protected Set<UUID> getDisabledPluginIds(KeyedBandanaContext context, String bandanaKey) {
        Set disabledBlueprints = (Set)this.bandanaManager.getValue((BandanaContext)context, bandanaKey);
        if (disabledBlueprints == null || disabledBlueprints.isEmpty()) {
            return Sets.newHashSet();
        }
        Collection disableBlueprintIds = Collections2.filter((Collection)Collections2.transform((Collection)disabledBlueprints, getUuidFromString::apply), isUuidNull::test);
        return Sets.newHashSet((Iterable)disableBlueprintIds);
    }

    protected Set<String> getDisabledSpaceBlueprintModuleCompleteKeys(Space space, String bandanaKey, AoBackedManager<? extends PluginBackedBlueprint, ? extends PluginBackedBlueprintAo> blueprintManager) {
        Set<UUID> disabledModuleIds = this.getDisabledBlueprintIds(space, bandanaKey);
        Collection globalDisabledModuleCompleteKeys = Collections2.filter((Collection)Collections2.transform(disabledModuleIds, blueprintId -> {
            PluginBackedBlueprint blueprint = (PluginBackedBlueprint)blueprintManager.getById((UUID)blueprintId);
            if (blueprint != null) {
                return blueprint.getModuleCompleteKey();
            }
            return null;
        }), input -> !Strings.isNullOrEmpty((String)input));
        return Sets.newHashSet((Iterable)globalDisabledModuleCompleteKeys);
    }

    protected Map<UUID, BlueprintState> buildBlueprintStateMap(String section, ConfluenceUser user, Space space, String bandanaKey, Collection<? extends PluginBackedBlueprint> blueprints) {
        HashMap blueprintStateMap = Maps.newHashMap();
        Set<UUID> globallyDisabledBlueprintIds = this.getDisabledBlueprintIds(null, bandanaKey);
        Set spaceDisabledBlueprintIds = space == null ? Collections.emptySet() : this.getDisabledBlueprintIds(space, bandanaKey);
        Collection<String> webInterfaceManagerDisplayableModules = this.getWebInterfaceManagerDisplayableBlueprintModuleKeys(section, user, space);
        for (PluginBackedBlueprint pluginBackedBlueprint : blueprints) {
            String moduleCompleteKey = pluginBackedBlueprint.getModuleCompleteKey();
            UUID blueprintId = pluginBackedBlueprint.getId();
            boolean isPluginBacked = StringUtils.isNotBlank((CharSequence)moduleCompleteKey);
            BlueprintState blueprintState = new BlueprintState.Builder().disabledInPluginSystem(isPluginBacked && !this.pluginAccessor.isPluginModuleEnabled(moduleCompleteKey)).disabledGlobally(globallyDisabledBlueprintIds.contains(blueprintId)).disabledInSpace(spaceDisabledBlueprintIds.contains(blueprintId)).disabledByWebInterfaceManager(isPluginBacked && !webInterfaceManagerDisplayableModules.contains(moduleCompleteKey)).build();
            blueprintStateMap.put(blueprintId, blueprintState);
        }
        return blueprintStateMap;
    }

    private Collection<String> getWebInterfaceManagerDisplayableBlueprintModuleKeys(String section, ConfluenceUser user, Space space) {
        DefaultWebInterfaceContext webInterfaceContext = new DefaultWebInterfaceContext();
        webInterfaceContext.setSpace(space);
        webInterfaceContext.setCurrentUser(user);
        Map webInterfaceContextMap = webInterfaceContext.toMap();
        ImmutableSet.Builder displayableBlueprintKeys = ImmutableSet.builder();
        for (WebItemModuleDescriptor webItemModuleDescriptor : this.webInterfaceManager.getDisplayableItems(section, webInterfaceContextMap)) {
            String blueprintKey = (String)webItemModuleDescriptor.getParams().get(BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key());
            if (StringUtils.isBlank((CharSequence)blueprintKey)) continue;
            displayableBlueprintKeys.add((Object)AbstractBandanaBlueprintStateController.getCompleteKey(webItemModuleDescriptor.getPluginKey(), blueprintKey));
        }
        return displayableBlueprintKeys.build();
    }

    private static String getCompleteKey(String pluginKey, String blueprintKey) {
        return new ModuleCompleteKey(pluginKey, blueprintKey).getCompleteKey();
    }
}

