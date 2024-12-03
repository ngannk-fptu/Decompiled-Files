/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginInformation
 *  com.google.common.base.Predicate
 *  org.joda.time.DateTime
 *  org.joda.time.Days
 *  org.joda.time.Duration
 *  org.joda.time.ReadableDuration
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.license;

import com.atlassian.plugin.PluginInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.LicensedAttributes;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;

public class PluginLicenses {
    private static final Function<PluginLicense, String> licensePluginKey = PluginLicense::getPluginKey;
    private static final Function<PluginLicense, DateTime> licenseCreationDate = PluginLicense::getCreationDate;
    public static Function<PluginLicense, Boolean> getLicenseAutoRenewal = PluginLicense::isAutoRenewal;
    public static Function<PluginLicense, Option<Integer>> getLicenseEdition = PluginLicense::getEdition;
    public static Function<PluginLicense, LicenseEditionType> getLicenseEditionType = PluginLicense::getEditionType;

    public static Predicate<PluginLicense> hasError(LicenseError error) {
        return license -> error == license.getError().getOrElse((LicenseError)null);
    }

    public static Predicate<PluginLicense> isNearlyExpired() {
        return license -> LicensedAttributes.isNearlyExpired().test(LicensedAttributes.LicenseAttributes.from(license));
    }

    public static Predicate<PluginLicense> isNearlyMaintenanceExpired() {
        return license -> LicensedAttributes.isNearlyMaintenanceExpired().test(LicensedAttributes.LicenseAttributes.from(license));
    }

    public static boolean isRoleNearlyExceeded(Option<PluginLicense> pluginLicense, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isRoleNearlyExceeded(LicensedAttributes.LicenseAttributes.from(pluginLicense), currentRoleCount);
    }

    public static Predicate<PluginLicense> isRecentlyExpired() {
        return new IsRecentlyExpired(Duration.standardDays((long)LicensedAttributes.RECENTLY_EXPIRED_DAYS.intValue()));
    }

    public static Predicate<PluginLicense> isRecentlyMaintenanceExpired() {
        return new IsRecentlyMaintenanceExpired(Duration.standardDays((long)LicensedAttributes.RECENTLY_EXPIRED_DAYS.intValue()));
    }

    public static Predicate<PluginLicense> isEvaluation() {
        return PluginLicense::isEvaluation;
    }

    public static Predicate<PluginLicense> isEmbeddedWithinHostLicense() {
        return PluginLicense::isEmbeddedWithinHostLicense;
    }

    public static Option<Days> getDaysSinceMaintenanceExpiry(PluginLicense pluginLicense) {
        Iterator<DateTime> iterator = pluginLicense.getMaintenanceExpiryDate().iterator();
        if (iterator.hasNext()) {
            DateTime maintenanceExpiryDate = iterator.next();
            if (!pluginLicense.isMaintenanceExpired()) {
                return Option.none(Days.class);
            }
            return Option.some(Days.daysBetween((ReadableInstant)maintenanceExpiryDate, (ReadableInstant)new DateTime()));
        }
        return Option.none(Days.class);
    }

    public static boolean isPluginBuyable(Option<PluginLicense> pluginLicense, boolean carebearSpecific) {
        return LicensedAttributes.isProductBuyable(LicensedAttributes.LicenseAttributes.from(pluginLicense)) && !carebearSpecific;
    }

    public static boolean isPluginTryable(Option<PluginLicense> pluginLicense, boolean carebearSpecific) {
        return LicensedAttributes.isProductTryable(LicensedAttributes.LicenseAttributes.from(pluginLicense)) && !carebearSpecific;
    }

    public static boolean isPluginRenewable(Option<PluginLicense> pluginLicense, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isProductRenewable(LicensedAttributes.LicenseAttributes.from(pluginLicense), currentRoleCount);
    }

    public static boolean isPluginCrossgradeable(Option<PluginInformation> pluginInformation, HostLicenseInformation hostLicenseInformation, Option<PluginLicense> pluginLicense) {
        return LicensedAttributes.isProductCrossgradeable(pluginInformation, hostLicenseInformation, LicensedAttributes.LicenseAttributes.from(pluginLicense));
    }

    public static boolean isCrossgradableAppAndHostIsTrialDcAndAppWillFailOnFullDc(Option<PluginInformation> pluginInformation, HostLicenseInformation hostLicenseInformation, Option<PluginLicense> pluginLicense) {
        return LicensedAttributes.isProductCrossgradeable(pluginInformation, hostLicenseInformation, LicensedAttributes.LicenseAttributes.from(pluginLicense)) && hostLicenseInformation.isDataCenter() && hostLicenseInformation.isEvaluation() && pluginLicense.exists((com.google.common.base.Predicate<PluginLicense>)((com.google.common.base.Predicate)l -> !l.isValidForDc()));
    }

    public static boolean isPluginRenewableRequiringContact(Option<PluginLicense> pluginLicense, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isProductRenewableRequiringContact(LicensedAttributes.LicenseAttributes.from(pluginLicense), currentRoleCount);
    }

    public static boolean isPluginUpgradable(Option<PluginLicense> pluginLicense, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isProductUpgradable(LicensedAttributes.LicenseAttributes.from(pluginLicense), currentRoleCount);
    }

    public static boolean isPluginUpgradeNearlyRequired(Option<PluginLicense> pluginLicense, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isProductUpgradeNearlyRequired(LicensedAttributes.LicenseAttributes.from(pluginLicense), currentRoleCount);
    }

    public static boolean isPluginUpgradeRequired(Option<PluginLicense> pluginLicense) {
        return LicensedAttributes.isProductUpgradeRequired(LicensedAttributes.LicenseAttributes.from(pluginLicense));
    }

    public static Function<PluginLicense, String> licensePluginKey() {
        return licensePluginKey;
    }

    public static Function<PluginLicense, DateTime> licenseCreationDate() {
        return licenseCreationDate;
    }

    private static class IsRecentlyMaintenanceExpired
    implements Predicate<PluginLicense> {
        private final DateTime dateAgo;

        public IsRecentlyMaintenanceExpired(Duration duration) {
            this.dateAgo = new DateTime().minus((ReadableDuration)duration);
        }

        @Override
        public boolean test(PluginLicense license) {
            Iterator<DateTime> iterator = license.getMaintenanceExpiryDate().iterator();
            if (iterator.hasNext()) {
                DateTime maintenanceExpiryDate = iterator.next();
                return maintenanceExpiryDate.isAfter((ReadableInstant)this.dateAgo) && maintenanceExpiryDate.isBefore((ReadableInstant)new DateTime());
            }
            return false;
        }
    }

    private static class IsRecentlyExpired
    implements Predicate<PluginLicense> {
        private final DateTime dateAgo;

        public IsRecentlyExpired(Duration duration) {
            this.dateAgo = new DateTime().minus((ReadableDuration)duration);
        }

        @Override
        public boolean test(PluginLicense license) {
            Iterator<DateTime> iterator = license.getExpiryDate().iterator();
            if (iterator.hasNext()) {
                DateTime expiryDate = iterator.next();
                return expiryDate.isAfter((ReadableInstant)this.dateAgo) && expiryDate.isBefore((ReadableInstant)new DateTime());
            }
            return false;
        }
    }
}

