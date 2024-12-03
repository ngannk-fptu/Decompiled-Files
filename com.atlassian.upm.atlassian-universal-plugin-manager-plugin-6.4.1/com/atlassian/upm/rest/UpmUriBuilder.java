/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserKey
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.upm.rest;

import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.analytics.rest.resources.AnalyticsResource;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.ChangeRequiringRestartCollectionResource;
import com.atlassian.upm.core.rest.resources.ChangeRequiringRestartResource;
import com.atlassian.upm.core.rest.resources.PluginMediaResource;
import com.atlassian.upm.core.test.rest.resources.ActiveEditionResource;
import com.atlassian.upm.core.test.rest.resources.BuildNumberResource;
import com.atlassian.upm.core.test.rest.resources.MpacBaseUrlResource;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.rest.resources.NotificationCollectionResource;
import com.atlassian.upm.notification.rest.resources.NotificationResource;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.rest.resources.BundleCollectionResource;
import com.atlassian.upm.osgi.rest.resources.BundleResource;
import com.atlassian.upm.osgi.rest.resources.PackageCollectionResource;
import com.atlassian.upm.osgi.rest.resources.PackageResource;
import com.atlassian.upm.osgi.rest.resources.ServiceCollectionResource;
import com.atlassian.upm.osgi.rest.resources.ServiceResource;
import com.atlassian.upm.request.rest.resources.PluginRequestCollectionResource;
import com.atlassian.upm.request.rest.resources.PluginRequestStatusResource;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
import com.atlassian.upm.rest.resources.AppCrossgradeResource;
import com.atlassian.upm.rest.resources.AvailablePluginOrPluginCollectionResource;
import com.atlassian.upm.rest.resources.BannerCollectionResource;
import com.atlassian.upm.rest.resources.CategoryCollectionResource;
import com.atlassian.upm.rest.resources.EnablementStateResource;
import com.atlassian.upm.rest.resources.InstalledMarketplacePluginCollectionResource;
import com.atlassian.upm.rest.resources.InstalledMarketplacePluginResource;
import com.atlassian.upm.rest.resources.InstalledMarketplacePluginSummaryResource;
import com.atlassian.upm.rest.resources.PacPluginDetailsResource;
import com.atlassian.upm.rest.resources.PacStatusResource;
import com.atlassian.upm.rest.resources.PluginLicenseResource;
import com.atlassian.upm.rest.resources.ProductUpdatePluginCompatibilityResource;
import com.atlassian.upm.rest.resources.ProductUpdatesResource;
import com.atlassian.upm.rest.resources.ProductVersionResource;
import com.atlassian.upm.rest.resources.PurchasedPluginCheckResource;
import com.atlassian.upm.rest.resources.PurchasedPluginCollectionResource;
import com.atlassian.upm.rest.resources.RecommendedPluginCollectionResource;
import com.atlassian.upm.rest.resources.RequestPluginUpdateResource;
import com.atlassian.upm.rest.resources.SafeModeResource;
import com.atlassian.upm.rest.resources.ScheduledJobResource;
import com.atlassian.upm.rest.resources.SelfUpdateCompletionResource;
import com.atlassian.upm.rest.resources.UpmSettingsResource;
import com.atlassian.upm.rest.resources.UserSettingsResource;
import com.atlassian.upm.rest.resources.VendorFeedbackResource;
import com.atlassian.upm.rest.resources.disableall.DisableAllIncompatibleResource;
import com.atlassian.upm.rest.resources.updateall.UpdateAllResource;
import com.atlassian.upm.test.rest.resources.BundleIdResource;
import com.atlassian.upm.test.rest.resources.MarketplaceCacheResource;
import com.atlassian.upm.test.rest.resources.RunScheduledJobResource;
import com.atlassian.upm.test.rest.resources.SetTestLicenseResource;
import com.atlassian.upm.test.rest.resources.UpmSysResource;
import com.atlassian.upm.test.rest.resources.UserKeyResource;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.core.UriBuilder;

