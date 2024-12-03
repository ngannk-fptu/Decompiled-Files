/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.license.PluginLicenses;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public final class PluginLicensesInternal {
    private PluginLicensesInternal() {
    }

    public static Function<PluginLicense, Option<String>> licensePluginSen() {
        return PluginLicense::getSupportEntitlementNumber;
    }

    public static Predicate<PluginLicense> isEditionMismatch() {
        return PluginLicenses.hasError(LicenseError.USER_MISMATCH).or(PluginLicenses.hasError(LicenseError.EDITION_MISMATCH));
    }

    public static Predicate<PluginLicense> isRecentlyExpiredEvaluation(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return PluginLicensesInternal.isRecentExpiredEvaluationCoreLogic().and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isRecentlyExpiredEvaluation(Option<Plugin> plugin) {
        return PluginLicensesInternal.isRecentExpiredEvaluationCoreLogic().and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    private static Predicate<PluginLicense> isRecentExpiredEvaluationCoreLogic() {
        return PluginLicenses.hasError(LicenseError.EXPIRED).and(PluginLicenses.isRecentlyExpired()).and(PluginLicenses.isEvaluation());
    }

    public static Predicate<PluginLicense> isNearlyExpiredEvaluation(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return PluginLicensesInternal.isNearlyExpiredEvaluationCoreLogic().and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isNearlyExpiredEvaluation(Option<Plugin> plugin) {
        return PluginLicensesInternal.isNearlyExpiredEvaluationCoreLogic().and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    private static Predicate<PluginLicense> isNearlyExpiredEvaluationCoreLogic() {
        return PluginLicenses.isNearlyExpired().and(PluginLicenses.isEvaluation());
    }

    public static Predicate<PluginLicense> isMaintenanceRecentlyExpired(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return ((Predicate<PluginLicense>)PluginLicense::isDataCenter).negate().and(PluginLicensesInternal.isMaintenanceRecentlyExpiredCoreLogic()).and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isMaintenanceRecentlyExpired(Option<Plugin> plugin) {
        return ((Predicate<PluginLicense>)PluginLicense::isDataCenter).negate().and(PluginLicensesInternal.isMaintenanceRecentlyExpiredCoreLogic()).and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    private static Predicate<PluginLicense> isMaintenanceRecentlyExpiredCoreLogic() {
        return ((Predicate<PluginLicense>)PluginLicense::isDataCenter).negate().and(PluginLicenses.isRecentlyMaintenanceExpired()).and(PluginLicenses.isEvaluation().negate());
    }

    public static Predicate<PluginLicense> isMaintenanceNearlyExpired(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return ((Predicate<PluginLicense>)PluginLicense::isDataCenter).negate().and(PluginLicensesInternal.isMaintenanceNearlyExpired()).and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isMaintenanceNearlyExpired(Option<Plugin> plugin) {
        return ((Predicate<PluginLicense>)PluginLicense::isDataCenter).negate().and(PluginLicensesInternal.isMaintenanceNearlyExpired()).and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    private static Predicate<PluginLicense> isMaintenanceNearlyExpired() {
        return PluginLicenses.isNearlyMaintenanceExpired().and(PluginLicenses.isEvaluation().negate());
    }

    private static Predicate<PluginLicense> isLicenseNearlyExpired() {
        return PluginLicenses.isNearlyExpired().and(PluginLicenses.isEvaluation().negate());
    }

    private static Predicate<PluginLicense> isLicenseRecentlyExpired() {
        return PluginLicenses.isRecentlyExpired().and(PluginLicenses.isEvaluation().negate());
    }

    public static Predicate<PluginLicense> isDataCenterLicenseRecentlyExpired(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return PluginLicensesInternal.isLicenseRecentlyExpired().and(PluginLicense::isDataCenter).and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isDataCenterLicenseNearlyExpired(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return PluginLicensesInternal.isLicenseNearlyExpired().and(PluginLicense::isDataCenter).and(PluginLicensesInternal.isUninstalledLegacyPlugin(pluginRetriever, licensingUsageVerifier).negate());
    }

    public static Predicate<PluginLicense> isDataCenterLicenseRecentlyExpired(Option<Plugin> plugin) {
        return PluginLicensesInternal.isLicenseRecentlyExpired().and(PluginLicense::isDataCenter).and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    public static Predicate<PluginLicense> isDataCenterLicenseNearlyExpired(Option<Plugin> plugin) {
        return PluginLicensesInternal.isLicenseNearlyExpired().and(PluginLicense::isDataCenter).and(PluginLicensesInternal.isUninstalledLegacyPlugin(plugin).negate());
    }

    private static Predicate<PluginLicense> isUninstalledLegacyPlugin(PluginRetriever pluginRetriever, LicensingUsageVerifier licensingUsageVerifier) {
        return license -> {
            String pluginKey = license.getPluginKey();
            if (!LicensedPlugins.isLegacyLicensePlugin(pluginKey)) {
                return false;
            }
            Iterator<Plugin> iterator = pluginRetriever.getPlugin(pluginKey).iterator();
            if (iterator.hasNext()) {
                Plugin plugin = iterator.next();
                return !LicensedPlugins.usesLicensing(plugin.getPlugin(), licensingUsageVerifier);
            }
            return true;
        };
    }

    public static Predicate<PluginLicense> isUninstalledLegacyPlugin(Option<Plugin> plugin) {
        return license -> {
            String pluginKey = license.getPluginKey();
            if (!LicensedPlugins.isLegacyLicensePlugin(pluginKey)) {
                return false;
            }
            return !plugin.isDefined();
        };
    }
}

