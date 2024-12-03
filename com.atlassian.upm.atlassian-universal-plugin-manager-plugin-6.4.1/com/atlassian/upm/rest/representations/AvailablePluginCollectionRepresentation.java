/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonCategorySummary;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.rest.representations.PluginRequestRepresentation;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.CategoryRepresentation;
import com.atlassian.upm.rest.representations.HostStatusRepresentation;
import com.atlassian.upm.rest.representations.IconRepresentation;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class AvailablePluginCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Map<String, String> linkTemplates;
    @JsonProperty
    private final Collection<AvailablePluginEntry> plugins;
    @JsonProperty
    private final HostStatusRepresentation hostStatus;

    @JsonCreator
    public AvailablePluginCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="linkTemplates") Map<String, String> linkTemplates, @JsonProperty(value="plugins") Collection<AvailablePluginEntry> plugins, @JsonProperty(value="hostStatus") HostStatusRepresentation hostStatus) {
        this.plugins = Collections.unmodifiableList(new ArrayList<AvailablePluginEntry>(plugins));
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.linkTemplates = Collections.unmodifiableMap(new HashMap<String, String>(linkTemplates));
        this.hostStatus = hostStatus;
    }

    public AvailablePluginCollectionRepresentation(UpmLinkBuilder linkBuilder, Iterable<AvailableAddonWithVersionBase> addons, Page<AddonSummary> sourceAddons, Map<String, PluginRequest> pluginRequests, HostStatusRepresentation hostStatus, UpmRepresentationFactory representationFactory, Function<? super Integer, URI> uriFromOffset) {
        this(linkBuilder, addons, sourceAddons, hostStatus, uriFromOffset, new SummaryToPluginEntry(representationFactory, pluginRequests), Collections.emptyMap());
    }

    public AvailablePluginCollectionRepresentation(UpmLinkBuilder linkBuilder, Iterable<AvailableAddonWithVersionBase> addons, Page<AddonSummary> sourceAddons, HostStatusRepresentation hostStatus, Function<? super Integer, URI> uriFromOffset, Function<AvailableAddonWithVersionBase, AvailablePluginEntry> toEntry, Map<String, URI> extraLinks) {
        this.links = AvailablePluginCollectionRepresentation.buildPluginsLinks(linkBuilder, sourceAddons, uriFromOffset, extraLinks);
        this.linkTemplates = linkBuilder.buildLinkTemplatesForInstallablePluginCollection();
        this.plugins = Collections.unmodifiableList(StreamSupport.stream(addons.spliterator(), false).map(toEntry).collect(Collectors.toList()));
        this.hostStatus = hostStatus;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public Map<String, String> getLinkTemplates() {
        return this.linkTemplates;
    }

    public Iterable<AvailablePluginEntry> getPlugins() {
        return this.plugins;
    }

    public HostStatusRepresentation getHostStatus() {
        return this.hostStatus;
    }

    protected static Map<String, URI> buildPluginsLinks(UpmLinkBuilder linkBuilder, Page<AddonSummary> addonsSource, Function<? super Integer, URI> uriFromOffset, Map<String, URI> extraLinks) {
        LinksMapBuilder builder = linkBuilder.buildLinksFor(uriFromOffset.apply((Integer)addonsSource.getOffset()));
        addonsSource.safeGetNext().ifPresent(next -> builder.put("next", (URI)uriFromOffset.apply(next.getBounds().getOffset())));
        addonsSource.safeGetPrevious().ifPresent(prev -> builder.put("prev", (URI)uriFromOffset.apply(prev.getBounds().getOffset())));
        builder.putAll(extraLinks);
        return builder.build();
    }

    public static class RequestedPluginEntry
    extends AvailablePluginEntry {
        @JsonProperty
        private final Collection<PluginRequestRepresentation> requests;

        @JsonCreator
        public RequestedPluginEntry(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="name") String name, @JsonProperty(value="summary") String summary, @JsonProperty(value="key") String key, @JsonProperty(value="logo") IconRepresentation logo, @JsonProperty(value="vendor") VendorRepresentation vendor, @JsonProperty(value="categories") Collection<CategoryRepresentation> categories, @JsonProperty(value="rating") Float rating, @JsonProperty(value="ratingCount") Integer ratingCount, @JsonProperty(value="reviewCount") Integer reviewCount, @JsonProperty(value="downloadCount") Integer downloadCount, @JsonProperty(value="installationCount") Integer installationCount, @JsonProperty(value="installed") Boolean installed, @JsonProperty(value="installable") Boolean installable, @JsonProperty(value="preinstalled") Boolean preinstalled, @JsonProperty(value="stable") Boolean stable, @JsonProperty(value="dataCenterCompatible") Boolean dataCenterCompatible, @JsonProperty(value="statusDataCenterCompatible") Boolean statusDataCenterCompatible, @JsonProperty(value="marketplaceType") String marketplaceType, @JsonProperty(value="usesLicensing") boolean usesLicensing, @JsonProperty(value="requests") Collection<PluginRequestRepresentation> requests, @JsonProperty(value="supportType") String supportType, @JsonProperty(value="hamsPluginKey") String hamsPluginKey, @JsonProperty(value="cloudFreeUsers") Integer cloudFreeUsers) {
            super(links, name, summary, key, logo, vendor, categories, rating, ratingCount, reviewCount, downloadCount, installationCount, installed, installable, preinstalled, stable, dataCenterCompatible, statusDataCenterCompatible, marketplaceType, usesLicensing, null, supportType, hamsPluginKey, cloudFreeUsers);
            this.requests = Collections.unmodifiableList(new ArrayList<PluginRequestRepresentation>(requests));
        }

        RequestedPluginEntry(Option<Plugin> installedPlugin, ApplicationProperties applicationProperties, AddonBase plugin, AddonVersionBase version, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, UpmRepresentationFactory representationFactory, Collection<PluginRequestRepresentation> requests, HostApplicationDescriptor hostApplicationDescriptor) {
            super(installedPlugin, applicationProperties, plugin, version, uriBuilder, linkBuilder, permissionEnforcer, licenseRepository, representationFactory, hostApplicationDescriptor);
            this.requests = Collections.unmodifiableList(new ArrayList<PluginRequestRepresentation>(requests));
        }

        RequestedPluginEntry(RequestedPluginEntry oldEntry, Collection<PluginRequestRepresentation> requests) {
            super(oldEntry.getLinks(), oldEntry.getName(), oldEntry.getSummary(), oldEntry.getKey(), oldEntry.getLogo(), oldEntry.getVendor(), oldEntry.getCategories(), oldEntry.getRating(), oldEntry.getRatingCount(), oldEntry.getReviewCount(), oldEntry.getDownloadCount(), oldEntry.getInstallationCount(), oldEntry.installed(), oldEntry.installable(), oldEntry.preinstalled(), oldEntry.stable(), oldEntry.dataCenterCompatible(), oldEntry.isStatusDataCenterCompatible(), oldEntry.getMarketplaceType(), oldEntry.isUsesLicensing(), null, oldEntry.getSupportType(), oldEntry.getHamsProductKey(), oldEntry.getCloudFreeUsers());
            this.requests = Collections.unmodifiableList(new ArrayList<PluginRequestRepresentation>(requests));
        }

        public Collection<PluginRequestRepresentation> getRequests() {
            return this.requests;
        }

        @Override
        public int compareTo(@Nonnull AvailablePluginEntry entry) {
            if (entry instanceof RequestedPluginEntry) {
                RequestedPluginEntry requestedEntry = (RequestedPluginEntry)entry;
                if (this.getRequests().size() != requestedEntry.getRequests().size()) {
                    return Integer.compare(requestedEntry.getRequests().size(), this.getRequests().size());
                }
                return this.getName().toLowerCase().compareTo(entry.getName().toLowerCase());
            }
            return super.compareTo(entry);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class AvailablePluginEntry
    implements Comparable<AvailablePluginEntry> {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final String key;
        @JsonProperty
        private final String summary;
        @JsonProperty
        private final IconRepresentation logo;
        @JsonProperty
        private final VendorRepresentation vendor;
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
        private final String marketplaceType;
        @JsonProperty
        private final boolean usesLicensing;
        @JsonProperty
        private final PluginLicenseRepresentation licenseDetails;
        @JsonProperty
        private final String supportType;
        @JsonProperty
        private final String hamsProductKey;
        @JsonProperty
        private final Integer cloudFreeUsers;

        @JsonCreator
        public AvailablePluginEntry(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="name") String name, @JsonProperty(value="summary") String summary, @JsonProperty(value="key") String key, @JsonProperty(value="logo") IconRepresentation logo, @JsonProperty(value="vendor") VendorRepresentation vendor, @JsonProperty(value="categories") Collection<CategoryRepresentation> categories, @JsonProperty(value="rating") Float rating, @JsonProperty(value="ratingCount") Integer ratingCount, @JsonProperty(value="reviewCount") Integer reviewCount, @JsonProperty(value="downloadCount") Integer downloadCount, @JsonProperty(value="installationCount") Integer installationCount, @JsonProperty(value="installed") Boolean installed, @JsonProperty(value="installable") Boolean installable, @JsonProperty(value="preinstalled") Boolean preinstalled, @JsonProperty(value="stable") Boolean stable, @JsonProperty(value="dataCenterCompatible") Boolean dataCenterCompatible, @JsonProperty(value="statusDataCenterCompatible") Boolean statusDataCenterCompatible, @JsonProperty(value="marketplaceType") String marketplaceType, @JsonProperty(value="usesLicensing") boolean usesLicensing, @JsonProperty(value="licenseDetails") PluginLicenseRepresentation licenseDetails, @JsonProperty(value="supportType") String supportType, @JsonProperty(value="hamsProductKey") String hamsProductKey, @JsonProperty(value="cloudFreeUsers") Integer cloudFreeUsers) {
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
            this.name = Objects.requireNonNull(name, "name");
            this.key = Objects.requireNonNull(key, "key");
            this.summary = Objects.requireNonNull(summary, "summary");
            this.logo = logo;
            this.vendor = vendor;
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
            this.marketplaceType = marketplaceType;
            this.usesLicensing = usesLicensing;
            this.licenseDetails = licenseDetails;
            this.supportType = supportType;
            this.hamsProductKey = hamsProductKey;
            this.cloudFreeUsers = cloudFreeUsers;
        }

        AvailablePluginEntry(Option<Plugin> installedPlugin, ApplicationProperties applicationProperties, AddonBase plugin, AddonVersionBase versionSummary, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PermissionEnforcer permissionEnforcer, PluginLicenseRepository licenseRepository, UpmRepresentationFactory representationFactory, HostApplicationDescriptor hostApplicationDescriptor) {
            Option<PluginLicense> pluginLicense = licenseRepository.getPluginLicense(plugin.getKey());
            this.links = this.buildLinks(plugin, versionSummary, uriBuilder, linkBuilder, installedPlugin, pluginLicense, permissionEnforcer);
            this.name = plugin.getName();
            this.key = plugin.getKey();
            this.summary = (String)plugin.getSummary().getOrElse((Object)"");
            this.logo = IconRepresentation.newIcon(UpmFugueConverters.toUpmOption(plugin.getLogo()));
            this.vendor = (VendorRepresentation)plugin.getVendor().map(representationFactory::createVendorRepresentation).getOrElse((Object)null);
            List<AddonCategorySummary> categories = Stream.concat(StreamSupport.stream(plugin.getCategories().spliterator(), false), StreamSupport.stream(versionSummary.getFunctionalCategories().spliterator(), false)).collect(Collectors.toList());
            this.categories = CategoryRepresentation.representUniqueCategories(categories, linkBuilder, uriBuilder);
            this.rating = Float.valueOf(plugin.getReviews().getAverageStars());
            this.ratingCount = plugin.getReviews().getCount();
            this.reviewCount = plugin.getReviews().getCount();
            this.downloadCount = plugin.getDistribution().getDownloads();
            this.installationCount = (Integer)plugin.getDistribution().getTotalInstalls().getOrElse((Object)null);
            this.installed = installedPlugin.isDefined();
            this.preinstalled = installedPlugin.isDefined() && plugin.getDistribution().isBundled();
            this.licenseDetails = (PluginLicenseRepresentation)pluginLicense.filter((Predicate<PluginLicense>)((Predicate)l -> permissionEnforcer.hasPermission(Permission.GET_PLUGIN_LICENSE))).map(l -> representationFactory.createPluginLicenseRepresentation(plugin.getKey(), installedPlugin, Option.some(l))).getOrElse((PluginLicenseRepresentation)null);
            this.hamsProductKey = plugin.getKey();
            this.cloudFreeUsers = (Integer)plugin.getCloudFreeUsers().filter(Plugins.hasCloudFreeUsers(hostApplicationDescriptor)).getOrElse((Object)null);
            this.installable = MarketplacePlugins.isInstallable(versionSummary, applicationProperties);
            this.stable = !versionSummary.isBeta();
            this.dataCenterCompatible = versionSummary.isDataCenterCompatible();
            this.statusDataCenterCompatible = versionSummary.isDataCenterStatusCompatible();
            this.marketplaceType = MarketplacePlugins.getMarketplaceTypeFromPaymentModel(versionSummary.getPaymentModel());
            this.supportType = MarketplacePlugins.getSupportTypeName(plugin, versionSummary);
            this.usesLicensing = versionSummary.getPaymentModel() == PaymentModel.PAID_VIA_ATLASSIAN;
        }

        private Map<String, URI> buildLinks(AddonBase plugin, AddonVersionBase pluginVersion, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, Option<Plugin> installedPlugin, Option<PluginLicense> pluginLicense, PermissionEnforcer permissionEnforcer) {
            String pluginKey = plugin.getKey();
            LinksMapBuilder links = linkBuilder.buildLinksForAvailablePlugin(uriBuilder.buildAvailablePluginUri(pluginKey), installedPlugin, pluginKey, pluginLicense, plugin, pluginVersion);
            Option<URI> iconUri = UpmFugueConverters.toUpmOption(plugin.getLogo()).map(ImageInfo::getImageUri);
            Option<URI> logoUri = UpmFugueConverters.toUpmOption(plugin.getLogo()).flatMap(image -> UpmFugueConverters.toUpmOption(image.getImageUri(ImageInfo.Size.SMALL_SIZE, ImageInfo.Resolution.DEFAULT_RESOLUTION)));
            links.put("plugin-icon", (URI)iconUri.getOrElse(uriBuilder.buildPluginIconLocationUri(pluginKey))).put("plugin-logo", (URI)logoUri.getOrElse(uriBuilder.buildPluginLogoLocationUri(pluginKey)));
            if (installedPlugin.isDefined()) {
                links.putIfPermitted(Permission.GET_INSTALLED_PLUGINS, installedPlugin, "manage", uriBuilder.buildUpmTabPluginUri("manage", pluginKey));
            } else {
                links.putIfPermitted(Permission.CREATE_PLUGIN_REQUEST, "request", uriBuilder.buildPluginRequestCollectionResourceUri()).putIfPermitted(Permission.MANAGE_PLUGIN_REQUESTS, "dismiss-request", uriBuilder.buildPluginRequestDismissCollectionResourceUri(pluginKey));
            }
            return links.build();
        }

        public Map<String, URI> getLinks() {
            return this.links;
        }

        public URI getSelf() {
            return this.links.get("self");
        }

        public String getName() {
            return this.name;
        }

        public String getKey() {
            return this.key;
        }

        public String getSummary() {
            return this.summary;
        }

        public URI getPluginIconLink() {
            return this.links.get("plugin-icon");
        }

        public URI getPluginLogoLink() {
            return this.links.get("plugin-logo");
        }

        public IconRepresentation getLogo() {
            return this.logo;
        }

        public VendorRepresentation getVendor() {
            return this.vendor;
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

        public Boolean preinstalled() {
            return this.preinstalled;
        }

        public Boolean stable() {
            return this.stable;
        }

        @Deprecated
        public boolean dataCenterCompatible() {
            return this.dataCenterCompatible;
        }

        public boolean isStatusDataCenterCompatible() {
            return this.statusDataCenterCompatible;
        }

        public String getMarketplaceType() {
            return this.marketplaceType;
        }

        public boolean isUsesLicensing() {
            return this.usesLicensing;
        }

        public PluginLicenseRepresentation getLicenseDetails() {
            return this.licenseDetails;
        }

        public String getSupportType() {
            return this.supportType;
        }

        public String getHamsProductKey() {
            return this.hamsProductKey;
        }

        public Integer getCloudFreeUsers() {
            return this.cloudFreeUsers;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public int compareTo(@Nonnull AvailablePluginEntry entry) {
            return this.getName().compareTo(entry.getName());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof AvailablePluginEntry) {
                return this.getKey().equals(((AvailablePluginEntry)obj).getKey());
            }
            return false;
        }
    }

    private static final class SummaryToPluginEntry
    implements Function<AvailableAddonWithVersionBase, AvailablePluginEntry> {
        private final UpmRepresentationFactory representationFactory;
        private final Map<String, PluginRequest> pluginRequests;

        public SummaryToPluginEntry(UpmRepresentationFactory representationFactory, Map<String, PluginRequest> pluginRequests) {
            this.representationFactory = representationFactory;
            this.pluginRequests = pluginRequests;
        }

        @Override
        public AvailablePluginEntry apply(AvailableAddonWithVersionBase a) {
            String pluginKey = a.getAddonBase().getKey();
            if (this.pluginRequests.containsKey(pluginKey)) {
                return this.representationFactory.createRequestedPluginEntry(a.getAddonBase(), a.getVersionBase(), Collections.singletonList(this.pluginRequests.get(pluginKey)));
            }
            return this.representationFactory.createAvailablePluginEntry(a.getAddonBase(), a.getVersionBase());
        }
    }
}

