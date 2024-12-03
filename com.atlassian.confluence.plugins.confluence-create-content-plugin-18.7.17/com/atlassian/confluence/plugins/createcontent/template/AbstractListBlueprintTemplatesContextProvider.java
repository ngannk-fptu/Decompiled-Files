/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractListBlueprintTemplatesContextProvider
implements ContextProvider {
    protected <T extends PluginBackedBlueprint> Collection<T> getEnabledBlueprints(Collection<T> blueprints, Map<UUID, BlueprintState> blueprintStateMap) {
        return Collections2.filter(blueprints, input -> {
            UUID blueprintId = input.getId();
            BlueprintState blueprintState = (BlueprintState)blueprintStateMap.get(blueprintId);
            return BlueprintState.FULLY_ENABLED.equals(blueprintState);
        });
    }

    protected <T extends PluginBackedBlueprint> Collection<T> getDisplayableBlueprints(Collection<T> blueprints, Map<UUID, BlueprintState> blueprintStateMap, boolean isViewingSpaceTemplateAdmin) {
        return Collections2.filter(blueprints, input -> {
            UUID blueprintId = input.getId();
            BlueprintState blueprintState = (BlueprintState)blueprintStateMap.get(blueprintId);
            if (blueprintState == null) {
                return false;
            }
            if (blueprintState.isDisabledInPluginSystem()) {
                return false;
            }
            if (blueprintState.isDisabledByWebInterfaceManager()) {
                return false;
            }
            return !isViewingSpaceTemplateAdmin || !blueprintState.isDisabledGlobally();
        });
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public abstract Map<String, Object> getContextMap(Map<String, Object> var1);
}

