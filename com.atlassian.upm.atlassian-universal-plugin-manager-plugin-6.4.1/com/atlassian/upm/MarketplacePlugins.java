/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Predicate
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.ReadablePartial
 */
package com.atlassian.upm;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.PricingType;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.marketplace.client.model.VendorSummary;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

public abstract class MarketplacePlugins {
    public static Boolean isInstallable(AddonVersionBase version, ApplicationProperties applicationProperties) {
        return version.isDeployable() && (!version.isStatic() || Objects.requireNonNull(applicationProperties, "applicationProperties").getDisplayName().equalsIgnoreCase(ApplicationKey.CONFLUENCE.getKey())) && version.getArtifactUri().isDefined();
    }

    public static String getMarketplaceTypeFromPaymentModel(PaymentModel paymentModel) {
        return paymentModel.name();
    }

    public static String getPluginNameAndVersion(AvailableAddonWithVersion update) {
        return update.getAddon().getName() + " " + (String)update.getVersion().getName().getOrElse((Object)"");
    }

    public static String getPluginNameAndVersion(Addon plugin) {
        Iterator iterator = plugin.getVersion().iterator();
        if (iterator.hasNext()) {
            AddonVersion version = (AddonVersion)iterator.next();
            return plugin.getName() + " " + (String)version.getName().getOrElse((Object)"");
        }
        return plugin.getName();
    }

    public static String getSupportTypeName(AddonBase addon, AddonVersionBase version) {
        if (!version.isSupported()) {
            return "unsupported";
        }
        for (VendorSummary vendor : addon.getVendor()) {
            if (!vendor.getName().contains("Atlassian")) continue;
            return "atlassian";
        }
        return "vendor";
    }

    public static boolean isLicensedToBeUpdated(AvailableAddonWithVersion plugin, PluginLicenseRepository licenseRepository, HostLicenseInformation hostLicenseInformation) {
        return new IsLicensedToBeUpdated(licenseRepository, hostLicenseInformation).test(plugin);
    }

    public static Option<DateTime> getExpectedDataCenterVersionLicenseExpiryDate(AvailableAddonWithVersion plugin, PluginLicenseRepository licenseRepository, HostLicenseInformation hostLicenseInformation) {
        if (hostLicenseInformation.isDataCenter()) {
            return Option.some(licenseRepository.getPluginLicense(plugin.getAddon().getKey()).flatMap(l -> l.isDataCenter() ? l.getExpiryDate() : l.getMaintenanceExpiryDate().map(med -> med.isAfter((ReadableInstant)PluginLicense.SERVER_LICENSE_CUTOFF_DATE) ? med : PluginLicense.SERVER_LICENSE_CUTOFF_DATE)).getOrElse(PluginLicense.SERVER_LICENSE_CUTOFF_DATE));
        }
        return Option.none();
    }

    public static boolean isDataCenterIncompatible(Plugin plugin, UpmHostApplicationInformation appInfo) {
        return appInfo.isHostDataCenterEnabled() ? plugin.isUserInstalled() && !Plugins.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin) : false;
    }

    public static boolean isDataCenterIncompatible(AddonVersionBase version, UpmHostApplicationInformation appInfo) {
        return appInfo.isHostDataCenterEnabled() && !version.isDataCenterStatusCompatible();
    }

    public static PricingType getPricingType(AddonVersionBase version) {
        return version.isDataCenterStatusCompatible() ? PricingType.DATA_CENTER : PricingType.SERVER;
    }

    public static boolean isLegacyDataCenterIncompatible(Plugin plugin, UpmHostApplicationInformation appInfo) {
        if (appInfo.isHostDataCenterEnabled()) {
            return plugin.isUserInstalled() && !Plugins.isLegacyDataCenterCompatibleAccordingToPluginDescriptor(plugin);
        }
        return false;
    }

    public static boolean isLegacyDataCenterIncompatible(AddonVersionBase version, UpmHostApplicationInformation appInfo) {
        if (appInfo.isHostDataCenterEnabled()) {
            return !version.isDataCenterCompatible();
        }
        return false;
    }

    private static class IsLicensedToBeUpdated
    implements Predicate<AvailableAddonWithVersion> {
        private final PluginLicenseRepository licenseRepository;
        private final HostLicenseInformation hostLicenseInformation;

        public IsLicensedToBeUpdated(PluginLicenseRepository licenseRepository, HostLicenseInformation hostLicenseInformation) {
            this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
            this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        }

        @Override
        public boolean test(AvailableAddonWithVersion pv) {
            for (PluginLicense pluginLicense : this.licenseRepository.getPluginLicense(pv.getAddon().getKey())) {
                if (!pluginLicense.isValid()) {
                    return true;
                }
                if (pluginLicense.getMaintenanceExpiryDate().exists((com.google.common.base.Predicate<DateTime>)((com.google.common.base.Predicate)med -> pv.getVersion().getReleaseDate().isAfter((ReadablePartial)med.toLocalDate())))) {
                    return false;
                }
                if (!this.hostLicenseInformation.isDataCenter() || pluginLicense.isDataCenter() || !pv.getVersion().isDataCenterStatusCompatible()) continue;
                return pluginLicense.isValidForDc();
            }
            return true;
        }
    }
}

