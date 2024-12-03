/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.representations.DefaultLinkBuilder;
import com.atlassian.upm.core.rest.representations.PluginSummaryRepresentation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginCollectionRepresentation {
    @JsonProperty
    final Collection<PluginSummaryRepresentation> plugins;
    @JsonProperty
    final Map<String, URI> links;

    @JsonCreator
    public PluginCollectionRepresentation(@JsonProperty(value="plugins") Collection<PluginSummaryRepresentation> plugins, @JsonProperty(value="links") Map<String, URI> links) {
        this.plugins = ImmutableList.copyOf(plugins);
        this.links = ImmutableMap.copyOf(links);
    }

    PluginCollectionRepresentation(BasePluginRepresentationFactory representationFactory, BaseUriBuilder uriBuilder, DefaultLinkBuilder linkBuilder, Locale locale, List<Plugin> plugins, Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> appPlugins) {
        this.links = linkBuilder.buildLinksFor(uriBuilder.buildPluginCollectionUri()).build();
        this.plugins = ImmutableList.copyOf((Collection)new Plugins.PluginOrdering(locale).sortedCopy(plugins).stream().map(plugin -> representationFactory.createPluginSummaryRepresentation((Plugin)plugin, Option.option(appPlugins.get(plugin.getKey())))).collect(Collectors.toList()));
    }

    public Iterable<PluginSummaryRepresentation> getPlugins() {
        return this.plugins;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }
}