public class UpmUriBuilder
extends BaseUriBuilder {
    public static final String LICENSED_MESSAGE = "licensed";
    public static final String TRIAL_STOPPED_MESSAGE = "trial_stopped";
    public static final String UNSUBSCRIBED_MESSAGE = "unsubscribed";
    private static final String DATA_CENTER_FOR_HAMS = ".data-center";

    public UpmUriBuilder(ApplicationProperties applicationProperties) {
        super(applicationProperties, "/rest/plugins/1.0");
    }

    public final String buildInstalledMarketplacePluginUriTemplate() {
        return this.newPluginBaseUriBuilder().build(new Object[0]).toASCIIString() + "/{pluginKey}/marketplace";
    }

    public final URI buildInstalledMarketplacePluginUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(InstalledMarketplacePluginResource.class).build(new Object[]{pluginKey});
    }

    public final URI buildPluginLicenseUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginLicenseResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildInstalledMarketplacePluginSummaryUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(InstalledMarketplacePluginSummaryResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPluginBannerLocationUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginMediaResource.class).path("plugin-banner").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildVendorIconLocationUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginMediaResource.class).path("vendor-icon").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildVendorLogoLocationUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginMediaResource.class).path("vendor-logo").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildSafeModeUri() {
        return this.newPluginBaseUriBuilder().path(SafeModeResource.class).build(new Object[0]);
    }

    public final URI buildExitSafeModeUri(boolean keepState) {
        return this.newPluginBaseUriBuilder().path(SafeModeResource.class).queryParam("keepState", new Object[]{keepState}).build(new Object[0]);
    }

    public final URI buildEnablementStateUri() {
        return this.newPluginBaseUriBuilder().path(EnablementStateResource.class).build(new Object[0]);
    }

    public final URI buildUserKeyResource() {
        return this.newPluginBaseUriBuilder().path(UserKeyResource.class).build(new Object[0]);
    }

    public final URI buildBuildNumberUri() {
        return this.newPluginBaseUriBuilder().path(BuildNumberResource.class).build(new Object[0]);
    }

    public final URI buildDataCenterEnabledUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("data-center").build(new Object[0]);
    }

    public final URI buildMailServerConfiguredUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("mail-server").build(new Object[0]);
    }

    public final URI buildSenUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("sen").build(new Object[0]);
    }

    public final URI buildAutoInstallRemotePluginsUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("auto-install-remote-plugins").build(new Object[0]);
    }

    public final URI buildCheckLicenseFeatureEnableUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("check-license").build(new Object[0]);
    }

    public final URI buildPurchasedAddonsFeatureEnableUri() {
        return this.newPluginBaseUriBuilder().path(UpmSysResource.class).path("purchased-addons").build(new Object[0]);
    }

    public final URI buildAppLicenseCrossgradeResourceUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(AppCrossgradeResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildMarketplaceCacheUri() {
        return this.newPluginBaseUriBuilder().path(MarketplaceCacheResource.class).build(new Object[0]);
    }

    public final URI buildPacStatusUri() {
        return this.newPluginBaseUriBuilder().path(PacStatusResource.class).build(new Object[0]);
    }

    public final URI buildPluginRequestStatusUri() {
        return this.newPluginBaseUriBuilder().path(PluginRequestStatusResource.class).build(new Object[0]);
    }

    public final URI buildCategoriesUri() {
        return this.newPluginBaseUriBuilder().path(CategoryCollectionResource.class).build(new Object[0]);
    }

    public final URI buildBannersUri(int offset) {
        return this.addOffset(this.newPluginBaseUriBuilder().path(BannerCollectionResource.class), offset).build(new Object[0]);
    }

    public final URI buildMpacBaseUrlUri() {
        return this.newPluginBaseUriBuilder().path(MpacBaseUrlResource.class).build(new Object[0]);
    }

    public final URI buildActiveEditionResource() {
        return this.newPluginBaseUriBuilder().path(ActiveEditionResource.class).build(new Object[0]);
    }

    public final URI buildPluginRequestCollectionResourceUri() {
        return this.buildPluginRequestCollectionResourceUri(null, null);
    }

    public final URI buildPluginRequestCollectionResourceUri(Integer max, Integer offset) {
        return this.addStartIndex(this.addMaxResults(this.newPluginBaseUriBuilder().path(PluginRequestCollectionResource.class), max), offset).build(new Object[0]);
    }

    public final URI buildPluginRequestCollectionResourceUri(Integer max, Integer offset, boolean excludeUserRequests) {
        return this.addExcludeUserRequests(this.addStartIndex(this.addMaxResults(this.newPluginBaseUriBuilder().path(PluginRequestCollectionResource.class), max), offset), excludeUserRequests).build(new Object[0]);
    }

    public final URI buildPluginRequestDismissCollectionResourceUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginRequestCollectionResource.class).path(UpmUriEscaper.escape(pluginKey)).build(new Object[0]);
    }

    public final URI buildPluginRequestResourceUri(String pluginKey, UserKey userKey) {
        return this.newPluginBaseUriBuilder().path(PluginRequestCollectionResource.class).path(UpmUriEscaper.escape(pluginKey)).path(userKey.getStringValue()).build(new Object[0]);
    }

    public final URI buildInstalledMarketplacePluginCollectionUri() {
        return this.newPluginBaseUriBuilder().path(InstalledMarketplacePluginCollectionResource.class).build(new Object[0]);
    }

    public final URI buildPacPluginDetailsResourceUri(String pluginKey, String pluginVersion) {
        return this.newPluginBaseUriBuilder().path(PacPluginDetailsResource.class).build(new Object[]{pluginKey, pluginVersion});
    }

    public final URI buildOsgiBundleCollectionUri() {
        return this.buildOsgiBundleCollectionUri(null);
    }

    public final URI buildOsgiBundleCollectionUri(String term) {
        UriBuilder builder = this.newPluginBaseUriBuilder().path(BundleCollectionResource.class);
        return term == null ? builder.build(new Object[0]) : builder.queryParam("q", new Object[]{Objects.requireNonNull(term, "term")}).build(new Object[0]);
    }

    public final URI buildOsgiBundleUri(long id) {
        return this.newPluginBaseUriBuilder().path(BundleResource.class).build(new Object[]{id});
    }

    public final URI buildOsgiBundleUri(Bundle bundle) {
        return this.buildOsgiBundleUri(Objects.requireNonNull(bundle, "bundle").getId());
    }

    public final URI buildOsgiServiceCollectionUri() {
        return this.newPluginBaseUriBuilder().path(ServiceCollectionResource.class).build(new Object[0]);
    }

    public final URI buildOsgiServiceUri(long id) {
        return this.newPluginBaseUriBuilder().path(ServiceResource.class).build(new Object[]{id});
    }

    public final URI buildOsgiServiceUri(Service service) {
        return this.buildOsgiServiceUri(Objects.requireNonNull(service, "service").getId());
    }

    public final URI buildOsgiPackageCollectionUri() {
        return this.newPluginBaseUriBuilder().path(PackageCollectionResource.class).build(new Object[0]);
    }

    public final URI buildOsgiPackageUri(long bundleId, String name, Version version) {
        return this.newPluginBaseUriBuilder().path(PackageResource.class).build(new Object[]{bundleId, Objects.requireNonNull(name, "name"), Objects.requireNonNull(version, "version")});
    }

    public final URI buildOsgiPackageUri(Package pkg) {
        Objects.requireNonNull(pkg, "pkg");
        return this.buildOsgiPackageUri(pkg.getExportingBundle().getId(), pkg.getName(), pkg.getVersion());
    }

    public final URI buildAvailablePluginCollectionUri(UpmMarketplaceFilter filter, Option<String> query, int offset) {
        return this.addSearchQuery(this.addOffset(this.newPluginBaseUriBuilder().path(AvailablePluginOrPluginCollectionResource.class), offset), query).build(new Object[]{filter.getKey()});
    }

    public final URI buildAvailablePluginCollectionUriWithCategory(UpmMarketplaceFilter filter, Option<String> category, int offset) {
        return this.addCategory(this.addOffset(this.newPluginBaseUriBuilder().path(AvailablePluginOrPluginCollectionResource.class), offset), category).build(new Object[]{filter.getKey()});
    }

    public final URI buildRecommendedPluginCollectionUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(RecommendedPluginCollectionResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPurchasedPluginCollectionUri() {
        return this.newPluginBaseUriBuilder().path(PurchasedPluginCollectionResource.class).build(new Object[0]);
    }

    public final URI buildAvailablePluginUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(AvailablePluginOrPluginCollectionResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildVendorFeedbackUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(VendorFeedbackResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildRequestUpdateUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(RequestPluginUpdateResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    private final URI buildPacPluginDetailsUriInternal(Option<URI> maybeUri) {
        Iterator<URI> iterator = maybeUri.iterator();
        if (iterator.hasNext()) {
            URI uri = iterator.next();
            return this.makeAbsoluteMpacUri(uri);
        }
        return URI.create(UpmSys.getMpacWebsiteBaseUrl());
    }

    public final URI buildPacPluginDetailsUri(AddonBase plugin) {
        Objects.requireNonNull(plugin, "plugin");
        return this.buildPacPluginDetailsUriInternal(Option.some(plugin.getAlternateUri()));
    }

    public final URI buildNotificationCollectionUri() {
        return this.newPluginBaseUriBuilder().path(NotificationCollectionResource.class).build(new Object[0]);
    }

    public final URI buildNotificationCollectionUri(UserKey userKey) {
        return this.newPluginBaseUriBuilder().path(NotificationCollectionResource.class).path(userKey.getStringValue()).build(new Object[0]);
    }

    public final URI buildNotificationCollectionUri(UserKey userKey, NotificationType type) {
        return this.newPluginBaseUriBuilder().path(NotificationCollectionResource.class).path(userKey.getStringValue()).path(type.getKey()).build(new Object[0]);
    }

    public final URI buildNotificationUri(UserKey userKey, NotificationType type, String pluginKey) {
        return this.newPluginBaseUriBuilder().path(NotificationResource.class).build(new Object[]{userKey.getStringValue(), type.getKey(), pluginKey});
    }

    public final URI buildPurchasedPluginCheckUri() {
        return this.newPluginBaseUriBuilder().path(PurchasedPluginCheckResource.class).build(new Object[0]);
    }

    public final URI buildPurchasedPluginCheckSignedUri() {
        return this.newPluginBaseUriBuilder().path(PurchasedPluginCheckResource.class).path("signed").build(new Object[0]);
    }

    public final URI buildPurchasedPluginCheckUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PurchasedPluginCheckResource.class).path(UpmUriEscaper.escape(pluginKey)).build(new Object[0]);
    }

    public final URI buildPluginLicenseValidateDowngradeUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginLicenseResource.class).path("validate-downgrade").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildAnalyticsUri() {
        return this.newPluginBaseUriBuilder().path(AnalyticsResource.class).build(new Object[0]);
    }

    public URI buildUpmSettingsUri() {
        return this.newPluginBaseUriBuilder().path(UpmSettingsResource.class).build(new Object[0]);
    }

    public URI buildUserSettingsUri() {
        return this.newPluginBaseUriBuilder().path(UserSettingsResource.class).build(new Object[0]);
    }

    public URI buildUserSettingsUri(UserSettings setting) {
        return this.newPluginBaseUriBuilder().path(UserSettingsResource.class).path(setting.getKey()).build(new Object[0]);
    }

    public URI buildSelfUpdateCompletionUri() {
        return this.newPluginBaseUriBuilder().path(SelfUpdateCompletionResource.class).build(new Object[0]);
    }

    public final URI buildUpmUri(String manageFilter, boolean absolute) {
        URI uri = this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/manage/" + manageFilter).build(new Object[0]);
        return absolute ? this.makeAbsolute(uri) : uri;
    }

    public final URI buildUpmUri(String manageFilter, String pluginKey, boolean absolute) {
        return this.addFragment(this.buildUpmUri(manageFilter, absolute), "manage/" + pluginKey);
    }

    public final URI buildUpmMarketplaceUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace").build(new Object[0]));
    }

    public final URI buildUpmSinglePluginViewUri(String pluginKey) {
        return this.buildUpmSinglePluginViewUri(pluginKey, Option.none());
    }

    public final URI buildUpmSinglePluginViewUri(String pluginKey, Option<String> filter) {
        UriBuilder path = this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace").path("plugins").path(pluginKey);
        for (String f : filter) {
            path = path.queryParam("filter", new Object[]{f});
        }
        return this.makeAbsolute(path.build(new Object[0]));
    }

    public final URI buildUpmUserSettingsUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace/settings").build(new Object[0]));
    }

    public final URI buildUpmViewPluginRequestsUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace/most-requested").build(new Object[0]));
    }

    public final URI buildUpmPluginRequestsUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace/requests").build(new Object[0]));
    }

    public final URI buildUpmDacLandingPageUri(String source) {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/develop").queryParam("source", new Object[]{source}).build(new Object[0]));
    }

    public final URI buildUpmMarketplacePluginCategoryUri(String category) {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/marketplace").queryParam("category", new Object[]{category}).build(new Object[0]));
    }

    public final URI buildUpmTabUri(String tabName) {
        return this.addFragment(this.buildUpmUri(), tabName);
    }

    public final URI buildUpmTabPluginUri(String tabName, String pluginKey) {
        return this.buildUpmTabUri(tabName + "/" + pluginKey);
    }

    public final URI emailUri(URI uri, EmailType emailType) {
        return UriBuilder.fromUri((URI)uri).queryParam("source", new Object[]{"email"}).queryParam("source-type", new Object[]{emailType.name().toLowerCase()}).build(new Object[0]);
    }

    public final URI buildUpmTabPluginUri(String tabName, String pluginKey, String messageParams) {
        return this.buildUpmTabUri(tabName + "/" + pluginKey + "/" + messageParams);
    }

    public final URI buildUpmPurchasedAddonsUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/purchases").build(new Object[0]));
    }

    public final URI buildLicenseReceiptUri(String pluginKey) {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/license/" + pluginKey).build(new Object[0]));
    }

    public final URI buildAbsoluteConfigureUrl(URI relativeUrl) {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path(relativeUrl.getPath()).replaceQuery(relativeUrl.getQuery()).build(new Object[0]));
    }

    public final URI buildManageApplicationsUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/applications/versions-licenses").build(new Object[0]));
    }

    public final URI makeAbsoluteMpacUri(URI uri) {
        return this.makeAbsolute(URI.create(UpmSys.getMpacWebsiteBaseUrl()), uri);
    }

    public final URI buildProductVersionUri() {
        return this.newPluginBaseUriBuilder().path(ProductVersionResource.class).build(new Object[0]);
    }

    public final URI buildUpdateAllUri() {
        return this.newPluginBaseUriBuilder().path(UpdateAllResource.class).build(new Object[0]);
    }

    public final URI buildDisableAllIncompatibleUri() {
        return this.newPluginBaseUriBuilder().path(DisableAllIncompatibleResource.class).build(new Object[0]);
    }

    public URI buildProductUpdatesUri() {
        return this.newPluginBaseUriBuilder().path(ProductUpdatesResource.class).build(new Object[0]);
    }

    public URI buildMacPluginLicenseUri(String pluginKey, String type) {
        return URI.create(UpmSys.getMacBaseUrl() + "/addon/" + type + "/" + pluginKey);
    }

    public final String getDataCenterHamsKey(String pluginKey) {
        return pluginKey + DATA_CENTER_FOR_HAMS;
    }

    public URI buildMacPluginLicenseUri(Boolean isDataCenter, String pluginKey, String type) {
        return this.buildMacPluginLicenseUri(isDataCenter != false ? this.getDataCenterHamsKey(pluginKey) : pluginKey, type);
    }

    public URI buildGeoIpUri() {
        return UriBuilder.fromUri((String)UpmSys.getMpacBaseUrl()).path("rest/2/geoip").build(new Object[0]);
    }

    public URI buildMacSubscriptionPluginLicenseFallbackUri(String hostSen) {
        String sen = hostSen.startsWith("SEN-") ? hostSen.substring("SEN-".length()) : hostSen;
        return UriBuilder.fromPath((String)UpmSys.getMacBaseUrl()).path("ondemand").path("configure").path(sen).build(new Object[0]);
    }

    public URI buildProductUpdatePluginCompatibilityUri(int productUpdateBuildNumber) {
        return this.newPluginBaseUriBuilder().path(ProductUpdatePluginCompatibilityResource.class).build(new Object[]{productUpdateBuildNumber});
    }

    @Override
    public URI buildChangesRequiringRestartUri() {
        return this.newPluginBaseUriBuilder().path(ChangeRequiringRestartCollectionResource.class).build(new Object[0]);
    }

    public URI buildChangesRequiringRestartUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(ChangeRequiringRestartResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public URI buildSetTestLicenseUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(SetTestLicenseResource.class).path(pluginKey).build(new Object[0]);
    }

    public URI buildUpdateCheckScheduledJobUri() {
        return this.newPluginBaseUriBuilder().path(ScheduledJobResource.class).path("updates").build(new Object[0]);
    }

    public URI buildTestScheduledJobUri() {
        return this.newPluginBaseUriBuilder().path(RunScheduledJobResource.class).build(new Object[0]);
    }

    public URI buildTestBundleIdUri() {
        return this.newPluginBaseUriBuilder().path(BundleIdResource.class).build(new Object[0]);
    }

    public URI buildUpmAuditLogUri(String source) {
        UriBuilder path = this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/log");
        if (!source.equals("")) {
            return path.queryParam("source", new Object[]{source}).build(new Object[0]);
        }
        return path.build(new Object[0]);
    }

    public URI buildUpmUpdateCheckUri(String source) {
        UriBuilder path = this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm/check");
        if (!source.equals("")) {
            return path.queryParam("source", new Object[]{source}).build(new Object[0]);
        }
        return path.build(new Object[0]);
    }

    public URI buildManageMpacAddOn(String pluginKey) {
        return this.buildManageMpacAddOn(pluginKey, Option.none(String.class));
    }

    private URI buildManageMpacAddOn(String pluginKey, Option<String> tabPath) {
        UriBuilder buidler = UriBuilder.fromUri((String)UpmSys.getMpacBaseUrl()).path("/manage/plugins/{pluginKey}");
        for (String tab : tabPath) {
            buidler.path(tab);
        }
        return buidler.build(new Object[]{pluginKey});
    }

    private UriBuilder getMacBillingApiUriBuilder() {
        return UriBuilder.fromPath((String)this.makeAbsolute(URI.create(UpmSys.getMacBillingUrl())).getPath());
    }

    private UriBuilder addStartIndex(UriBuilder uriBuilder, Integer startIndex) {
        return this.addQueryParamIfNotNull(uriBuilder, "start-index", startIndex);
    }

    private UriBuilder addMaxResults(UriBuilder uriBuilder, Integer max) {
        return this.addQueryParamIfNotNull(uriBuilder, "max-results", max);
    }

    private UriBuilder addExcludeUserRequests(UriBuilder uriBuilder, Boolean excludeUserRequests) {
        return this.addQueryParamIfNotNull(uriBuilder, "exclude-user-requests", excludeUserRequests);
    }

    private UriBuilder addSearchQuery(UriBuilder uriBuilder, Option<String> query) {
        return this.addOptionalParameter(uriBuilder, query, "q");
    }

    private UriBuilder addCategory(UriBuilder uriBuilder, Option<String> category) {
        return this.addOptionalParameter(uriBuilder, category, "category");
    }

    private UriBuilder addOptionalParameter(UriBuilder uriBuilder, Option<String> val, String key) {
        Iterator<String> iterator = val.iterator();
        if (iterator.hasNext()) {
            String v = iterator.next();
            return uriBuilder.queryParam(key, new Object[]{v});
        }
        return uriBuilder;
    }

    private UriBuilder addOffset(UriBuilder uriBuilder, int offset) {
        return offset <= 0 ? uriBuilder : uriBuilder.queryParam("offset", new Object[]{offset});
    }

    private UriBuilder addQueryParamIfNotNull(UriBuilder uriBuilder, String name, Object value) {
        if (value != null) {
            return uriBuilder.queryParam(name, new Object[]{value});
        }
        return uriBuilder;
    }
}

