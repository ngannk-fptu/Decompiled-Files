/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.HostStatusRepresentation;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstalledMarketplacePluginCollectionRepresentation {
    @JsonProperty
    final Collection<InstalledMarketplacePluginRepresentation> plugins;
    @JsonProperty
    final Map<String, URI> links;
    @JsonProperty
    private final String upmUpdateVersion;
    @JsonProperty
    private final HostStatusRepresentation hostStatus;

    @JsonCreator
    public InstalledMarketplacePluginCollectionRepresentation(@JsonProperty(value="plugins") Collection<InstalledMarketplacePluginRepresentation> plugins, @JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="hostStatus") HostStatusRepresentation hostStatus, @JsonProperty(value="upmUpdateVersion") String upmUpdateVersion) {
        this.plugins = Collections.unmodifiableList(new ArrayList<InstalledMarketplacePluginRepresentation>(plugins));
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.hostStatus = hostStatus;
        this.upmUpdateVersion = upmUpdateVersion;
    }

    InstalledMarketplacePluginCollectionRepresentation(UpmRepresentationFactory representationFactory, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, UpmHostApplicationInformation appInfo, Locale locale, List<Plugin> plugins, Iterable<AvailableAddonWithVersion> updates, Iterable<IncompatiblePluginData> incompatibles, HostStatusRepresentation hostStatus, String upmUpdateVersion) {
        LinksMapBuilder builder = linkBuilder.buildLinksFor(uriBuilder.buildInstalledMarketplacePluginCollectionUri()).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, "alternate", uriBuilder.buildPluginCollectionUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "audit-log", uriBuilder.buildAuditLogFeedUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "audit-log-max-entries", uriBuilder.buildAuditLogMaxEntriesUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "audit-log-purge-after", uriBuilder.buildAuditLogPurgeAfterUri()).putIfPermitted(Permission.MANAGE_AUDIT_LOG, "audit-log-purge-after-manage", uriBuilder.buildAuditLogPurgeAfterUri()).putIfPermitted(Permission.GET_PRODUCT_UPDATE_COMPATIBILITY, "product-updates", uriBuilder.buildProductUpdatesUri()).putIfPermitted(Permission.DISABLE_ALL_USER_INSTALLED, "disable-all", uriBuilder.buildDisableAllIncompatibleUri());
        builder.putIfPermitted(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI, "update-all", uriBuilder.buildUpdateAllUri());
        this.links = builder.build();
        this.plugins = Collections.unmodifiableList(new Plugins.PluginOrdering(locale).sortedCopy(plugins).stream().map(InstalledMarketplacePluginRepresentation.toEntry(representationFactory, appInfo, updates, incompatibles)).collect(Collectors.toList()));
        this.hostStatus = hostStatus;
        this.upmUpdateVersion = upmUpdateVersion;
    }

    public Iterable<InstalledMarketplacePluginRepresentation> getPlugins() {
        return this.plugins;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public HostStatusRepresentation getHostStatus() {
        return this.hostStatus;
    }
}

