/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.license.entity.LicenseError
 *  com.atlassian.upm.api.license.entity.LicenseType
 *  com.atlassian.upm.api.license.entity.PluginLicense
 *  com.atlassian.upm.api.util.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.joda.time.Days
 *  org.joda.time.Duration
 *  org.joda.time.Interval
 *  org.joda.time.ReadableDuration
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.license;

import com.atlassian.upm.SysCommon;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;

public class PluginLicenses {
    public static final Integer NEARLY_EXPIRED_DAYS = 7;
    public static final Integer RECENTLY_EXPIRED_DAYS = 7;
    private static final Function<PluginLicense, String> licensePluginKey = new Function<PluginLicense, String>(){

        public String apply(PluginLicense from) {
            return from.getPluginKey();
        }
    };
    private static final Function<PluginLicense, DateTime> licenseCreationDate = new Function<PluginLicense, DateTime>(){

        public DateTime apply(PluginLicense from) {
            return from.getCreationDate();
        }
    };

    public static Predicate<PluginLicense> hasError(final LicenseError error) {
        return new Predicate<PluginLicense>(){

            public boolean apply(PluginLicense license) {
                return error == license.getError().getOrElse((Object)null);
            }
        };
    }

    public static Predicate<PluginLicense> isNearlyExpired() {
        return new Predicate<PluginLicense>(){

            public boolean apply(PluginLicense license) {
                Iterator i$ = license.getExpiryDate().iterator();
                if (i$.hasNext()) {
                    DateTime expiryDate = (DateTime)i$.next();
                    return new Interval((ReadableInstant)expiryDate.minusDays(NEARLY_EXPIRED_DAYS.intValue()), (ReadableInstant)expiryDate).contains((ReadableInstant)new DateTime());
                }
                return false;
            }
        };
    }

    public static Predicate<PluginLicense> isNearlyMaintenanceExpired() {
        return new Predicate<PluginLicense>(){

            public boolean apply(PluginLicense license) {
                Iterator i$ = license.getMaintenanceExpiryDate().iterator();
                if (i$.hasNext()) {
                    DateTime maintenanceExpiryDate = (DateTime)i$.next();
                    return new Interval((ReadableInstant)maintenanceExpiryDate.minusDays(NEARLY_EXPIRED_DAYS.intValue()), (ReadableInstant)maintenanceExpiryDate).contains((ReadableInstant)new DateTime());
                }
                return false;
            }
        };
    }

    public static Predicate<PluginLicense> isRecentlyExpired() {
        return new IsRecentlyExpired(Duration.standardDays((long)RECENTLY_EXPIRED_DAYS.intValue()));
    }

    public static Predicate<PluginLicense> hasLicenseExpiredWithin(Duration duration) {
        return new IsRecentlyExpired(duration);
    }

    public static Predicate<PluginLicense> isRecentlyMaintenanceExpired() {
        return new IsRecentlyMaintenanceExpired(Duration.standardDays((long)RECENTLY_EXPIRED_DAYS.intValue()));
    }

    public static Predicate<PluginLicense> hasMaintenanceExpiredWithin(Duration duration) {
        return new IsRecentlyMaintenanceExpired(duration);
    }

    public static Predicate<PluginLicense> isEvaluation() {
        return new Predicate<PluginLicense>(){

            public boolean apply(PluginLicense license) {
                return license.isEvaluation();
            }
        };
    }

    public static Predicate<PluginLicense> isEmbeddedWithinHostLicense() {
        return new Predicate<PluginLicense>(){

            public boolean apply(PluginLicense license) {
                return license.isEmbeddedWithinHostLicense();
            }
        };
    }

    public static Option<Days> getDaysSinceMaintenanceExpiry(PluginLicense pluginLicense) {
        Iterator i$ = pluginLicense.getMaintenanceExpiryDate().iterator();
        if (i$.hasNext()) {
            DateTime maintenanceExpiryDate = (DateTime)i$.next();
            if (!pluginLicense.isMaintenanceExpired()) {
                return Option.none(Days.class);
            }
            return Option.some((Object)Days.daysBetween((ReadableInstant)maintenanceExpiryDate, (ReadableInstant)new DateTime()));
        }
        return Option.none(Days.class);
    }

