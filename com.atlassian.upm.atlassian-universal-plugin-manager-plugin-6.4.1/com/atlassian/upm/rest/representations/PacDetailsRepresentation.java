/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.PluginVersionPair;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.PacVersionDetailsRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PacDetailsRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String installedVersion;
    @JsonProperty
    private final PacVersionDetailsRepresentation versionDetails;
    @JsonProperty
    private final AvailablePluginUpdateRepresentation update;

    @JsonCreator
    public PacDetailsRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="installedVersion") String installedVersion, @JsonProperty(value="versionDetails") PacVersionDetailsRepresentation versionDetails, @JsonProperty(value="update") AvailablePluginUpdateRepresentation update) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.installedVersion = installedVersion;
        this.versionDetails = versionDetails;
        this.update = update;
    }

    PacDetailsRepresentation(Option<PluginVersionPair> pluginVersionPair, Plugin installedPlugin, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, ApplicationProperties applicationProperties, UpmInformation upm, UpmAppManager appManager, HostLicenseInformation hostLicenseInformation, LicenseDateFormatter dateFormatter) {
        String key = installedPlugin.getPlugin().getKey();
        URI selfLink = uriBuilder.buildPacPluginDetailsResourceUri(key, installedPlugin.getPluginInformation().getVersion());
        LinksMapBuilder links = null;
        PacVersionDetailsRepresentation versionRep = null;
        AvailablePluginUpdateRepresentation updateRep = null;
        for (PluginVersionPair pvs : pluginVersionPair) {
            Addon addon = pvs.getAddon();
            for (AddonVersion specificVer : pvs.getSpecific()) {
                links = linkBuilder.buildLinksForAvailablePluginDetail(selfLink, Option.some(installedPlugin), key, Option.none(PluginLicense.class), addon, specificVer);
                versionRep = new PacVersionDetailsRepresentation(addon, specificVer, Option.some(installedPlugin), upm, permissionEnforcer, licenseRepository, appManager);
            }
            for (AddonVersion latest : pvs.getLatest()) {
                if (latest.getName().equals(UpmFugueConverters.fugueSome(installedPlugin.getVersion())) || !permissionEnforcer.hasPermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI)) continue;
                AvailableAddonWithVersion au = new AvailableAddonWithVersion(addon, latest);
                updateRep = new AvailablePluginUpdateRepresentation(au, installedPlugin, uriBuilder, linkBuilder, upm, licenseRepository, applicationProperties, permissionEnforcer, appManager, hostLicenseInformation, dateFormatter);
            }
        }
        if (links == null) {
            links = linkBuilder.buildLinkForSelf(selfLink);
        }
        links.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "available", uriBuilder.buildAvailablePluginCollectionUri(UpmMarketplaceFilter.RECENTLY_UPDATED, Option.none(String.class), 0)).put("installed", uriBuilder.buildPluginCollectionUri());
        this.versionDetails = versionRep;
        this.update = updateRep;
        this.links = links.build();
        this.installedVersion = installedPlugin.getPluginInformation().getVersion();
    }

    public URI getSelfLink() {
        return this.links.get("self");
    }

    public URI getInstalledLink() {
        return this.links.get("installed");
    }

    public URI getAvailableLink() {
        return this.links.get("available");
    }

    public URI getDetailsLink() {
        return this.links.get("details");
    }

    public String getInstalledVersion() {
        return this.installedVersion;
    }

    public PacVersionDetailsRepresentation getVersionDetails() {
        return this.versionDetails;
    }

    public AvailablePluginUpdateRepresentation getUpdate() {
        return this.update;
    }

    public static class AvailablePluginUpdateRepresentation {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String version;
        @JsonProperty
        private final String license;
        @JsonProperty
        private final String summary;
        @JsonProperty
        private final String description;
        @JsonProperty
        private final PacVersionDetailsRepresentation versionDetails;
        @JsonProperty
        private final boolean installable;
        @JsonProperty
        private final boolean licenseCompatible;
        @JsonProperty
        private final boolean statusDataCenterCompatible;
        @JsonProperty
        private final String dataCenterCutoffDateString;

        @JsonCreator
        public AvailablePluginUpdateRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="version") String version, @JsonProperty(value="license") String license, @JsonProperty(value="summary") String summary, @JsonProperty(value="description") String description, @JsonProperty(value="versionDetails") PacVersionDetailsRepresentation versionDetails, @JsonProperty(value="installable") boolean installable, @JsonProperty(value="licenseCompatible") boolean licenseCompatible, @JsonProperty(value="statusDataCenterCompatible") boolean statusDataCenterCompatible, @JsonProperty(value="dataCenterCutoffDateString") String dataCenterCutoffDateString) {
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
            this.version = Objects.requireNonNull(version, "version");
            this.license = license;
            this.summary = summary;
            this.description = description;
            this.versionDetails = versionDetails;
            this.installable = installable;
            this.licenseCompatible = licenseCompatible;
            this.statusDataCenterCompatible = statusDataCenterCompatible;
            this.dataCenterCutoffDateString = dataCenterCutoffDateString;
        }

        AvailablePluginUpdateRepresentation(AvailableAddonWithVersion update, Plugin installedPlugin, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, UpmInformation upm, PluginLicenseRepository licenseRepository, ApplicationProperties applicationProperties, PermissionEnforcer permissionEnforcer, UpmAppManager appManager, HostLicenseInformation hostLicenseInformation, LicenseDateFormatter dateFormatter) {
            Addon mpacAddon = update.getAddon();
            AddonVersion latestVersion = update.getVersion();
            LinksMapBuilder links = linkBuilder.buildLinkForSelf(uriBuilder.buildPacPluginDetailsResourceUri(installedPlugin.getKey(), (String)latestVersion.getName().getOrElse((Object)""))).put("details", uriBuilder.buildPacPluginDetailsUri(mpacAddon));
            for (URI binary : latestVersion.getArtifactUri()) {
                links.putIfPermitted(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI, Option.some(installedPlugin), "binary", binary);
            }
            this.links = links.build();
            this.version = (String)latestVersion.getName().getOrElse((Object)"");
            this.license = (String)UpmFugueConverters.toUpmOption(latestVersion.getLicenseType()).map(LicenseType::getName).getOrElse("");
            this.summary = (String)latestVersion.getReleaseSummary().getOrElse((Object)"");
            this.description = ((HtmlString)mpacAddon.getDescription().getOrElse((Object)HtmlString.html(""))).getHtml();
            this.versionDetails = new PacVersionDetailsRepresentation(mpacAddon, latestVersion, Option.some(installedPlugin), upm, permissionEnforcer, licenseRepository, appManager);
            this.installable = MarketplacePlugins.isInstallable(latestVersion, applicationProperties);
            this.licenseCompatible = MarketplacePlugins.isLicensedToBeUpdated(update, licenseRepository, hostLicenseInformation);
            this.statusDataCenterCompatible = latestVersion.isDataCenterStatusCompatible();
            this.dataCenterCutoffDateString = (String)MarketplacePlugins.getExpectedDataCenterVersionLicenseExpiryDate(update, licenseRepository, hostLicenseInformation).map(dateFormatter::formatDate).getOrElse((String)null);
        }

        public URI getSelfLink() {
            return this.links.get("self");
        }

        public URI getBinaryLink() {
            return this.links.get("binary");
        }

        public URI getInstalledLink() {
            return this.links.get("installed");
        }

        public URI getAvailableLink() {
            return this.links.get("available");
        }

        public URI getDetailsLink() {
            return this.links.get("details");
        }

        public String getVersion() {
            return this.version;
        }

        public String getLicense() {
            return this.license;
        }

        public String getSummary() {
            return this.summary;
        }

        public String getDescription() {
            return this.description;
        }

        public PacVersionDetailsRepresentation getVersionDetails() {
            return this.versionDetails;
        }

        public boolean isLicenseCompatible() {
            return this.licenseCompatible;
        }

        public boolean isStatusDataCenterCompatible() {
            return this.statusDataCenterCompatible;
        }

        public String getDataCenterCutoffDateString() {
            return this.dataCenterCutoffDateString;
        }
    }
}

