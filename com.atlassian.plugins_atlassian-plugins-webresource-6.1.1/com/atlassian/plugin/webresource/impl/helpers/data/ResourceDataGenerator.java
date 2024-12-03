/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 */
package com.atlassian.plugin.webresource.impl.helpers.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.data.DefaultPluginDataResource;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.helpers.BaseHelpers;
import com.atlassian.plugin.webresource.impl.helpers.ResourceGenerationInfo;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceDataGenerator {
    public Set<PluginDataResource> generate(ResourceGenerationInfo info) {
        HashSet<PluginDataResource> dataResources = new HashSet<PluginDataResource>();
        if (info.getResourcePhase().isPresent()) {
            RequestState requestState = info.getData();
            ResourcePhase resourcePhase = info.getResourcePhase().get();
            List<String> webResourceKeysWithData = new BundleFinder(requestState.getSnapshot()).included(requestState.getIncluded(resourcePhase)).excluded(requestState.getExcluded(), BaseHelpers.isConditionsSatisfied(requestState.getRequestCache(), requestState.getUrlStrategy())).deepFilter(BaseHelpers.isConditionsSatisfied(requestState.getRequestCache(), requestState.getUrlStrategy())).end();
            requestState.getSnapshot().toBundles(webResourceKeysWithData).forEach(bundle -> bundle.getData().forEach((key, value) -> dataResources.add(new DefaultPluginDataResource(bundle.getKey() + '.' + key, (Jsonable)value))));
            requestState.getIncludedData(resourcePhase).entrySet().stream().filter(entry -> !requestState.getExcludedData().contains(entry.getKey())).forEach(data -> dataResources.add(new DefaultPluginDataResource((String)data.getKey(), (Jsonable)data.getValue())));
        }
        return dataResources;
    }
}

