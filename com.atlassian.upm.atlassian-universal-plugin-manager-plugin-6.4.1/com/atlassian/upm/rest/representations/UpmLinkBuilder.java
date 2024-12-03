/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Predicate
 *  io.atlassian.fugue.Option
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.AddonExternalLinkType;
import com.atlassian.marketplace.client.api.AddonVersionExternalLinkType;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.DefaultLinkBuilder;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.impl.LicensedPlugins;
import com.atlassian.upm.license.internal.HostApplicationLicenseAttributes;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginServiceUtil;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.servlet.PurchasedAddonsServlet;
import com.atlassian.upm.test.rest.resources.UpmSysResource;
import com.google.common.base.Predicate;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpmLinkBuilder
extends DefaultLinkBuilder {
    private final UpmUriBuilder uriBuilder;
    private final PluginMetadataAccessor metadata;
    private final PermissionEnforcer permissionEnforcer;
    private final SysPersisted sysPersisted;
    private final ProductMailService mailService;
    private final SafeModeAccessor safeMode;
    private final RoleBasedLicensingPluginService roleBasedLicensingService;
    private final UpmHostApplicationInformation appInfo;
    private final HostLicenseProvider hostLicenseProvider;
    private final HostLicenseInformation hostLicenseInformation;
    private final LicensingUsageVerifier licensingUsageVerifier;
    private final ApplicationProperties applicationProperties;

    public UpmLinkBuilder(UpmUriBuilder uriBuilder, PluginRestartRequiredService restartRequiredService, PluginMetadataAccessor metadata, AsynchronousTaskManager asynchronousTaskManager, PermissionEnforcer permissionEnforcer, SysPersisted sysPersisted, ProductMailService mailService, SafeModeAccessor safeMode, PluginControlHandlerRegistry pluginControlHandlerRegistry, RoleBasedLicensingPluginService roleBasedLicensingService, UpmHostApplicationInformation appInfo, HostLicenseProvider hostLicenseProvider, HostLicenseInformation hostLicenseInformation, UpmAppManager appManager, LicensingUsageVerifier licensingUsageVerifier, ApplicationProperties applicationProperties) {
        super(uriBuilder, restartRequiredService, asynchronousTaskManager, permissionEnforcer, metadata, appManager);
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.mailService = Objects.requireNonNull(mailService, "mailService");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.roleBasedLicensingService = Objects.requireNonNull(roleBasedLicensingService, "roleBasedLicensingService");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        this.licensingUsageVerifier = licensingUsageVerifier;
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    private Map<String, URI> generateDefaultLinksMap() {
        LinksMapBuilder builder = this.builder().put("installed", this.uriBuilder.buildPluginCollectionUri()).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "marketplace", this.uriBuilder.buildUpmMarketplaceUri()).putIfPermitted(Permission.GET_NOTIFICATIONS, "notifications", this.uriBuilder.buildNotificationCollectionUri()).putIfPermitted(Permission.GET_PLUGIN_REQUESTS, "requests", this.uriBuilder.buildPluginRequestCollectionResourceUri()).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "categories", this.uriBuilder.buildCategoriesUri()).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "banners", this.uriBuilder.buildBannersUri(0));
        for (UpmMarketplaceFilter f : UpmMarketplaceFilter.values()) {
            builder.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, f.getLinkRel(), this.uriBuilder.buildAvailablePluginCollectionUri(f, Option.none(String.class), 0));
        }
        return builder.build();
    }

    @Override
    public LinksMapBuilder buildLinksFor(URI selfLink, boolean addConditionalLinks) {
        Map<String, URI> filteredLinks = this.generateDefaultLinksMap().entrySet().stream().filter(e -> !selfLink.equals(e.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        LinksMapBuilder builder = super.buildLinksFor(selfLink, addConditionalLinks).putAll(filteredLinks);
        if (addConditionalLinks) {
            this.addSafeModeLinks(builder);
            this.addOsgiLinks(builder);
        }
        return builder;
    }

    private LinksMapBuilder addSafeModeLinks(LinksMapBuilder builder) {
        if (this.safeMode.isSafeMode()) {
            builder.putIfPermitted(Permission.MANAGE_SAFE_MODE, "exit-safe-mode-restore", this.uriBuilder.buildExitSafeModeUri(false)).putIfPermitted(Permission.MANAGE_SAFE_MODE, "exit-safe-mode-keep", this.uriBuilder.buildExitSafeModeUri(true));
        } else {
            builder.putIfPermitted(Permission.MANAGE_SAFE_MODE, "enter-safe-mode", this.uriBuilder.buildSafeModeUri());
        }
        return builder;
    }

    private LinksMapBuilder addOsgiLinks(LinksMapBuilder builder) {
        builder.putIfPermitted(Permission.GET_OSGI_STATE, "osgi-bundles", this.uriBuilder.buildOsgiBundleCollectionUri()).putIfPermitted(Permission.GET_OSGI_STATE, "osgi-services", this.uriBuilder.buildOsgiServiceCollectionUri()).putIfPermitted(Permission.GET_OSGI_STATE, "osgi-packages", this.uriBuilder.buildOsgiPackageCollectionUri());
        return builder;
    }

    public Map<String, URI> buildPermissionedUris() {
        LinksMapBuilder linksMapBuilder = new LinksMapBuilder(this.permissionEnforcer);
        linksMapBuilder.put("upmUriToken", this.uriBuilder.buildPluginCollectionUri()).put("upmUriInstalled", this.uriBuilder.buildInstalledMarketplacePluginCollectionUri()).putIfPermitted(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_FILE, "upmUriInstallFile", this.uriBuilder.buildPluginCollectionUri()).putIfPermitted(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI, "upmUriInstallUri", this.uriBuilder.buildPluginCollectionUri());
        for (UpmMarketplaceFilter f : UpmMarketplaceFilter.values()) {
            linksMapBuilder.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, this.getFilterResourceVariableName(f), this.uriBuilder.buildAvailablePluginCollectionUri(f, Option.none(String.class), 0));
        }
        linksMapBuilder.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "upmUriCategories", this.uriBuilder.buildCategoriesUri()).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "upmUriBanners", this.uriBuilder.buildBannersUri(0)).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "upmUriMarketplace", this.uriBuilder.buildUpmMarketplaceUri()).putIfPermitted(Permission.GET_PURCHASED_PLUGINS, "upmUriPurchases", this.uriBuilder.buildPurchasedPluginCollectionUri()).putIfPermitted(Permission.GET_PURCHASED_PLUGINS, "upmUriUpdateLicenses", this.uriBuilder.buildPurchasedPluginCheckUri()).putIfPermitted(Permission.GET_PURCHASED_PLUGINS, "upmUriUpdateLicensesSigned", this.uriBuilder.buildPurchasedPluginCheckSignedUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "upmUriAuditLog", this.uriBuilder.buildAuditLogFeedUri()).putIfPermitted(Permission.GET_PRODUCT_UPDATE_COMPATIBILITY, "upmUriProductUpdates", this.uriBuilder.buildProductUpdatesUri()).putIfPermitted(Permission.GET_SAFE_MODE, "upmUriSafeMode", this.uriBuilder.buildSafeModeUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "upmUriPurgeAfter", this.uriBuilder.buildAuditLogPurgeAfterUri()).putIfPermitted(Permission.MANAGE_AUDIT_LOG, "upmUriManagePurgeAfter", this.uriBuilder.buildAuditLogPurgeAfterUri()).putIfPermitted(Permission.GET_OSGI_STATE, "upmUriOsgiBundles", this.uriBuilder.buildOsgiBundleCollectionUri()).putIfPermitted(Permission.GET_OSGI_STATE, "upmUriOsgiServices", this.uriBuilder.buildOsgiServiceCollectionUri()).putIfPermitted(Permission.GET_OSGI_STATE, "upmUriOsgiPackages", this.uriBuilder.buildOsgiPackageCollectionUri()).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "upmUriProductVersion", this.uriBuilder.buildProductVersionUri()).putIfPermitted(Permission.CREATE_PLUGIN_REQUEST, "upmUriCreateRequests", this.uriBuilder.buildPluginRequestCollectionResourceUri()).put("upmUriPluginRequestsPage", this.uriBuilder.buildUpmPluginRequestsUri()).putIfPermitted(Permission.GET_AUDIT_LOG, "upmUriAuditLogServlet", this.uriBuilder.buildUpmAuditLogUri("")).put("upmUriPendingTasks", this.uriBuilder.buildLegacyPendingTasksUri()).putIfPermitted(Permission.ADD_ANALYTICS_ACTIVITY, "upmUriAnalytics", this.uriBuilder.buildAnalyticsUri()).putIfPermitted(Permission.MANAGE_ON_PREMISE_SETTINGS, "upmUriSettings", this.uriBuilder.buildUpmSettingsUri()).putIfPermittedAndConditioned(Permission.MANAGE_USER_SETTINGS, this.hasAvailableUserSetting(), "upmUriUserSettings", this.uriBuilder.buildUserSettingsUri()).putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, "upmUriCreateEvalLicense", this.uriBuilder.buildHamletCreateEvalLicenseUri()).put("upmUriPacStatus", this.uriBuilder.buildPacStatusUri()).put("upmUriManage", this.uriBuilder.buildUpmUri()).put("upmUriAcceptedMpacEula", this.uriBuilder.buildUserSettingsUri(UserSettings.ACCEPTED_MARKETPLACE_EULA)).putIfPermitted(Permission.MANAGE_APPLICATION_LICENSES, "upmUriManageApplications", this.uriBuilder.buildManageApplicationsUri());
        if (!this.sysPersisted.is(UpmSettings.REQUESTS_DISABLED)) {
            linksMapBuilder.put("upmUriViewRequests", this.uriBuilder.buildPluginRequestCollectionResourceUri());
        }
        if (!this.sysPersisted.is(UpmSettings.PAC_DISABLED)) {
            linksMapBuilder.put("mpacBaseUrl", Sys.getMpacBaseUrl());
        }
        if (!this.safeMode.isSafeMode()) {
            linksMapBuilder.putIfPermitted(Permission.MANAGE_SAFE_MODE, "upmUriEnterSafeMode", this.uriBuilder.buildSafeModeUri());
        }
        return linksMapBuilder.build();
    }

    private String getFilterResourceVariableName(UpmMarketplaceFilter f) {
        switch (f) {
            case FEATURED: {
                return "upmUriFeatured";
            }
            case HIGHEST_RATED: {
                return "upmUriHighestRated";
            }
            case TOP_GROSSING: {
                return "upmUriTopGrossing";
            }
            case MOST_POPULAR: {
                return "upmUriPopular";
            }
            case TRENDING: {
                return "upmUriTrending";
            }
            case BY_ATLASSIAN: {
                return "upmUriByAtlassian";
            }
            case TOP_VENDOR: {
                return "upmUriTopVendor";
            }
        }
        return "upmUriAvailable";
    }

    public LinksMapBuilder buildLinksForAvailablePlugin(URI selfLink, Option<Plugin> installedPlugin, String pluginKey, Option<PluginLicense> possiblePluginLicense, AddonBase availablePlugin, AddonVersionBase availablePluginVersion) {
        boolean dataCenterIncompatible;
        LinksMapBuilder builder = this.buildLinkForSelf(selfLink);
        boolean usesLicensing = availablePluginVersion.getPaymentModel().equals(PaymentModel.PAID_VIA_ATLASSIAN);
        builder.putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "singlePluginViewLink", this.uriBuilder.buildUpmSinglePluginViewUri(pluginKey));
        boolean bl = dataCenterIncompatible = installedPlugin.exists((Predicate<Plugin>)((Predicate)p -> MarketplacePlugins.isDataCenterIncompatible(p, this.appInfo))) || MarketplacePlugins.isDataCenterIncompatible(availablePluginVersion, this.appInfo);
        if (dataCenterIncompatible) {
            builder.putIfPermitted(Permission.REQUEST_PLUGIN_UPDATE, installedPlugin, "request-update", this.uriBuilder.buildRequestUpdateUri(pluginKey));
        }
        for (Plugin ip : installedPlugin) {
            builder.putIfPermitted(Permission.GET_INSTALLED_PLUGINS, installedPlugin, "marketplace-summary", this.uriBuilder.buildInstalledMarketplacePluginSummaryUri(pluginKey));
        }
        this.addLinksForAvailableMarketplaceAddonBase(builder, availablePlugin, availablePluginVersion);
        return usesLicensing ? this.addPluginLicenseLinks(builder, pluginKey, installedPlugin, possiblePluginLicense, Option.some(availablePluginVersion)) : builder;
    }

    public LinksMapBuilder buildLinksForAvailablePluginDetail(URI selfLink, Option<Plugin> installedPlugin, String pluginKey, Option<PluginLicense> possiblePluginLicense, Addon availablePlugin, AddonVersion availablePluginVersion) {
        LinksMapBuilder builder = this.buildLinksForAvailablePlugin(selfLink, installedPlugin, pluginKey, possiblePluginLicense, availablePlugin, availablePluginVersion);
        return this.addLinksForAvailableMarketplaceAddonDetail(builder, availablePlugin, availablePluginVersion);
    }

    public LinksMapBuilder buildLinksForInstalledMarketplacePlugin(Plugin plugin, Option<PluginLicense> possiblePluginLicense, boolean usesLicensing) {
        Option<Plugin> pluginOption = Option.some(plugin);
        LinksMapBuilder builder = this.buildLinkForSelf(this.uriBuilder.buildInstalledMarketplacePluginUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, Option.some(plugin), "alternate", this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-summary", this.uriBuilder.buildPluginSummaryUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "marketplace-summary", this.uriBuilder.buildInstalledMarketplacePluginSummaryUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-icon", this.uriBuilder.buildPluginIconLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-logo", this.uriBuilder.buildPluginLogoLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-details", this.uriBuilder.buildUpmTabPluginUri("manage", plugin.getKey())).put("pac-details", this.uriBuilder.buildPacPluginDetailsResourceUri(plugin.getKey(), plugin.getVersion())).put("manage", this.uriBuilder.buildUpmTabPluginUri("manage", plugin.getKey()));
        for (URI postInstallUrl : this.metadata.getPostInstallUri(plugin)) {
            builder.putIfPermitted(Permission.GET_POST_INSTALL_POST_UPDATE_PAGE, pluginOption, "post-install", postInstallUrl);
        }
        for (URI postUpdateUrl : this.metadata.getPostUpdateUri(plugin)) {
            builder.putIfPermitted(Permission.GET_POST_INSTALL_POST_UPDATE_PAGE, pluginOption, "post-update", postUpdateUrl);
        }
        if (usesLicensing && !this.safeMode.isSafeMode() && this.permissionEnforcer.hasVendorFeedbackPermission(plugin)) {
            builder.put("vendor-feedback", this.uriBuilder.buildVendorFeedbackUri(plugin.getKey()));
        }
        return usesLicensing ? this.addPluginLicenseLinks(builder, plugin.getKey(), Option.some(plugin), possiblePluginLicense, Option.none()) : builder;
    }

    public LinksMapBuilder buildLinksForPluginLicense(String pluginKey, Option<Plugin> plugin, Option<PluginLicense> possiblePluginLicense) {
        LinksMapBuilder builder = this.buildLinkForSelf(this.uriBuilder.buildPluginLicenseUri(pluginKey)).putIfPermittedAndConditioned(Permission.GET_INSTALLED_PLUGINS, plugin.isDefined(), "alternate", this.uriBuilder.buildPluginUri(pluginKey)).putIfPermittedAndConditioned(Permission.GET_AVAILABLE_PLUGINS, !plugin.isDefined(), "alternate", this.uriBuilder.buildAvailablePluginUri(pluginKey));
        return this.addPluginLicenseLinks(builder, pluginKey, plugin, possiblePluginLicense, Option.none());
    }

    private LinksMapBuilder addPluginLicenseLinks(LinksMapBuilder builder, String pluginKey, Option<Plugin> plugin, Option<PluginLicense> possiblePluginLicense, Option<AddonVersionBase> availablePluginVersion) {
        boolean isDataCenter;
        boolean carebearSpecifcPlugin;
        Option<Integer> roleCount = RoleBasedLicensingPluginServiceUtil.getRoleCount(this.roleBasedLicensingService, plugin.map(Plugins.toPlugPlugin), possiblePluginLicense);
        HostApplicationLicenseAttributes applicationLicenseAttributes = this.hostLicenseProvider.getHostApplicationLicenseAttributes();
        boolean isHostDataCenter = applicationLicenseAttributes == null ? false : applicationLicenseAttributes.isDataCenter();
        boolean isPluginDataCenter = (Boolean)availablePluginVersion.map(AddonVersionBase::isDataCenterStatusCompatible).orElse(plugin.map(Plugins::isStatusDataCenterCompatibleAccordingToPluginDescriptor)).getOrElse(false);
        builder.putIfPermitted(Permission.GET_PLUGIN_LICENSE, plugin, "license", this.uriBuilder.buildPluginLicenseUri(pluginKey)).putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "update-license", this.uriBuilder.buildPluginLicenseUri(pluginKey)).putIfPermittedAndConditioned(Permission.MANAGE_PLUGIN_LICENSE, plugin, this.isAddonLicenseDownloadableFromHamlet(), "check-license", this.uriBuilder.buildPurchasedPluginCheckUri(pluginKey)).putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "validate-downgrade", this.uriBuilder.buildPluginLicenseValidateDowngradeUri(pluginKey)).putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "license-callback", this.uriBuilder.buildLicenseReceiptUri(pluginKey));
        boolean bl = carebearSpecifcPlugin = plugin.isDefined() && this.licensingUsageVerifier.isCarebearSpecificPlugin(plugin.get().getPlugin()) || LicensedPlugins.isServerWithCloudAlternative(this.appInfo.getHostingType(), this.applicationProperties.getPlatformId());
        if (PluginLicenses.isPluginTryable(possiblePluginLicense, carebearSpecifcPlugin)) {
            builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "try", this.uriBuilder.buildMacPluginLicenseUri(isHostDataCenter && isPluginDataCenter, pluginKey, "try"));
        }
        boolean bl2 = isDataCenter = isHostDataCenter && (Boolean)possiblePluginLicense.map(PluginLicense::isDataCenter).getOrElse(isPluginDataCenter) != false;
        if (PluginLicenses.isPluginCrossgradeable(plugin.map(Plugin::getPluginInformation), this.hostLicenseInformation, possiblePluginLicense)) {
            builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "crossgrade", this.uriBuilder.buildAppLicenseCrossgradeResourceUri(pluginKey));
        }
        if (PluginLicenses.isPluginBuyable(possiblePluginLicense, carebearSpecifcPlugin)) {
            builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "new", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, pluginKey, "new"));
        } else if (PluginLicenses.isPluginUpgradable(possiblePluginLicense, roleCount)) {
            builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "upgrade", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, pluginKey, "upgrade"));
        } else if (PluginLicenses.isPluginRenewable(possiblePluginLicense, roleCount) || PluginLicenses.isCrossgradableAppAndHostIsTrialDcAndAppWillFailOnFullDc(plugin.map(Plugin::getPluginInformation), this.hostLicenseInformation, possiblePluginLicense)) {
            builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "renew", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, pluginKey, "renew"));
        } else if (PluginLicenses.isPluginRenewableRequiringContact(possiblePluginLicense, roleCount)) {
            for (Plugin p : plugin) {
                for (String vendorUrl : Option.option(p.getPluginInformation().getVendorUrl())) {
                    builder.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, plugin, "renew-requires-contact", URI.create(vendorUrl));
                }
            }
        }
        return builder;
    }

    private boolean hasAvailableUserSetting() {
        return this.emailNotificationsAreEnabled();
    }

    private boolean emailNotificationsAreEnabled() {
        return !this.sysPersisted.is(UpmSettings.EMAIL_DISABLED) && this.isMailConfigured();
    }

    private boolean isMailConfigured() {
        Iterator<Boolean> iterator = UpmSysResource.isMailServerConfigured().iterator();
        if (iterator.hasNext()) {
            Boolean mailServerConfigured = iterator.next();
            return mailServerConfigured;
        }
        return !this.mailService.isDisabled() && this.mailService.isConfigured();
    }

    public Map<String, String> buildLinkTemplatesForInstallablePluginCollection() {
        if (this.permissionEnforcer.hasPermission(Permission.GET_INSTALLED_PLUGINS)) {
            return Collections.singletonMap("installed", this.uriBuilder.buildInstalledMarketplacePluginUriTemplate());
        }
        return Collections.emptyMap();
    }

    private boolean isAddonLicenseDownloadableFromHamlet() {
        if (!UpmSys.isCheckLicenseFeatureEnabled()) {
            return false;
        }
        return !PurchasedAddonsServlet.getPurchasedAddonsNonFunctionalReason(this.hostLicenseProvider.getHostApplicationLicenseAttributes()).isDefined();
    }

    private LinksMapBuilder addLinksForAvailableMarketplaceAddonBase(LinksMapBuilder builder, AddonBase addon, AddonVersionBase version) {
        builder.put("details", this.uriBuilder.buildPacPluginDetailsUri(addon)).put("external-binary", version.getExternalLinkUri(AddonVersionExternalLinkType.BINARY));
        if (version.getPaymentModel().equals(PaymentModel.PAID_VIA_ATLASSIAN)) {
            builder.put("pricing", UpmFugueConverters.toUpmOption(addon.getPricingUri(MarketplacePlugins.getPricingType(version))).map(Sys.resolveMarketplaceUri())).put("pricing-page", UpmFugueConverters.toUpmOption(addon.getPricingDetailsPageUri()).map(Sys.resolveMarketplaceUri()));
            builder.put("geo-ip", this.uriBuilder.buildGeoIpUri());
        }
        for (URI binaryUri : version.getArtifactUri()) {
            if (!this.permissionEnforcer.hasInProcessInstallationFromUriPermission(binaryUri)) continue;
            builder.put("binary", binaryUri);
        }
        return builder;
    }

    private LinksMapBuilder addLinksForAvailableMarketplaceAddonDetail(LinksMapBuilder builder, Addon addon, AddonVersion version) {
        builder.put("issue-tracker", addon.getExternalLinkUri(AddonExternalLinkType.ISSUE_TRACKER)).put("wiki", addon.getExternalLinkUri(AddonExternalLinkType.WIKI)).put("forums", addon.getExternalLinkUri(AddonExternalLinkType.FORUMS)).put("privacy", addon.getExternalLinkUri(AddonExternalLinkType.PRIVACY)).put("support", (io.atlassian.fugue.Option<URI>)addon.getSupportDetailsPageUri().map(Sys.resolveMarketplaceUri()));
        builder.put("learn-more", version.getExternalLinkUri(AddonVersionExternalLinkType.LEARN_MORE)).put("documentation", version.getExternalLinkUri(AddonVersionExternalLinkType.DOCUMENTATION)).put("release-notes", version.getExternalLinkUri(AddonVersionExternalLinkType.RELEASE_NOTES)).put("javadocs", version.getExternalLinkUri(AddonVersionExternalLinkType.JAVADOC)).put("license-type", version.getExternalLinkUri(AddonVersionExternalLinkType.LICENSE)).put("donate", version.getExternalLinkUri(AddonVersionExternalLinkType.DONATE)).put("purchase", version.getExternalLinkUri(AddonVersionExternalLinkType.PURCHASE)).put("eula", version.getExternalLinkUri(AddonVersionExternalLinkType.EULA));
        return builder;
    }
}

