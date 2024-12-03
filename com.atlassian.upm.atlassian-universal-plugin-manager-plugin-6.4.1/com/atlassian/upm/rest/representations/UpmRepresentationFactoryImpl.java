/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.ConnectScope;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.PluginPrimaryAction;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.UserAttributes;
import com.atlassian.upm.core.rest.representations.DefaultRepresentationFactory;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.jwt.UpmJwtToken;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.BundleAccessor;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.ServiceAccessor;
import com.atlassian.upm.osgi.rest.representations.BundleRepresentation;
import com.atlassian.upm.osgi.rest.representations.BundleSummaryRepresentation;
import com.atlassian.upm.osgi.rest.representations.CollectionRepresentation;
import com.atlassian.upm.osgi.rest.representations.PackageRepresentation;
import com.atlassian.upm.osgi.rest.representations.PackageSummaryRepresentation;
import com.atlassian.upm.osgi.rest.representations.ServiceRepresentation;
import com.atlassian.upm.osgi.rest.representations.ServiceSummaryRepresentation;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.pac.PluginVersionPair;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.request.rest.representations.PluginRequestCollectionRepresentation;
import com.atlassian.upm.request.rest.representations.PluginRequestRepresentation;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.AvailablePluginCollectionRepresentation;
import com.atlassian.upm.rest.representations.AvailablePluginRepresentation;
import com.atlassian.upm.rest.representations.BannerCollectionRepresentation;
import com.atlassian.upm.rest.representations.HostLicenseDetailsRepresentation;
import com.atlassian.upm.rest.representations.HostStatusRepresentation;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginCollectionRepresentation;
import com.atlassian.upm.rest.representations.InstalledMarketplacePluginRepresentation;
import com.atlassian.upm.rest.representations.JwtTokenRepresentation;
import com.atlassian.upm.rest.representations.PacDetailsRepresentation;
import com.atlassian.upm.rest.representations.PluginLicenseRepresentation;
import com.atlassian.upm.rest.representations.PluginPermissionRepresentation;
import com.atlassian.upm.rest.representations.ProductUpdatePluginCompatibilityRepresentation;
import com.atlassian.upm.rest.representations.ProductUpdatesRepresentation;
import com.atlassian.upm.rest.representations.ProductVersionRepresentation;
import com.atlassian.upm.rest.representations.PurchasedPluginCollectionRepresentation;
import com.atlassian.upm.rest.representations.RecommendedPluginCollectionRepresentation;
import com.atlassian.upm.rest.representations.SafeModeErrorReenablingPluginModuleRepresentation;
import com.atlassian.upm.rest.representations.SafeModeErrorReenablingPluginRepresentation;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.UpmSettingsCollectionRepresentation;
import com.atlassian.upm.rest.representations.UserSettingsRepresentation;
import com.atlassian.upm.rest.resources.PacStatusResource;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class UpmRepresentationFactoryImpl
extends DefaultRepresentationFactory
implements UpmRepresentationFactory {
    private final PluginRetriever pluginRetriever;
    private final UpmUriBuilder uriBuilder;
    private final UpmLinkBuilder linkBuilder;
    private final AsynchronousTaskManager taskManager;
    private final BundleAccessor bundleAccessor;
    private final ServiceAccessor serviceAccessor;
    private final PackageAccessor packageAccessor;
    private final PermissionEnforcer permissionEnforcer;
    private final ApplicationProperties applicationProperties;
    private final LicenseDateFormatter licenseDateFormatter;
    private final HostLicenseProvider hostLicenseProvider;
    private final PacClient pacClient;
    private final PluginLicenseRepository licenseRepository;
    private final SysPersisted sysPersisted;
    private final UserManager userManager;
    private final PluginRequestStore pluginRequestStore;
    private final HostLicenseInformation hostLicenseInformation;
    private final UpmInformation upm;
    private final PluginUpdateRequestStore pluginUpdateRequestStore;
    private final SafeModeAccessor safeMode;
    private final I18nResolver i18nResolver;
    private final RoleBasedLicensingPluginService roleBasedLicensingPluginService;
    private final UpmHostApplicationInformation appInfo;
    private final HostApplicationDescriptor hostApplicationDescriptor;
    private final LicensingUsageVerifier licensingUsageVerifier;
    private final ApplicationPluginsManager applicationPluginsManager;
    private Function<Pair<AvailableAddonWithVersion, Collection<PluginRequest>>, AvailablePluginCollectionRepresentation.RequestedPluginEntry> toRequestedPluginEntry = input -> this.createRequestedPluginEntry(((AvailableAddonWithVersion)input.first()).getAddon(), ((AvailableAddonWithVersion)input.first()).getVersion(), (Collection)input.second());

    public UpmRepresentationFactoryImpl(PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder, PacClient pacClient, AsynchronousTaskManager taskManager, BundleAccessor bundleAccessor, ServiceAccessor serviceAccessor, PackageAccessor packageAccessor, PermissionEnforcer permissionEnforcer, ApplicationProperties applicationProperties, LicenseDateFormatter licenseDateFormatter, HostLicenseProvider hostLicenseProvider, PluginLicenseRepository licenseRepository, SysPersisted sysPersisted, UserManager userManager, PluginRequestStore pluginRequestStore, HostLicenseInformation hostLicenseInformation, UpmInformation upm, PluginUpdateRequestStore pluginUpdateRequestStore, SafeModeAccessor safeMode, PluginRestartRequiredService restartRequiredService, I18nResolver i18nResolver, RoleBasedLicensingPluginService roleBasedLicensingPluginService, UpmHostApplicationInformation appInfo, HostApplicationDescriptor hostApplicationDescriptor, UpmAppManager appManager, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        super(pluginRetriever, metadata, uriBuilder, linkBuilder, permissionEnforcer, restartRequiredService, appManager, licensingUsageVerifier, applicationPluginsManager);
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.linkBuilder = Objects.requireNonNull(linkBuilder, "linkBuilder");
        this.taskManager = Objects.requireNonNull(taskManager, "taskManager");
        this.bundleAccessor = Objects.requireNonNull(bundleAccessor, "bundleAccessor");
        this.serviceAccessor = Objects.requireNonNull(serviceAccessor, "serviceAccessor");
        this.packageAccessor = Objects.requireNonNull(packageAccessor, "packageAccessor");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.licenseDateFormatter = Objects.requireNonNull(licenseDateFormatter, "licenseDateFormatter");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.pluginRequestStore = Objects.requireNonNull(pluginRequestStore, "pluginRequestStore");
        this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.pluginUpdateRequestStore = Objects.requireNonNull(pluginUpdateRequestStore, "pluginUpdateRequestStore");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.roleBasedLicensingPluginService = Objects.requireNonNull(roleBasedLicensingPluginService, "roleBasedLicensingPluginService");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
        this.hostApplicationDescriptor = Objects.requireNonNull(hostApplicationDescriptor, "hostApplicationDescriptor");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    @Override
    public InstalledMarketplacePluginCollectionRepresentation createInstalledMarketplacePluginCollectionRepresentation(Locale locale, List<Plugin> plugins, Iterable<AvailableAddonWithVersion> updates, Iterable<IncompatiblePluginData> incompatibles, RequestContext context, String upmUpdateVersion) {
        return new InstalledMarketplacePluginCollectionRepresentation(this, this.uriBuilder, this.linkBuilder, this.appInfo, locale, plugins, updates, incompatibles, this.createHostStatusRepresentation(context), upmUpdateVersion);
    }

    @Override
    public InstalledMarketplacePluginRepresentation createInstalledMarketplacePluginRepresentation(Plugin plugin, Option<AvailableAddonWithVersion> availableUpdate, Option<IncompatiblePluginData> incompatible) {
        Objects.requireNonNull(plugin, "plugin");
        boolean usesLicensing = this.isInstalledPluginUsingLicensing(plugin);
        Option<PluginLicense> license = this.getPluginLicense(plugin, usesLicensing);
        PluginLicenseRepresentation licenseDetails = this.getLicenseDetailsRepresentation(plugin, license, usesLicensing);
        Option<AddonVersion> availableUpdateVersion = availableUpdate.map(AvailableAddonWithVersion::getVersion);
        boolean updatableToPaid = this.permissionEnforcer.hasPermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI) && com.atlassian.upm.license.impl.LicensedPlugins.isFreeUpdatableToPaid(plugin, availableUpdateVersion, this.licensingUsageVerifier);
        boolean carebearServerSpecific = this.permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE) && com.atlassian.upm.license.impl.LicensedPlugins.isServerWithCloudAlternative(this.appInfo.getHostingType(), this.applicationProperties.getPlatformId());
        boolean carebearServerTryOrBuy = carebearServerSpecific && usesLicensing && (PluginLicenses.isPluginBuyable(license, false) || PluginLicenses.isPluginTryable(license, false));
        Optional<PluginPrimaryAction> actionRequired = com.atlassian.upm.license.impl.LicensedPlugins.getPrimaryPluginActionRequired(this.permissionEnforcer, this.pluginUpdateRequestStore, this.roleBasedLicensingPluginService, plugin, availableUpdateVersion, incompatible, license, this.appInfo, this.hostLicenseInformation, this.licensingUsageVerifier, this.applicationPluginsManager);
        InstalledMarketplacePluginRepresentation.PluginPrimaryActionRepresentation primaryAction = actionRequired.map(pluginPrimaryAction -> new InstalledMarketplacePluginRepresentation.PluginPrimaryActionRepresentation((PluginPrimaryAction)((Object)pluginPrimaryAction), plugin, this.permissionEnforcer, this.licenseRepository, this.pluginUpdateRequestStore)).orElse(null);
        boolean licenseReadOnly = usesLicensing && !this.permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE);
        LinksMapBuilder linksBuilder = this.linkBuilder.buildLinksForInstalledMarketplacePlugin(plugin, license, usesLicensing);
        PacDetailsRepresentation.AvailablePluginUpdateRepresentation updateRep = null;
        for (AvailableAddonWithVersion update : availableUpdate) {
            linksBuilder.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, Option.some(plugin), "update-details", this.uriBuilder.buildAvailablePluginUri(plugin.getKey()));
            for (URI binary : update.getVersion().getArtifactUri()) {
                linksBuilder.putIfPermitted(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI, Option.some(plugin), "binary", binary);
            }
            updateRep = new PacDetailsRepresentation.AvailablePluginUpdateRepresentation(update, plugin, this.uriBuilder, this.linkBuilder, this.upm, this.licenseRepository, this.applicationProperties, this.permissionEnforcer, this.appManager, this.hostLicenseInformation, this.licenseDateFormatter);
        }
        actionRequired.ifPresent(pluginPrimaryAction -> {
            if (pluginPrimaryAction.canRequestUpdateFromVendor() && primaryAction.isActionRequired()) {
                linksBuilder.putIfPermitted(Permission.REQUEST_PLUGIN_UPDATE, Option.some(plugin), "request-update", this.uriBuilder.buildRequestUpdateUri(plugin.getKey()));
            }
        });
        return new InstalledMarketplacePluginRepresentation(linksBuilder.build(), plugin.getKey(), plugin.getName(), licenseDetails, licenseReadOnly, primaryAction, plugin.getKey(), updatableToPaid, incompatible.isDefined(), !MarketplacePlugins.isLegacyDataCenterIncompatible(plugin, this.appInfo), !MarketplacePlugins.isDataCenterIncompatible(plugin, this.appInfo), updateRep, PluginInfoUtils.isDataCenterApp(plugin.getPluginInformation()), carebearServerTryOrBuy, carebearServerSpecific);
    }

    private boolean isInstalledPluginUsingLicensing(Plugin plugin) {
        return LicensedPlugins.usesLicensing(plugin.getPlugin(), this.licensingUsageVerifier) && this.permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE, plugin);
    }

    private Option<PluginLicense> getPluginLicense(Plugin plugin, boolean usesLicensing) {
        return usesLicensing ? this.licenseRepository.getPluginLicense(plugin.getKey()) : Option.none(PluginLicense.class);
    }

    private PluginLicenseRepresentation getLicenseDetailsRepresentation(Plugin plugin, Option<PluginLicense> license, boolean usesLicensing) {
        if (usesLicensing && license.isDefined()) {
            return this.createPluginLicenseRepresentation(plugin.getKey(), Option.some(plugin), license);
        }
        return null;
    }

    @Override
    public PacDetailsRepresentation createPacDetailsRepresentation(Plugin plugin, Option<PluginVersionPair> pluginVersionPair) {
        return new PacDetailsRepresentation(pluginVersionPair, plugin, this.uriBuilder, this.linkBuilder, this.permissionEnforcer, this.licenseRepository, this.applicationProperties, this.upm, this.appManager, this.hostLicenseInformation, this.licenseDateFormatter);
    }

    @Override
    public AvailablePluginCollectionRepresentation.AvailablePluginEntry createAvailablePluginEntry(AddonBase plugin, AddonVersionBase version) {
        Option<Plugin> installedPlugin = this.pluginRetriever.getPlugin(plugin.getKey());
        return new AvailablePluginCollectionRepresentation.AvailablePluginEntry(installedPlugin, this.applicationProperties, plugin, version, this.uriBuilder, this.linkBuilder, this.permissionEnforcer, this.licenseRepository, this, this.hostApplicationDescriptor);
    }

    @Override
    public AvailablePluginCollectionRepresentation.RequestedPluginEntry createRequestedPluginEntry(AddonBase plugin, AddonVersionBase version, Collection<PluginRequest> requests) {
        Option<Plugin> installedPlugin = this.pluginRetriever.getPlugin(plugin.getKey());
        List<PluginRequestRepresentation> requestReps = requests.stream().map(this::createPluginRequestRepresentation).collect(Collectors.toList());
        return new AvailablePluginCollectionRepresentation.RequestedPluginEntry(installedPlugin, this.applicationProperties, plugin, version, this.uriBuilder, this.linkBuilder, this.permissionEnforcer, this.licenseRepository, this, requestReps, this.hostApplicationDescriptor);
    }

    @Override
    public PurchasedPluginCollectionRepresentation.UnknownPluginEntry createUnknownPluginEntry(String pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            LinksMapBuilder builder = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "plugin-details", this.uriBuilder.buildUpmTabPluginUri("manage", pluginKey));
            return new PurchasedPluginCollectionRepresentation.UnknownPluginEntry(pluginKey, plugin.getName(), builder.build());
        }
        return new PurchasedPluginCollectionRepresentation.UnknownPluginEntry(pluginKey, null, Collections.emptyMap());
    }

    @Override
    public AvailablePluginCollectionRepresentation createAvailablePluginCollectionRepresentation(Iterable<AvailableAddonWithVersionBase> addons, Page<AddonSummary> sourceAddons, Map<String, PluginRequest> pluginRequests, RequestContext context, final UpmMarketplaceFilter filter, final Option<String> searchText) {
        return new AvailablePluginCollectionRepresentation(this.linkBuilder, addons, sourceAddons, pluginRequests, this.createHostStatusRepresentation(context), this, (Function<? super Integer, URI>)new Function<Integer, URI>(){

            @Override
            public URI apply(Integer offset) {
                return UpmRepresentationFactoryImpl.this.uriBuilder.buildAvailablePluginCollectionUri(filter, searchText, offset);
            }
        });
    }

    @Override
    public AvailablePluginRepresentation createAvailablePluginRepresentation(Addon addon, AddonVersion version) {
        return new AvailablePluginRepresentation(Objects.requireNonNull(addon, "addon"), Objects.requireNonNull(version, "addonVersion"), this.uriBuilder, this.linkBuilder, this.pluginRetriever, this.applicationProperties, this.permissionEnforcer, this, this.pluginRequestStore, this.licenseRepository, this.upm, this.hostApplicationDescriptor, this.appManager);
    }

    @Override
    public BannerCollectionRepresentation createBannerCollectionRepresentation(Page<AddonReference> banners) {
        return new BannerCollectionRepresentation(banners, this.uriBuilder, this.linkBuilder);
    }

    @Override
    public RecommendedPluginCollectionRepresentation createRecommendedPluginCollectionRepresentation(Iterable<AddonReference> recommendations, String pluginKey) {
        return new RecommendedPluginCollectionRepresentation(recommendations, this.uriBuilder, this.linkBuilder, pluginKey);
    }

    @Override
    public ProductUpdatesRepresentation createProductUpdatesRepresentation(Collection<ApplicationVersion> productVersions, RequestContext context) {
        return new ProductUpdatesRepresentation(this.uriBuilder, productVersions, this.linkBuilder, this.createHostStatusRepresentation(context));
    }

    @Override
    public ProductVersionRepresentation createProductVersionRepresentation(boolean development, boolean unknown) {
        return new ProductVersionRepresentation(development, unknown);
    }

    @Override
    public ProductUpdatePluginCompatibilityRepresentation createProductUpdatePluginCompatibilityRepresentation(ProductUpdatePluginCompatibility pluginCompatibility, int productUpdateBuildNumber, Locale locale) {
        return new ProductUpdatePluginCompatibilityRepresentation(this.uriBuilder, this.linkBuilder, this.pluginRetriever, pluginCompatibility, productUpdateBuildNumber, locale);
    }

    @Override
    public CollectionRepresentation<BundleSummaryRepresentation> createOsgiBundleCollectionRepresentation() {
        return this.createOsgiBundleCollectionRepresentation(null);
    }

    @Override
    public CollectionRepresentation<BundleSummaryRepresentation> createOsgiBundleCollectionRepresentation(@Nullable String term) {
        return new CollectionRepresentation<BundleSummaryRepresentation>(BundleSummaryRepresentation.wrapSummary(this.uriBuilder).fromIterable(this.bundleAccessor.getBundles(term)), this.safeMode.isSafeMode(), this.linkBuilder.buildLinksFor(this.uriBuilder.buildOsgiBundleCollectionUri(term)).build());
    }

    @Override
    public BundleRepresentation createOsgiBundleRepresentation(Bundle bundle) {
        return new BundleRepresentation(Objects.requireNonNull(bundle, "bundle"), this.uriBuilder);
    }

    @Override
    public CollectionRepresentation<ServiceSummaryRepresentation> createOsgiServiceCollectionRepresentation() {
        return new CollectionRepresentation<ServiceSummaryRepresentation>(ServiceSummaryRepresentation.wrapSummary(this.uriBuilder).fromIterable(this.serviceAccessor.getServices()), this.safeMode.isSafeMode(), this.linkBuilder.buildLinksFor(this.uriBuilder.buildOsgiServiceCollectionUri()).build());
    }

    @Override
    public ServiceRepresentation createOsgiServiceRepresentation(Service service) {
        return new ServiceRepresentation(Objects.requireNonNull(service, "service"), this.uriBuilder);
    }

    @Override
    public CollectionRepresentation<PackageSummaryRepresentation> createOsgiPackageCollectionRepresentation() {
        return new CollectionRepresentation<PackageSummaryRepresentation>(PackageSummaryRepresentation.wrapSummary(this.uriBuilder).fromIterable(this.packageAccessor.getPackages()), this.safeMode.isSafeMode(), this.linkBuilder.buildLinksFor(this.uriBuilder.buildOsgiPackageCollectionUri()).build());
    }

    @Override
    public PackageRepresentation createOsgiPackageRepresentation(Package pkg) {
        return new PackageRepresentation(Objects.requireNonNull(pkg, "pkg"), this.uriBuilder);
    }

    @Override
    public PacStatusResource.PacStatusRepresentation createPacStatusRepresentation(boolean disabled, boolean reached) {
        return new PacStatusResource.PacStatusRepresentation(disabled, reached, this.uriBuilder, this.linkBuilder);
    }

    @Override
    public PluginLicenseRepresentation createPluginLicenseRepresentation(String pluginKey, Option<Plugin> plugin, Option<PluginLicense> pluginLicense) {
        Iterator<PluginLicense> iterator = pluginLicense.iterator();
        if (iterator.hasNext()) {
            PluginLicense license = iterator.next();
            return new PluginLicenseRepresentation(pluginKey, plugin, license, this.licenseDateFormatter, this.linkBuilder, this.roleBasedLicensingPluginService, this.i18nResolver, this.hostLicenseInformation);
        }
        return new PluginLicenseRepresentation(pluginKey, plugin, this.linkBuilder);
    }

    @Override
    public HostStatusRepresentation createHostStatusRepresentation(RequestContext context) {
        UserAttributes userAttributes = UserAttributes.fromCurrentUser(this.userManager);
        if (userAttributes.isAdmin() || userAttributes.isSystemAdmin()) {
            return new HostStatusRepresentation(!this.taskManager.isBaseUrlValid(context.getRequest()), this.safeMode.isSafeMode(), this.sysPersisted.is(UpmSettings.PAC_DISABLED), context.isPacUnreachable(), this.createHostLicenseRepresentation());
        }
        return new HostStatusRepresentation(!this.taskManager.isBaseUrlValid(context.getRequest()), this.safeMode.isSafeMode(), this.sysPersisted.is(UpmSettings.PAC_DISABLED), context.isPacUnreachable(), null);
    }

    @Override
    public HostLicenseDetailsRepresentation createHostLicenseRepresentation() {
        return new HostLicenseDetailsRepresentation(this.hostLicenseProvider.getHostApplicationLicenseAttributes(), this.appInfo, this.licenseDateFormatter);
    }

    @Override
    public PluginRequestRepresentation createPluginRequestRepresentation(PluginRequest request) {
        return new PluginRequestRepresentation(request.getPluginKey(), request, this.linkBuilder, this.uriBuilder);
    }

    @Override
    public PluginRequestCollectionRepresentation createPluginRequestCollectionRepresentation(Map<String, Collection<PluginRequest>> requests) {
        return this.createPluginRequestCollectionRepresentation(requests, false);
    }

    @Override
    public PluginRequestCollectionRepresentation createAnonymousPluginRequestCollectionRepresentation(Map<String, Collection<PluginRequest>> requests) {
        return this.createPluginRequestCollectionRepresentation(requests, true);
    }

    @Override
    public PurchasedPluginCollectionRepresentation createPurchasedPluginCollectionRepresentation(Locale locale, Collection<AvailableAddonWithVersionBase> plugins, Collection<AvailableAddonWithVersionBase> incompatiblePlugins, Collection<String> unknownPluginKeys, RequestContext context) {
        return new PurchasedPluginCollectionRepresentation(plugins, incompatiblePlugins, unknownPluginKeys, this.uriBuilder, this.linkBuilder, this.createHostStatusRepresentation(context), this);
    }

    @Override
    public UpmSettingsCollectionRepresentation createUpmSettingsCollectionRepresentation() {
        ArrayList<UpmSettingsCollectionRepresentation.UpmSettingRepresentation> builder = new ArrayList<UpmSettingsCollectionRepresentation.UpmSettingRepresentation>();
        for (UpmSettings s : UpmSettings.values()) {
            if (!this.permissionEnforcer.hasPermission(s.getPermission())) continue;
            builder.add(new UpmSettingsCollectionRepresentation.UpmSettingRepresentation(s.getKey(), this.sysPersisted.is(s), s.isRequiresRefresh(), s.getDefaultCheckedValue(), false));
        }
        return new UpmSettingsCollectionRepresentation(Collections.unmodifiableList(builder));
    }

    @Override
    public UserSettingsRepresentation createUserSettingsRepresentation(boolean emailDisabled) {
        return new UserSettingsRepresentation(emailDisabled);
    }

    @Override
    public VendorRepresentation createVendorRepresentation(VendorSummary vendor) {
        if (vendor == null || StringUtils.isBlank((CharSequence)vendor.getName())) {
            return null;
        }
        URI marketplaceLink = URI.create(Sys.getMpacBaseUrl()).resolve(vendor.getAlternateUri());
        return new VendorRepresentation(vendor.getName(), marketplaceLink, vendor.isTopVendor());
    }

    @Override
    public SafeModeErrorReenablingPluginRepresentation createSafeModeErrorReenablingPluginRepresentation(Plugin plugin, boolean enabling) {
        return new SafeModeErrorReenablingPluginRepresentation(plugin.getKey(), plugin.getName(), enabling);
    }

    @Override
    public SafeModeErrorReenablingPluginModuleRepresentation createSafeModeErrorReenablingPluginModuleRepresentation(Plugin.Module module, boolean enabling) {
        return new SafeModeErrorReenablingPluginModuleRepresentation(module.getPlugin().getKey(), module.getPlugin().getName(), module.getCompleteKey(), module.getName(), enabling);
    }

    @Override
    public PluginPermissionRepresentation createPluginPermissionRepresentation(ConnectScope scope) {
        String nameKey = "connect.scope." + scope.getKey().toLowerCase() + ".name";
        String descriptionKey = "connect.scope." + scope.getKey().toLowerCase() + ".description";
        return new PluginPermissionRepresentation(scope.getKey(), this.getI18nOrDefaultValue(nameKey, scope.getName()), this.getI18nOrDefaultValue(descriptionKey, scope.getDescription()));
    }

    private String getI18nOrDefaultValue(String i18nKey, String defaultValue) {
        String val = this.i18nResolver.getText(i18nKey, new Serializable[]{this.applicationProperties.getDisplayName()});
        return i18nKey.equals(val) ? defaultValue : val;
    }

    private PluginRequestCollectionRepresentation createPluginRequestCollectionRepresentation(Map<String, Collection<PluginRequest>> requests, boolean anonymize) {
        Collection<AvailableAddonWithVersion> plugins = this.pacClient.getPlugins(requests.keySet());
        ArrayList<Pair<AvailableAddonWithVersion, Collection<PluginRequest>>> pluginsWithRequests = new ArrayList<Pair<AvailableAddonWithVersion, Collection<PluginRequest>>>();
        for (AvailableAddonWithVersion a : plugins) {
            String pluginKey = a.getAddon().getKey();
            if (!requests.containsKey(pluginKey)) continue;
            pluginsWithRequests.add(Pair.pair(a, requests.get(pluginKey)));
        }
        List<AvailablePluginCollectionRepresentation.RequestedPluginEntry> requestedPlugins = pluginsWithRequests.stream().map(this.toRequestedPluginEntry).collect(Collectors.toList());
        if (anonymize) {
            requestedPlugins = this.anonymize(requestedPlugins);
        }
        return new PluginRequestCollectionRepresentation(this.linkBuilder.buildLinksFor(this.uriBuilder.buildPluginRequestCollectionResourceUri()).build(), requestedPlugins.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
    }

    private List<AvailablePluginCollectionRepresentation.RequestedPluginEntry> anonymize(List<AvailablePluginCollectionRepresentation.RequestedPluginEntry> nonAnonymous) {
        return nonAnonymous.stream().map(entry -> new AvailablePluginCollectionRepresentation.RequestedPluginEntry((AvailablePluginCollectionRepresentation.RequestedPluginEntry)entry, entry.getRequests().stream().map(this::stripOtherUserInfo).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    private PluginRequestRepresentation stripOtherUserInfo(PluginRequestRepresentation rep) {
        if (rep.getUser().getUserKey().equals(this.userManager.getRemoteUserKey().getStringValue())) {
            return rep;
        }
        return PluginRequestRepresentation.anonymize(rep);
    }

    @Override
    public JwtTokenRepresentation createJwtTokenRepresentation(UpmJwtToken token) {
        return new JwtTokenRepresentation(token.getToken());
    }
}

