/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.Page;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.AvailablePluginCollectionRepresentation;
import com.atlassian.upm.rest.representations.HostStatusRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PurchasedPluginCollectionRepresentation
extends AvailablePluginCollectionRepresentation {
    @JsonProperty
    private final Collection<UnknownPluginEntry> unknownPlugins;
    @JsonProperty
    private final Collection<AvailablePluginCollectionRepresentation.AvailablePluginEntry> incompatiblePlugins;

    @JsonCreator
    public PurchasedPluginCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="linkTemplates") Map<String, String> linkTemplates, @JsonProperty(value="plugins") Collection<AvailablePluginCollectionRepresentation.AvailablePluginEntry> plugins, @JsonProperty(value="hostStatus") HostStatusRepresentation hostStatus, @JsonProperty(value="unknownPlugins") Collection<UnknownPluginEntry> unknownPlugins, @JsonProperty(value="incompatiblePlugins") Collection<AvailablePluginCollectionRepresentation.AvailablePluginEntry> incompatiblePlugins) {
        super(links, linkTemplates, plugins, hostStatus);
        this.unknownPlugins = Collections.unmodifiableList(new ArrayList<UnknownPluginEntry>(unknownPlugins));
        this.incompatiblePlugins = Collections.unmodifiableList(new ArrayList<AvailablePluginCollectionRepresentation.AvailablePluginEntry>(incompatiblePlugins));
    }

    public PurchasedPluginCollectionRepresentation(Collection<AvailableAddonWithVersionBase> plugins, Collection<AvailableAddonWithVersionBase> incompatiblePlugins, Collection<String> unknownPluginKeys, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, HostStatusRepresentation hostStatus, UpmRepresentationFactory representationFactory) {
        super(linkBuilder, plugins, Page.empty(), hostStatus, (? super Integer o) -> uriBuilder.buildPurchasedPluginCollectionUri(), PurchasedPluginCollectionRepresentation.entryWithPurchaseDetails(representationFactory), Collections.singletonMap("update-licenses", uriBuilder.buildPurchasedPluginCheckUri()));
        this.incompatiblePlugins = Collections.unmodifiableList(incompatiblePlugins.stream().map(PurchasedPluginCollectionRepresentation.entryWithPurchaseDetails(representationFactory)).collect(Collectors.toList()));
        this.unknownPlugins = Collections.unmodifiableList(unknownPluginKeys.stream().map(PurchasedPluginCollectionRepresentation.unknownPluginEntry(representationFactory)).collect(Collectors.toList()));
    }

    public Collection<UnknownPluginEntry> getUnknownPlugins() {
        return this.unknownPlugins;
    }

    public Collection<AvailablePluginCollectionRepresentation.AvailablePluginEntry> getIncompatiblePlugins() {
        return this.incompatiblePlugins;
    }

    private static Function<AvailableAddonWithVersionBase, AvailablePluginCollectionRepresentation.AvailablePluginEntry> entryWithPurchaseDetails(UpmRepresentationFactory representationFactory) {
        return a -> representationFactory.createAvailablePluginEntry(a.getAddonBase(), a.getVersionBase());
    }

    private static Function<String, UnknownPluginEntry> unknownPluginEntry(UpmRepresentationFactory representationFactory) {
        return representationFactory::createUnknownPluginEntry;
    }

    public static class UnknownPluginEntry {
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final Map<String, URI> links;

        @JsonCreator
        public UnknownPluginEntry(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="links") Map<String, URI> links) {
            this.key = key;
            this.name = name;
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        }

        public String getKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        public Map<String, URI> getLinks() {
            return this.links;
        }
    }
}

