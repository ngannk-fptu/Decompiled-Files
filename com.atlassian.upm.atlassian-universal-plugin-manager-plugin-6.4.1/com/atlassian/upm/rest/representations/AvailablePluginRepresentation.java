/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Predicate
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.LicenseType;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.representations.RestartState;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.request.rest.representations.PluginRequestRepresentation;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.CategoryRepresentation;
import com.atlassian.upm.rest.representations.IconRepresentation;
import com.atlassian.upm.rest.representations.PacVersionDetailsRepresentation;
import com.atlassian.upm.rest.representations.PluginLicenseRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.google.common.base.Predicate;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AvailablePluginRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final IconRepresentation logo;
    @JsonProperty
    private final VendorRepresentation vendor;
    @JsonProperty
    private final String version;
    @JsonProperty
    private final String installedVersion;
    @JsonProperty
    private final String license;
    @JsonProperty
    private final String summary;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final PluginLicenseRepresentation licenseDetails;
    @JsonProperty
    private final PacVersionDetailsRepresentation versionDetails;
    @JsonProperty
    private final String restartState;
    @JsonProperty
    private final String marketplaceType;
    @JsonProperty
    private final boolean usesLicensing;
    @JsonProperty
    private final Collection<CategoryRepresentation> categories;
    @JsonProperty
    private final Float rating;
    @JsonProperty
    private final Integer ratingCount;
    @JsonProperty
    private final Integer reviewCount;
    @JsonProperty
    private final Integer downloadCount;
    @JsonProperty
    private final Integer installationCount;
    @JsonProperty
    private final Boolean installed;
    @JsonProperty
    private final Boolean installable;
    @JsonProperty
    private final Boolean preinstalled;
    @JsonProperty
    private final Boolean stable;
    @Deprecated
    @JsonProperty
    private final boolean dataCenterCompatible;
    @JsonProperty
    private final boolean statusDataCenterCompatible;
    @JsonProperty
    private final Collection<PluginRequestRepresentation> requests;
    @JsonProperty
    private final String hamsProductKey;
    @JsonProperty
    private final Integer cloudFreeUsers;

    @JsonCreator
    public AvailablePluginRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="logo") IconRepresentation logo, @JsonProperty(value="vendor") VendorRepresentation vendor, @JsonProperty(value="version") String version, @JsonProperty(value="installedVersion") String installedVersion, @JsonProperty(value="license") String license, @JsonProperty(value="summary") String summary, @JsonProperty(value="description") String description, @JsonProperty(value="licenseDetails") PluginLicenseRepresentation licenseDetails, @JsonProperty(value="versionDetails") PacVersionDetailsRepresentation versionDetails, @JsonProperty(value="restartState") String restartState, @JsonProperty(value="marketplaceType") String marketplaceType, @JsonProperty(value="usesLicensing") boolean usesLicensing, @JsonProperty(value="categories") Collection<CategoryRepresentation> categories, @JsonProperty(value="rating") Float rating, @JsonProperty(value="ratingCount") Integer ratingCount, @JsonProperty(value="reviewCount") Integer reviewCount, @JsonProperty(value="downloadCount") Integer downloadCount, @JsonProperty(value="installationCount") Integer installationCount, @JsonProperty(value="installed") Boolean installed, @JsonProperty(value="installable") Boolean installable, @JsonProperty(value="preinstalled") Boolean preinstalled, @JsonProperty(value="stable") Boolean stable, @JsonProperty(value="dataCenterCompatible") Boolean dataCenterCompatible, @JsonProperty(value="statusDataCenterCompatible") Boolean statusDataCenterCompatible, @JsonProperty(value="requests") Collection<PluginRequestRepresentation> requests, @JsonProperty(value="hamsProductKey") String hamsProductKey, @JsonProperty(value="cloudFreeUsers") Integer cloudFreeUsers) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.key = Objects.requireNonNull(key, "key");
        this.name = Objects.requireNonNull(name, "name");
        this.logo = logo;
        this.vendor = vendor;
        this.version = Objects.requireNonNull(version, "version");
        this.installedVersion = installedVersion;
        this.license = license;
        this.summary = summary;
        this.description = description;
        this.licenseDetails = licenseDetails;
        this.versionDetails = versionDetails;
        this.restartState = restartState;
        this.marketplaceType = marketplaceType;
        this.usesLicensing = usesLicensing;
        this.categories = Collections.unmodifiableList(new ArrayList<CategoryRepresentation>(categories));
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.reviewCount = reviewCount;
        this.downloadCount = downloadCount;
        this.installationCount = installationCount;
        this.installed = installed;
        this.installable = installable;
        this.preinstalled = preinstalled;
        this.stable = stable;
        this.dataCenterCompatible = dataCenterCompatible == null ? false : dataCenterCompatible;
        this.statusDataCenterCompatible = statusDataCenterCompatible == null ? false : statusDataCenterCompatible;
        this.requests = Collections.unmodifiableList(new ArrayList<PluginRequestRepresentation>(requests));
        this.hamsProductKey = hamsProductKey;
        this.cloudFreeUsers = cloudFreeUsers;
    }

    AvailablePluginRepresentation(Addon addon, AddonVersion version, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PluginRetriever pluginRetriever, ApplicationProperties applicationProperties, PermissionEnforcer permissionEnforcer, UpmRepresentationFactory factory, PluginRequestStore requestStore, PluginLicenseRepository licenseRepository, UpmInformation upm, HostApplicationDescriptor hostApplicationDescriptor, UpmAppManager appManager) {
        String pluginKey = addon.getKey();
        Option<Plugin> installedPlugin = pluginRetriever.getPlugin(pluginKey);
        Option<PluginLicense> pluginLicense = licenseRepository.getPluginLicense(pluginKey);
        LinksMapBuilder links = linkBuilder.buildLinksForAvailablePluginDetail(uriBuilder.buildAvailablePluginUri(pluginKey), installedPlugin, pluginKey, pluginLicense, addon, version).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "available", uriBuilder.buildAvailablePluginCollectionUri(UpmMarketplaceFilter.RECENTLY_UPDATED, Option.none(String.class), 0)).put("installed", uriBuilder.buildPluginCollectionUri()).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, "manage", uriBuilder.buildUpmTabPluginUri("manage", pluginKey)).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "recommendations", uriBuilder.buildRecommendedPluginCollectionUri(pluginKey));
        if (installedPlugin.isDefined()) {
            links.putIfPermitted(Permission.CREATE_PLUGIN_REQUEST, installedPlugin, "request", uriBuilder.buildPluginRequestCollectionResourceUri()).putIfPermitted(Permission.MANAGE_PLUGIN_REQUESTS, installedPlugin, "dismiss-request", uriBuilder.buildPluginRequestDismissCollectionResourceUri(pluginKey));
        }
        this.links = links.build();
        this.version = (String)version.getName().getOrElse((Object)"");
        this.license = (String)version.getLicenseType().map(LicenseType::getName).getOrElse((Object)"");
        this.installable = MarketplacePlugins.isInstallable(version, applicationProperties);
        this.stable = !version.isBeta();
        this.dataCenterCompatible = version.isDataCenterCompatible();
        this.statusDataCenterCompatible = version.isDataCenterStatusCompatible();
        this.marketplaceType = MarketplacePlugins.getMarketplaceTypeFromPaymentModel(version.getPaymentModel());
        this.usesLicensing = version.getPaymentModel() == PaymentModel.PAID_VIA_ATLASSIAN;
        this.versionDetails = new PacVersionDetailsRepresentation(addon, version, installedPlugin, upm, permissionEnforcer, licenseRepository, appManager);
        this.key = pluginKey;
        this.name = addon.getName();
        this.logo = IconRepresentation.newIcon(UpmFugueConverters.toUpmOption(addon.getLogo()));
        this.vendor = (VendorRepresentation)addon.getVendor().map(factory::createVendorRepresentation).getOrElse((Object)null);
        this.installedVersion = installedPlugin.isDefined() ? installedPlugin.get().getPluginInformation().getVersion() : null;
        this.summary = (String)addon.getSummary().getOrElse((Object)"");
        this.description = ((HtmlString)version.getMoreDetails().orElse(addon.getDescription()).getOrElse((Object)HtmlString.html(""))).getHtml();
        this.licenseDetails = (PluginLicenseRepresentation)pluginLicense.filter((Predicate<PluginLicense>)((Predicate)l -> permissionEnforcer.hasPermission(Permission.GET_PLUGIN_LICENSE))).map(l -> factory.createPluginLicenseRepresentation(pluginKey, installedPlugin, Option.some(l))).getOrElse((PluginLicenseRepresentation)null);
        this.restartState = RestartState.toString((PluginRestartState)installedPlugin.map(Plugin::getRestartState).getOrElse(PluginRestartState.NONE));
        List<AddonCategorySummary> categories = Stream.concat(StreamSupport.stream(addon.getCategories().spliterator(), false), StreamSupport.stream(version.getFunctionalCategories().spliterator(), false)).collect(Collectors.toList());
        this.categories = CategoryRepresentation.representUniqueCategories(categories, linkBuilder, uriBuilder);
        this.rating = Float.valueOf(addon.getReviews().getAverageStars());
        this.ratingCount = addon.getReviews().getCount();
        this.reviewCount = addon.getReviews().getCount();
        this.downloadCount = addon.getDistribution().getDownloads();
        this.installationCount = (Integer)addon.getDistribution().getTotalInstalls().getOrElse((Object)null);
        this.installed = installedPlugin.isDefined();
        this.preinstalled = installedPlugin.isDefined() && addon.getDistribution().isBundled();
        this.requests = permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_REQUESTS) ? Collections.unmodifiableList(requestStore.getRequests(this.getKey()).stream().map(factory::createPluginRequestRepresentation).collect(Collectors.toList())) : Collections.emptyList();
        this.hamsProductKey = pluginKey;
        this.cloudFreeUsers = (Integer)addon.getCloudFreeUsers().filter(Plugins.hasCloudFreeUsers(hostApplicationDescriptor)).getOrElse((Object)null);
    }

    public Map<String, URI> getLinks() {
        return this.links;
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

    public URI getDataPrivacyLink() {
        return this.links.get("privacy");
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public IconRepresentation getLogo() {
        return this.logo;
    }

    public VendorRepresentation getVendor() {
        return this.vendor;
    }

    public String getVersion() {
        return this.version;
    }

    public String getInstalledVersion() {
        return this.installedVersion;
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

    public String getMarketplaceType() {
        return this.marketplaceType;
    }

    public boolean isUsesLicensing() {
        return this.usesLicensing;
    }

    public String getRestartState() {
        return this.restartState;
    }

    public Collection<CategoryRepresentation> getCategories() {
        return this.categories;
    }

    public Float getRating() {
        return this.rating;
    }

    public Integer getRatingCount() {
        return this.ratingCount;
    }

    public Integer getReviewCount() {
        return this.reviewCount;
    }

    public Integer getDownloadCount() {
        return this.downloadCount;
    }

    public Integer getInstallationCount() {
        return this.installationCount;
    }

    public Boolean installed() {
        return this.installed;
    }

    public Boolean installable() {
        return this.installable;
    }

    @Deprecated
    public boolean dataCenterCompatible() {
        return this.dataCenterCompatible;
    }

    public boolean isStatusDataCenterCompatible() {
        return this.statusDataCenterCompatible;
    }

    public boolean preinstalled() {
        return this.preinstalled;
    }

    public Collection<PluginRequestRepresentation> getRequests() {
        return this.requests;
    }

    public String getHamsProductKey() {
        return this.hamsProductKey;
    }

    public Integer getCloudFreeUsers() {
        return this.cloudFreeUsers;
    }

    public PluginLicenseRepresentation getLicenseDetails() {
        return this.licenseDetails;
    }
}