    public static boolean isPluginBuyable(Option<PluginLicense> pluginLicense) {
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return registeredLicense.isEvaluation() || PluginLicenses.isErrorEqual((Option<LicenseError>)registeredLicense.getError(), LicenseError.TYPE_MISMATCH);
        }
        return true;
    }

    public static boolean isPluginTryable(Option<PluginLicense> pluginLicense) {
        return !pluginLicense.isDefined();
    }

    public static boolean isPluginRenewable(Option<PluginLicense> pluginLicense) {
        if (PluginLicenses.isPluginUpgradable(pluginLicense)) {
            return false;
        }
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            boolean nearlyExpired = PluginLicenses.isNearlyExpired().apply((Object)registeredLicense);
            boolean nearlyMaintenanceExpired = PluginLicenses.isNearlyMaintenanceExpired().apply((Object)registeredLicense);
            boolean maintenanceExpired = registeredLicense.isMaintenanceExpired();
            boolean hasAppropriateError = registeredLicense.getError().isDefined() && ImmutableSet.of((Object[])new LicenseError[]{LicenseError.EXPIRED, LicenseError.VERSION_MISMATCH}).contains(registeredLicense.getError().get());
            boolean hasAppropriateType = ImmutableSet.of((Object[])new LicenseType[]{LicenseType.ACADEMIC, LicenseType.COMMERCIAL, LicenseType.STARTER}).contains((Object)registeredLicense.getLicenseType());
            boolean evaluation = registeredLicense.isEvaluation();
            return (nearlyExpired || nearlyMaintenanceExpired || maintenanceExpired || hasAppropriateError) && hasAppropriateType && !evaluation;
        }
        return false;
    }

    public static boolean isPluginUpgradable(Option<PluginLicense> pluginLicense) {
        if (PluginLicenses.isPluginBuyable(pluginLicense)) {
            return false;
        }
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return PluginLicenses.isErrorEqual((Option<LicenseError>)registeredLicense.getError(), LicenseError.USER_MISMATCH) || PluginLicenses.isErrorEqual((Option<LicenseError>)registeredLicense.getError(), LicenseError.EDITION_MISMATCH);
        }
        return false;
    }

    public static Function<PluginLicense, String> licensePluginKey() {
        return licensePluginKey;
    }

    public static Function<PluginLicense, DateTime> licenseCreationDate() {
        return licenseCreationDate;
    }

    private static boolean isErrorEqual(Option<LicenseError> possibleError, LicenseError equalTo) {
        Iterator i$ = possibleError.iterator();
        if (i$.hasNext()) {
            LicenseError error = (LicenseError)i$.next();
            return equalTo.equals((Object)error);
        }
        return false;
    }

    public static boolean isPluginTrialSubscriptionStartable(Option<PluginLicense> pluginLicense) {
        return !pluginLicense.isDefined();
    }

    public static boolean isPluginTrialSubscriptionResumable(Option<PluginLicense> pluginLicense) {
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return registeredLicense.isSubscription() && !registeredLicense.isActive() && registeredLicense.isEvaluation() && (Boolean)registeredLicense.getSubscriptionEndDate().map((Function)new Function<DateTime, Boolean>(){

                public Boolean apply(DateTime subscriptionEndDate) {
                    return subscriptionEndDate.isAfterNow();
                }
            }).getOrElse((Object)false) != false;
        }
        return false;
    }

    public static boolean isPluginTrialSubscriptionCancellable(Option<PluginLicense> pluginLicense) {
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return registeredLicense.isSubscription() && registeredLicense.isActive() && registeredLicense.isEvaluation();
        }
        return false;
    }

    public static boolean isPluginSubscribable(Option<PluginLicense> pluginLicense) {
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return registeredLicense.isSubscription() && !registeredLicense.isEvaluation() && !registeredLicense.isAutoRenewal();
        }
        return false;
    }

    public static boolean isPluginUnsubscribable(Option<PluginLicense> pluginLicense) {
        if (PluginLicenses.isPluginSubscribable(pluginLicense) || PluginLicenses.isPluginTrialSubscriptionCancellable(pluginLicense)) {
            return false;
        }
        Iterator i$ = pluginLicense.iterator();
        if (i$.hasNext()) {
            PluginLicense registeredLicense = (PluginLicense)i$.next();
            return registeredLicense.isSubscription() && registeredLicense.isActive() && !registeredLicense.isEvaluation() && registeredLicense.isAutoRenewal();
        }
        return false;
    }

    public static boolean isPluginEligibleForAtlassianLicensing(String pluginKey) {
        return SysCommon.isOnDemand() ? !Iterables.contains(SysCommon.getOnDemandPaidViaAtlassianBlacklist(), (Object)pluginKey) : true;
    }

    private static class IsRecentlyMaintenanceExpired
    implements Predicate<PluginLicense> {
        private final DateTime dateAgo;

        public IsRecentlyMaintenanceExpired(Duration duration) {
            this.dateAgo = new DateTime().minus((ReadableDuration)duration);
        }

        public boolean apply(PluginLicense license) {
            Iterator i$ = license.getMaintenanceExpiryDate().iterator();
            if (i$.hasNext()) {
                DateTime maintenanceExpiryDate = (DateTime)i$.next();
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

        public boolean apply(PluginLicense license) {
            Iterator i$ = license.getExpiryDate().iterator();
            if (i$.hasNext()) {
                DateTime expiryDate = (DateTime)i$.next();
                return expiryDate.isAfter((ReadableInstant)this.dateAgo) && expiryDate.isBefore((ReadableInstant)new DateTime());
            }
            return false;
        }
    }
}

