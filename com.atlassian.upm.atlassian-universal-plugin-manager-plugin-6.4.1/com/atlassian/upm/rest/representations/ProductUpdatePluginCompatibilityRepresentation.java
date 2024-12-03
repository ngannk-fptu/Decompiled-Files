/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.RestartState;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProductUpdatePluginCompatibilityRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Collection<PluginEntry> compatible;
    @JsonProperty
    private final Collection<PluginEntry> updateRequired;
    @JsonProperty
    private final Collection<PluginEntry> updateRequiredAfterProductUpdate;
    @JsonProperty
    private final Collection<PluginEntry> incompatible;
    @JsonProperty
    private final Collection<PluginEntry> unknown;

    @JsonCreator
    public ProductUpdatePluginCompatibilityRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="compatible") Collection<PluginEntry> compatible, @JsonProperty(value="updateRequired") Collection<PluginEntry> updateRequired, @JsonProperty(value="updateRequiredAfterProductUpdate") Collection<PluginEntry> updateRequiredAfterProductUpdate, @JsonProperty(value="incompatible") Collection<PluginEntry> incompatible, @JsonProperty(value="unknown") Collection<PluginEntry> unknown) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.compatible = Collections.unmodifiableList(new ArrayList<PluginEntry>(compatible));
        this.updateRequired = Collections.unmodifiableList(new ArrayList<PluginEntry>(updateRequired));
        this.updateRequiredAfterProductUpdate = Collections.unmodifiableList(new ArrayList<PluginEntry>(updateRequiredAfterProductUpdate));
        this.incompatible = Collections.unmodifiableList(new ArrayList<PluginEntry>(incompatible));
        this.unknown = Collections.unmodifiableList(new ArrayList<PluginEntry>(unknown));
    }

    public ProductUpdatePluginCompatibilityRepresentation(UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PluginRetriever pluginRetriever, ProductUpdatePluginCompatibility pluginCompatibility, int productUpdateBuildNumber, Locale locale) {
        ToPluginEntriesFunction toEntries = new ToPluginEntriesFunction(uriBuilder, linkBuilder, pluginRetriever);
        ToUpdatablePluginEntriesFunction toUpdatableEntries = new ToUpdatablePluginEntriesFunction(uriBuilder, linkBuilder, pluginRetriever);
        Collator collator = Collator.getInstance(locale);
        Comparator pluginNameComparator = (a, b) -> collator.compare(a.getName(), b.getName());
        this.links = linkBuilder.buildLinkForSelf(uriBuilder.buildProductUpdatePluginCompatibilityUri(productUpdateBuildNumber)).put("installed", uriBuilder.buildPluginCollectionUri()).putIfPermitted(Permission.GET_PRODUCT_UPDATE_COMPATIBILITY, "product-updates", uriBuilder.buildProductUpdatesUri()).build();
        this.compatible = StreamSupport.stream(pluginCompatibility.getCompatible().spliterator(), false).map(toEntries).sorted(pluginNameComparator).collect(Collectors.toList());
        this.updateRequired = StreamSupport.stream(pluginCompatibility.getUpdateRequired().spliterator(), false).map(toUpdatableEntries).sorted(pluginNameComparator).collect(Collectors.toList());
        this.updateRequiredAfterProductUpdate = StreamSupport.stream(pluginCompatibility.getUpdateRequiredAfterProductUpdate().spliterator(), false).map(toUpdatableEntries).sorted(pluginNameComparator).collect(Collectors.toList());
        this.incompatible = StreamSupport.stream(pluginCompatibility.getIncompatible().spliterator(), false).map(toEntries).sorted(pluginNameComparator).collect(Collectors.toList());
        this.unknown = StreamSupport.stream(pluginCompatibility.getUnknown().spliterator(), false).map(toEntries).sorted(pluginNameComparator).collect(Collectors.toList());
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public Collection<PluginEntry> getCompatible() {
        return this.compatible;
    }

    public Collection<PluginEntry> getUpdateRequired() {
        return this.updateRequired;
    }

    public Collection<PluginEntry> getUpdateRequiredAfterProductUpdate() {
        return this.updateRequiredAfterProductUpdate;
    }

    public Collection<PluginEntry> getIncompatible() {
        return this.incompatible;
    }

    public Collection<PluginEntry> getUnknown() {
        return this.unknown;
    }

    private static class ToUpdatablePluginEntriesFunction
    extends ToPluginEntriesFunction {
        ToUpdatablePluginEntriesFunction(UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PluginRetriever pluginRetriever) {
            super(uriBuilder, linkBuilder, pluginRetriever);
        }

        @Override
        protected Map<String, URI> getLinks(Plugin plugin) {
            return this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "available", this.uriBuilder.buildAvailablePluginUri(plugin.getKey())).putIfPermitted(Permission.MANAGE_PLUGIN_ENABLEMENT, "modify", this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, Option.some(plugin), "plugin-icon", this.uriBuilder.buildPluginIconLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, Option.some(plugin), "plugin-logo", this.uriBuilder.buildPluginLogoLocationUri(plugin.getKey())).build();
        }
    }

    private static class ToPluginEntriesFunction
    implements Function<Plugin, PluginEntry> {
        protected final UpmUriBuilder uriBuilder;
        protected final UpmLinkBuilder linkBuilder;
        protected final PluginRetriever pluginRetriever;

        ToPluginEntriesFunction(UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PluginRetriever pluginRetriever) {
            this.uriBuilder = uriBuilder;
            this.linkBuilder = linkBuilder;
            this.pluginRetriever = pluginRetriever;
        }

        @Override
        public PluginEntry apply(Plugin plugin) {
            return new PluginEntry(this.getLinks(plugin), plugin.getName(), this.pluginRetriever.isPluginEnabled(plugin.getKey()), plugin.getKey(), RestartState.toString(plugin.getRestartState()));
        }

        protected Map<String, URI> getLinks(Plugin plugin) {
            return this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.MANAGE_PLUGIN_ENABLEMENT, "modify", this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, Option.some(plugin), "plugin-icon", this.uriBuilder.buildPluginIconLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, Option.some(plugin), "plugin-logo", this.uriBuilder.buildPluginLogoLocationUri(plugin.getKey())).build();
        }
    }

    public static final class PluginEntry {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String key;
        @JsonProperty
        private final boolean enabled;
        @JsonProperty
        private final String restartState;

        @JsonCreator
        public PluginEntry(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="name") String name, @JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="key") String key, @JsonProperty(value="restartState") String restartState) {
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
            this.name = Objects.requireNonNull(name, "name");
            this.key = Objects.requireNonNull(key, "key");
            this.enabled = enabled;
            this.restartState = restartState;
        }

        public URI getSelfLink() {
            return this.links.get("self");
        }

        public URI getAvailableLink() {
            return this.links.get("available");
        }

        public URI getModifyLink() {
            return this.links.get("modify");
        }

        public URI getPluginIconLink() {
            return this.links.get("plugin-icon");
        }

        public URI getPluginLogoLink() {
            return this.links.get("plugin-logo");
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public String getRestartState() {
            return this.restartState;
        }

        public String toString() {
            return "PluginEntry{links=" + this.links + ", name='" + this.name + '\'' + ", enabled=" + this.enabled + ", restartState='" + this.restartState + '\'' + '}';
        }
    }
}

