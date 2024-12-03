/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.gson.Gson
 */
package com.atlassian.confluence.plugins.featurediscovery;

import com.atlassian.confluence.plugins.featurediscovery.model.DiscoveredFeature;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DiscoveredPluginFeaturesContextProvider
implements ContextProvider {
    private final FeatureDiscoveryService featureDiscoveryService;
    private final Gson gson;

    public DiscoveredPluginFeaturesContextProvider(FeatureDiscoveryService featureDiscoveryService) {
        this.featureDiscoveryService = featureDiscoveryService;
        this.gson = new Gson();
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> params) {
        ConfluenceUser user = (ConfluenceUser)params.get("user");
        if (user == null) {
            return Collections.emptyMap();
        }
        List<DiscoveredFeature> features = this.featureDiscoveryService.getFeaturesDiscoveredByUser(user);
        HashMultimap featureMap = HashMultimap.create();
        for (DiscoveredFeature feature : features) {
            featureMap.put((Object)feature.getPluginKey(), (Object)feature.getFeatureKey());
        }
        return ImmutableMap.of((Object)"discoveredList", (Object)this.gson.toJson((Object)featureMap.asMap()));
    }
}

