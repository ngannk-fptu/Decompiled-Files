/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.impl;

import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.upm.Optionals;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.PluginPrimaryAction;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.PluginLicensesInternal;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginServiceUtil;
import com.atlassian.upm.pac.IncompatiblePluginData;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class LicensedPlugins {
    private static final List<String> CLOUD_READY_PRODUCTS = Arrays.asList("bitbucket", "conf", "jira", "bamboo", "crowd");

    public static Optional<PluginPrimaryAction> getPrimaryPluginActionRequired(PermissionEnforcer permissionEnforcer, PluginUpdateRequestStore pluginUpdateRequestStore, RoleBasedLicensingPluginService roleBasedLicensingService, Plugin plugin, Option<AddonVersion> availableUpdate, Option<IncompatiblePluginData> incompatible, Option<PluginLicense> pluginLicense, UpmHostApplicationInformation hostInfo, HostLicenseInformation hostLicenseInformation, LicensingUsageVerifier licensingUsageVerifier, ApplicationPluginsManager applicationPluginsManager) {
        if (applicationPluginsManager.getApplicationRelatedPluginKeys().contains(plugin.getKey())) {
            return Optional.empty();
        }
        return Optionals.or(LicensedPlugins.checkForIncompatibleActions(plugin, incompatible, availableUpdate, permissionEnforcer, licensingUsageVerifier, pluginUpdateRequestStore, hostInfo), LicensedPlugins.checkForWrongAppTypeAction(plugin, availableUpdate, permissionEnforcer, hostInfo), LicensedPlugins.checkForLicensingActions(plugin, permissionEnforcer, pluginLicense, roleBasedLicensingService, hostLicenseInformation, licensingUsageVerifier), LicensedPlugins.checkForUpdateActions(plugin, availableUpdate, permissionEnforcer, licensingUsageVerifier), LicensedPlugins.checkForUpdateRequestedActions(plugin, incompatible, pluginUpdateRequestStore));
    }

    private static Optional<PluginPrimaryAction> checkForWrongAppTypeAction(Plugin plugin, Option<AddonVersion> availableUpdate, PermissionEnforcer permissionEnforcer, UpmHostApplicationInformation hostInfo) {
        boolean isWrongAppType = LicensedPlugins.isWrongAppType(plugin, hostInfo);
        boolean isUpdatable = LicensedPlugins.isUpdateable(availableUpdate, permissionEnforcer);
        if (isWrongAppType && isUpdatable) {
            return Optional.of(PluginPrimaryAction.WRONG_APP_TYPE_WITH_UPDATE);
        }
        if (isWrongAppType) {
            return Optional.of(PluginPrimaryAction.WRONG_APP_TYPE);
        }
        return Optional.empty();
    }

    private static boolean isWrongAppType(Plugin plugin, UpmHostApplicationInformation hostInfo) {
        return hostInfo.isHostDataCenterEnabled() ? PluginInfoUtils.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin.getPluginInformation()) && PluginInfoUtils.isServerApp(plugin.getPluginInformation()) : PluginInfoUtils.isDataCenterApp(plugin.getPluginInformation());
    }

    private static boolean isUpdateable(Option<AddonVersion> availableUpdate, PermissionEnforcer permissionEnforcer) {
        for (AddonVersion update : availableUpdate) {
            for (URI binaryUri : update.getArtifactUri()) {
                if (!permissionEnforcer.hasInProcessInstallationFromUriPermission(binaryUri)) continue;
                return update.isDeployable();
            }
        }
        return false;
    }

    private static Optional<PluginPrimaryAction> checkForIncompatibleActions(Plugin plugin, Option<IncompatiblePluginData> incompatible, Option<AddonVersion> availableUpdate, PermissionEnforcer permissionEnforcer, LicensingUsageVerifier licensingUsageVerifier, PluginUpdateRequestStore pluginUpdateRequestStore, UpmHostApplicationInformation hostInfo) {
        if (Sys.isIncompatiblePluginCheckEnabled() && !plugin.isUpmPlugin()) {
            for (IncompatiblePluginData incompatiblePlugin : incompatible) {
                for (AddonVersion update : availableUpdate) {
                    for (URI binaryUri : update.getArtifactUri()) {
                        if (!permissionEnforcer.hasInProcessInstallationFromUriPermission(binaryUri)) continue;
                        return Optional.of(LicensedPlugins.getIncompatibleActionWhenUpdateAvailable(plugin, update, licensingUsageVerifier, hostInfo));
                    }
                }
                if (IncompatiblePluginData.IncompatibilityType.APPLICATION == incompatiblePlugin.getIncompatibilityType()) {
                    return Optional.of(PluginPrimaryAction.INCOMPATIBLE_WITH_HOST_APPLICATION);
                }
                if (pluginUpdateRequestStore.isPluginUpdateRequested(plugin)) continue;
                if (IncompatiblePluginData.IncompatibilityType.DATA_CENTER == incompatiblePlugin.getIncompatibilityType()) {
                    return Optional.of(PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE);
                }
                if (IncompatiblePluginData.IncompatibilityType.LEGACY_DATA_CENTER == incompatiblePlugin.getIncompatibilityType()) {
                    return Optional.of(PluginPrimaryAction.INCOMPATIBLE_LEGACY_DATA_CENTER_COMPATIBLE);
                }
                if (!permissionEnforcer.hasPermission(Permission.REQUEST_PLUGIN_UPDATE)) continue;
                return Optional.of(PluginPrimaryAction.INCOMPATIBLE_WITHOUT_UPDATE);
            }
        }
        return Optional.empty();
    }

    private static PluginPrimaryAction getIncompatibleActionWhenUpdateAvailable(Plugin plugin, AddonVersion update, LicensingUsageVerifier licensingUsageVerifier, UpmHostApplicationInformation hostInfo) {
        if (hostInfo.isHostDataCenterEnabled()) {
            if (update.isDataCenterStatusCompatible()) {
                return LicensedPlugins.isFreeUpdatableToPaid(plugin, Option.some(update), licensingUsageVerifier) ? PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_WITH_PAID_UPDATE : PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_WITH_UPDATE;
            }
            return PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE;
        }
        return LicensedPlugins.isFreeUpdatableToPaid(plugin, Option.some(update), licensingUsageVerifier) ? PluginPrimaryAction.INCOMPATIBLE_WITH_PAID_UPDATE : PluginPrimaryAction.INCOMPATIBLE_WITH_UPDATE;
    }

    private static Optional<PluginPrimaryAction> checkForLicensingActions(Plugin plugin, PermissionEnforcer permissionEnforcer, Option<PluginLicense> pluginLicense, RoleBasedLicensingPluginService roleBasedLicensingService, HostLicenseInformation hostLicenseInformation, LicensingUsageVerifier licensingUsageVerifier) {
        if (com.atlassian.upm.license.LicensedPlugins.usesLicensing(plugin.getPlugin(), licensingUsageVerifier) && permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE)) {
            if (PluginLicenses.isPluginUpgradeRequired(pluginLicense)) {
                return Optional.of(PluginPrimaryAction.UPGRADABLE);
            }
            for (PluginLicense license : pluginLicense) {
                if (PluginLicenses.isPluginCrossgradeable(Option.some(plugin.getPluginInformation()), hostLicenseInformation, Option.some(license)) && !license.isValidForDc()) {
                    if (hostLicenseInformation.isEvaluation()) {
                        return Optional.of(PluginPrimaryAction.LICENSE_FUTURE_INCOMPATIBLE);
                    }
                    return Optional.of(PluginPrimaryAction.LICENSE_INCOMPATIBLE);
                }
                if (PluginLicensesInternal.isRecentlyExpiredEvaluation(Option.some(plugin)).test(license)) {
                    return Optional.of(PluginPrimaryAction.EVAL_RECENTLY_EXPIRED);
                }
                if (PluginLicensesInternal.isDataCenterLicenseRecentlyExpired(Option.some(plugin)).test(license)) {
                    return Optional.of(PluginPrimaryAction.LICENSE_RECENTLY_EXPIRED);
                }
                if (PluginLicensesInternal.isMaintenanceRecentlyExpired(Option.some(plugin)).test(license)) {
                    return Optional.of(PluginPrimaryAction.MAINTENANCE_RECENTLY_EXPIRED);
                }
                if (PluginLicenses.isPluginUpgradeNearlyRequired(Option.some(license), RoleBasedLicensingPluginServiceUtil.getRoleCount(roleBasedLicensingService, Option.some(plugin.getPlugin()), Option.some(license)))) {
                    return Optional.of(PluginPrimaryAction.UPGRADE_NEARLY_REQUIRED);
                }
                if (PluginLicensesInternal.isNearlyExpiredEvaluation(Option.some(plugin)).test(license)) {
                    return Optional.of(PluginPrimaryAction.EVAL_NEARLY_EXPIRED);
                }
                if (PluginLicensesInternal.isDataCenterLicenseNearlyExpired(Option.some(plugin)).test(license)) {
                    return Optional.of(PluginPrimaryAction.LICENSE_NEARLY_EXPIRING);
                }
                if (!PluginLicensesInternal.isMaintenanceNearlyExpired(Option.some(plugin)).test(license)) continue;
                return Optional.of(PluginPrimaryAction.MAINTENANCE_NEARLY_EXPIRING);
            }
        }
        return Optional.empty();
    }

    private static Optional<PluginPrimaryAction> checkForUpdateActions(Plugin plugin, Option<AddonVersion> availableUpdate, PermissionEnforcer permissionEnforcer, LicensingUsageVerifier licensingUsageVerifier) {
        for (AddonVersion update : availableUpdate) {
            for (URI binaryUri : update.getArtifactUri()) {
                if (!permissionEnforcer.hasInProcessInstallationFromUriPermission(binaryUri)) continue;
                return Optional.of(LicensedPlugins.getUpdatablePrimaryAction(plugin, availableUpdate, false, licensingUsageVerifier));
            }
        }
        return Optional.empty();
    }

    private static Optional<PluginPrimaryAction> checkForUpdateRequestedActions(Plugin plugin, Option<IncompatiblePluginData> incompatible, PluginUpdateRequestStore pluginUpdateRequestStore) {
        Iterator<IncompatiblePluginData> iterator;
        if (Sys.isIncompatiblePluginCheckEnabled() && !plugin.isUpmPlugin() && (iterator = incompatible.iterator()).hasNext()) {
            IncompatiblePluginData incompatiblePlugin = iterator.next();
            if (pluginUpdateRequestStore.isPluginUpdateRequested(plugin)) {
                return incompatiblePlugin.isDataCenter() ? Optional.of(PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_REQUESTED_UPDATE) : Optional.of(PluginPrimaryAction.INCOMPATIBLE_REQUESTED_UPDATE);
            }
            return incompatiblePlugin.isDataCenter() ? Optional.of(PluginPrimaryAction.INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE) : Optional.of(PluginPrimaryAction.INCOMPATIBLE_WITHOUT_UPDATE);
        }
        return Optional.empty();
    }

    private static PluginPrimaryAction getUpdatablePrimaryAction(Plugin plugin, Option<AddonVersion> availableUpdate, boolean remoteInstallable, LicensingUsageVerifier licensingUsageVerifier) {
        for (AddonVersion update : availableUpdate) {
            if (update.isDeployable() || remoteInstallable) continue;
            return PluginPrimaryAction.UPDATABLE_NONDEPLOYABLE;
        }
        return LicensedPlugins.isFreeUpdatableToPaid(plugin, availableUpdate, licensingUsageVerifier) ? PluginPrimaryAction.UPDATABLE_TO_PAID : PluginPrimaryAction.UPDATABLE;
    }

    public static boolean isFreeUpdatableToPaid(Plugin currentlyInstalledPlugin, Option<AddonVersion> availableUpdate, LicensingUsageVerifier licensingUsageVerifier) {
        Iterator<AddonVersion> iterator = availableUpdate.iterator();
        if (iterator.hasNext()) {
            AddonVersion update = iterator.next();
            return !com.atlassian.upm.license.LicensedPlugins.usesLicensing(currentlyInstalledPlugin.getPlugin(), licensingUsageVerifier) && update.getPaymentModel().equals(PaymentModel.PAID_VIA_ATLASSIAN);
        }
        return false;
    }

    public static boolean isServerWithCloudAlternative(HostingType hostingType, String platformId) {
        return HostingType.SERVER.equals((Object)hostingType) && CLOUD_READY_PRODUCTS.contains(platformId);
    }
}

