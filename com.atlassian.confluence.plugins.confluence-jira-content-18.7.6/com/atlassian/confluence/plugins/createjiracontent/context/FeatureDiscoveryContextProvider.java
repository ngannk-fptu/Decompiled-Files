/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.createjiracontent.context;

import com.atlassian.confluence.plugins.createjiracontent.services.FeatureDiscoveryService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class FeatureDiscoveryContextProvider
implements ContextProvider {
    private final FeatureDiscoveryService featureDiscoveryService;

    public FeatureDiscoveryContextProvider(FeatureDiscoveryService featureDiscoveryService) {
        this.featureDiscoveryService = featureDiscoveryService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> params) {
        HashMap context = Maps.newHashMap();
        ConfluenceUser user = (ConfluenceUser)params.get("user");
        if (user != null) {
            context.put("shouldShowDiscovery", !this.featureDiscoveryService.hasUserDiscovered(user));
        } else {
            context.put("shouldShowDiscovery", false);
        }
        return context;
    }
}

