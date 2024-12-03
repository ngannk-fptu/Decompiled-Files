/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.template;

import com.atlassian.confluence.plugins.createcontent.BlueprintConstants;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public class BlueprintsDisabledPredicate
implements Predicate<ModuleDescriptor> {
    private final PluginAccessor pluginAccessor;

    public BlueprintsDisabledPredicate(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public boolean test(ModuleDescriptor moduleDescriptor) {
        if (!(moduleDescriptor instanceof WebItemModuleDescriptor)) {
            return false;
        }
        String webItemBlueprintKey = (String)moduleDescriptor.getParams().get(BlueprintConstants.BLUEPRINT_PARAM_KEY);
        boolean webItemHasBlueprint = StringUtils.isNotBlank((CharSequence)webItemBlueprintKey);
        if (!webItemHasBlueprint) {
            return false;
        }
        if (!this.pluginAccessor.isPluginEnabled(moduleDescriptor.getPluginKey())) {
            return false;
        }
        return !this.pluginAccessor.isPluginModuleEnabled(moduleDescriptor.getCompleteKey());
    }
}

