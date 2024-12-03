/*
 * Decompiled with CFR 0.152.
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
import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.jwt.UpmJwtToken;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.Service;
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
import com.atlassian.upm.pac.PluginVersionPair;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.rest.representations.PluginRequestCollectionRepresentation;
import com.atlassian.upm.request.rest.representations.PluginRequestRepresentation;
import com.atlassian.upm.rest.UpmMarketplaceFilter;
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
import com.atlassian.upm.rest.representations.UpmSettingsCollectionRepresentation;
import com.atlassian.upm.rest.representations.UserSettingsRepresentation;
import com.atlassian.upm.rest.resources.PacStatusResource;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface UpmRepresentationFactory
extends BasePluginRepresentationFactory {
    public InstalledMarketplacePluginCollectionRepresentation createInstalledMarketplacePluginCollectionRepresentation(Locale var1, List<Plugin> var2, Iterable<AvailableAddonWithVersion> var3, Iterable<IncompatiblePluginData> var4, RequestContext var5, String var6);

    public InstalledMarketplacePluginRepresentation createInstalledMarketplacePluginRepresentation(Plugin var1, Option<AvailableAddonWithVersion> var2, Option<IncompatiblePluginData> var3);

    public PacDetailsRepresentation createPacDetailsRepresentation(Plugin var1, Option<PluginVersionPair> var2);

    public AvailablePluginCollectionRepresentation.AvailablePluginEntry createAvailablePluginEntry(AddonBase var1, AddonVersionBase var2);

    public AvailablePluginCollectionRepresentation.RequestedPluginEntry createRequestedPluginEntry(AddonBase var1, AddonVersionBase var2, Collection<PluginRequest> var3);

    public PurchasedPluginCollectionRepresentation.UnknownPluginEntry createUnknownPluginEntry(String var1);

    public AvailablePluginCollectionRepresentation createAvailablePluginCollectionRepresentation(Iterable<AvailableAddonWithVersionBase> var1, Page<AddonSummary> var2, Map<String, PluginRequest> var3, RequestContext var4, UpmMarketplaceFilter var5, Option<String> var6);

    public AvailablePluginRepresentation createAvailablePluginRepresentation(Addon var1, AddonVersion var2);

    public ProductUpdatesRepresentation createProductUpdatesRepresentation(Collection<ApplicationVersion> var1, RequestContext var2);

    public ProductVersionRepresentation createProductVersionRepresentation(boolean var1, boolean var2);

    public ProductUpdatePluginCompatibilityRepresentation createProductUpdatePluginCompatibilityRepresentation(ProductUpdatePluginCompatibility var1, int var2, Locale var3);

    public CollectionRepresentation<BundleSummaryRepresentation> createOsgiBundleCollectionRepresentation();

    public CollectionRepresentation<BundleSummaryRepresentation> createOsgiBundleCollectionRepresentation(String var1);

    public BundleRepresentation createOsgiBundleRepresentation(Bundle var1);

    public CollectionRepresentation<ServiceSummaryRepresentation> createOsgiServiceCollectionRepresentation();

    public ServiceRepresentation createOsgiServiceRepresentation(Service var1);

    public CollectionRepresentation<PackageSummaryRepresentation> createOsgiPackageCollectionRepresentation();

    public PackageRepresentation createOsgiPackageRepresentation(Package var1);

    public PacStatusResource.PacStatusRepresentation createPacStatusRepresentation(boolean var1, boolean var2);

    public PluginLicenseRepresentation createPluginLicenseRepresentation(String var1, Option<Plugin> var2, Option<PluginLicense> var3);

    public HostStatusRepresentation createHostStatusRepresentation(RequestContext var1);

    public HostLicenseDetailsRepresentation createHostLicenseRepresentation();

    public PluginRequestRepresentation createPluginRequestRepresentation(PluginRequest var1);

    public PluginRequestCollectionRepresentation createPluginRequestCollectionRepresentation(Map<String, Collection<PluginRequest>> var1);

    public PluginRequestCollectionRepresentation createAnonymousPluginRequestCollectionRepresentation(Map<String, Collection<PluginRequest>> var1);

    public BannerCollectionRepresentation createBannerCollectionRepresentation(Page<AddonReference> var1);

    public RecommendedPluginCollectionRepresentation createRecommendedPluginCollectionRepresentation(Iterable<AddonReference> var1, String var2);

    public PurchasedPluginCollectionRepresentation createPurchasedPluginCollectionRepresentation(Locale var1, Collection<AvailableAddonWithVersionBase> var2, Collection<AvailableAddonWithVersionBase> var3, Collection<String> var4, RequestContext var5);

    public UpmSettingsCollectionRepresentation createUpmSettingsCollectionRepresentation();

    public UserSettingsRepresentation createUserSettingsRepresentation(boolean var1);

    public VendorRepresentation createVendorRepresentation(VendorSummary var1);

    public SafeModeErrorReenablingPluginRepresentation createSafeModeErrorReenablingPluginRepresentation(Plugin var1, boolean var2);

    public SafeModeErrorReenablingPluginModuleRepresentation createSafeModeErrorReenablingPluginModuleRepresentation(Plugin.Module var1, boolean var2);

    public PluginPermissionRepresentation createPluginPermissionRepresentation(ConnectScope var1);

    public JwtTokenRepresentation createJwtTokenRepresentation(UpmJwtToken var1);
}

