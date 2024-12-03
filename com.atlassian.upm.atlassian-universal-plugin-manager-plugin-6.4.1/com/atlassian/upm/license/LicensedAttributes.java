/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginInformation
 *  com.google.common.base.Predicate
 *  org.joda.time.DateTime
 *  org.joda.time.Interval
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.license;

import com.atlassian.plugin.PluginInformation;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

public abstract class LicensedAttributes {
    public static final Integer NEARLY_EXPIRED_DAYS = 30;
    public static final Integer RECENTLY_EXPIRED_DAYS = 7;
    private static final Set<String> RENEWABLE_TYPES = Stream.of(LicenseType.ACADEMIC.name(), LicenseType.COMMERCIAL.name(), LicenseType.COMMUNITY.name(), LicenseType.OPEN_SOURCE.name(), LicenseType.STARTER.name()).collect(Collectors.toSet());

    public static boolean isProductBuyable(Option<LicenseAttributes> license) {
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            return registeredLicense.evaluation || LicensedAttributes.isErrorEqual(registeredLicense.licenseError, LicenseError.TYPE_MISMATCH.name());
        }
        return true;
    }

    public static boolean isProductTryable(Option<LicenseAttributes> license) {
        return !license.isDefined();
    }

    public static boolean isProductCrossgradeable(Option<PluginInformation> pluginInformation, HostLicenseInformation hostLicenseInformation, Option<LicenseAttributes> license) {
        return hostLicenseInformation.isDataCenter() && pluginInformation.exists((com.google.common.base.Predicate<PluginInformation>)((com.google.common.base.Predicate)PluginInfoUtils::isStatusDataCenterCompatibleAccordingToPluginDescriptor)) && license.exists((com.google.common.base.Predicate<LicenseAttributes>)((com.google.common.base.Predicate)l -> !((LicenseAttributes)l).evaluation && ((LicenseAttributes)l).isDataCenter == false));
    }

    public static boolean isProductRenewable(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            boolean hasAppropriateType = RENEWABLE_TYPES.contains(registeredLicense.licenseType);
            return LicensedAttributes.isProductRenewableInternal(license, currentRoleCount) && hasAppropriateType;
        }
        return false;
    }

    public static boolean isProductRenewableRequiringContact(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            boolean hasAppropriateType = RENEWABLE_TYPES.contains(registeredLicense.licenseType);
            return LicensedAttributes.isProductRenewableInternal(license, currentRoleCount) && !hasAppropriateType;
        }
        return false;
    }

    private static boolean isProductRenewableInternal(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        if (LicensedAttributes.isProductUpgradable(license, currentRoleCount)) {
            return false;
        }
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            boolean nearlyExpired = LicensedAttributes.isNearlyExpired().test(registeredLicense);
            boolean nearlyMaintenanceExpired = LicensedAttributes.isNearlyMaintenanceExpired().test(registeredLicense);
            boolean maintenanceExpired = registeredLicense.isMaintenanceExpired();
            boolean hasAppropriateError = LicensedAttributes.isErrorIn(registeredLicense.licenseError, Arrays.asList(LicenseError.EXPIRED.name(), LicenseError.VERSION_MISMATCH.name()));
            boolean evaluation = registeredLicense.evaluation;
            boolean active = registeredLicense.active;
            return active && (nearlyExpired || nearlyMaintenanceExpired || maintenanceExpired || hasAppropriateError) && !evaluation;
        }
        return false;
    }

    public static boolean isProductUpgradable(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isProductUpgradeRequired(license) || LicensedAttributes.isProductUpgradeNearlyRequired(license, currentRoleCount);
    }

    public static boolean isProductUpgradeNearlyRequired(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        return LicensedAttributes.isRoleNearlyExceeded(license, currentRoleCount);
    }

    public static boolean isProductUpgradeRequired(Option<LicenseAttributes> license) {
        if (LicensedAttributes.isProductBuyable(license)) {
            return false;
        }
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            return LicensedAttributes.isErrorIn(registeredLicense.licenseError, Arrays.asList(LicenseError.USER_MISMATCH.name(), LicenseError.EDITION_MISMATCH.name(), LicenseError.ROLE_EXCEEDED.name()));
        }
        return false;
    }

    public static boolean isRoleNearlyExceeded(Option<LicenseAttributes> license, Option<Integer> currentRoleCount) {
        Iterator<LicenseAttributes> iterator = license.iterator();
        if (iterator.hasNext()) {
            LicenseAttributes registeredLicense = iterator.next();
            return LicensedAttributes.isRoleNearlyExceeded(currentRoleCount).test(registeredLicense);
        }
        return false;
    }

    public static Predicate<LicenseAttributes> isRoleNearlyExceeded(Option<Integer> currentRoleCount) {
        return license -> {
            Iterator iterator = currentRoleCount.iterator();
            while (iterator.hasNext()) {
                int currRoleCount = (Integer)iterator.next();
                Iterator iterator2 = ((LicenseAttributes)license).edition.iterator();
                if (!iterator2.hasNext()) continue;
                int maxRoleCount = (Integer)iterator2.next();
                if (currRoleCount > maxRoleCount) {
                    return false;
                }
                if (maxRoleCount > 5) {
                    return (double)currRoleCount >= (double)maxRoleCount * 0.8;
                }
                return currRoleCount >= maxRoleCount - 1;
            }
            return false;
        };
    }

    public static Predicate<LicenseAttributes> isNearlyExpired() {
        return license -> (Boolean)((LicenseAttributes)license).expiryDate.map(expiryDate -> new Interval((ReadableInstant)expiryDate.minusDays(NEARLY_EXPIRED_DAYS.intValue()), (ReadableInstant)expiryDate).contains((ReadableInstant)new DateTime())).getOrElse(false);
    }

    public static Predicate<LicenseAttributes> isNearlyMaintenanceExpired() {
        return license -> (Boolean)((LicenseAttributes)license).maintenanceExpiryDate.map(maintenanceExpiryDate -> new Interval((ReadableInstant)maintenanceExpiryDate.minusDays(NEARLY_EXPIRED_DAYS.intValue()), (ReadableInstant)maintenanceExpiryDate).contains((ReadableInstant)new DateTime())).getOrElse(false);
    }

    private static boolean isErrorEqual(Option<String> possibleError, String equalTo) {
        Iterator<String> iterator = possibleError.iterator();
        if (iterator.hasNext()) {
            String error = iterator.next();
            return equalTo.equalsIgnoreCase(error);
        }
        return false;
    }

    private static boolean isErrorIn(Option<String> possibleError, Collection<String> errors) {
        List lowercaseErrorNames = errors.stream().map(String::toLowerCase).collect(Collectors.toList());
        Iterator<String> iterator = possibleError.iterator();
        if (iterator.hasNext()) {
            String error = iterator.next();
            return lowercaseErrorNames.contains(error.toLowerCase());
        }
        return false;
    }

    public static class LicenseAttributes {
        private final boolean active;
        private final boolean evaluation;
        private final Option<DateTime> expiryDate;
        private final Option<DateTime> maintenanceExpiryDate;
        private final Option<String> licenseError;
        private final Option<Integer> edition;
        private final String licenseType;
        private final Boolean isDataCenter;

        private LicenseAttributes(Builder builder) {
            this.active = Objects.requireNonNull(builder.active, "active");
            this.evaluation = Objects.requireNonNull(builder.evaluation, "evaluation");
            this.expiryDate = Objects.requireNonNull(builder.expiryDate, "expiryDate");
            this.maintenanceExpiryDate = Objects.requireNonNull(builder.maintenanceExpiryDate, "maintenanceExpiryDate");
            this.licenseError = Objects.requireNonNull(builder.licenseError, "licenseError");
            this.edition = Objects.requireNonNull(builder.edition, "edition");
            this.licenseType = Objects.requireNonNull(builder.licenseType, "licenseType");
            this.isDataCenter = Objects.requireNonNull(builder.isDataCenter, "isDataCenter");
        }

        public boolean isMaintenanceExpired() {
            Iterator<DateTime> iterator = this.maintenanceExpiryDate.iterator();
            if (iterator.hasNext()) {
                DateTime maintenanceExpiry = iterator.next();
                return maintenanceExpiry.isBeforeNow();
            }
            return false;
        }

        public boolean isDataCenter() {
            return this.isDataCenter;
        }

        public static Option<LicenseAttributes> from(Option<PluginLicense> pluginLicense) {
            Iterator<PluginLicense> iterator = pluginLicense.iterator();
            if (iterator.hasNext()) {
                PluginLicense l = iterator.next();
                return Option.some(LicenseAttributes.from(l));
            }
            return Option.none();
        }

        public static LicenseAttributes from(PluginLicense pluginLicense) {
            return LicenseAttributes.builder().active(pluginLicense.isActive()).evaluation(pluginLicense.isEvaluation()).expiryDate(pluginLicense.getExpiryDate()).maintenanceExpiryDate(pluginLicense.getMaintenanceExpiryDate()).licenseError(pluginLicense.getError().map(Enum::name)).edition(pluginLicense.getEdition()).licenseType(pluginLicense.getLicenseType().name()).isDataCenter(pluginLicense.isDataCenter()).build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Boolean active;
            private Boolean evaluation;
            private Option<DateTime> expiryDate;
            private Option<DateTime> maintenanceExpiryDate;
            private Option<String> licenseError;
            private Option<Integer> edition;
            private String licenseType;
            private Boolean isDataCenter;

            public Builder active(boolean active) {
                this.active = active;
                return this;
            }

            public Builder evaluation(boolean evaluation) {
                this.evaluation = evaluation;
                return this;
            }

            public Builder isDataCenter(boolean isDataCenter) {
                this.isDataCenter = isDataCenter;
                return this;
            }

            public Builder licenseError(Option<String> licenseError) {
                this.licenseError = licenseError;
                return this;
            }

            public Builder edition(Option<Integer> edition) {
                this.edition = edition;
                return this;
            }

            public Builder expiryDate(Option<DateTime> expiryDate) {
                this.expiryDate = expiryDate;
                return this;
            }

            public Builder maintenanceExpiryDate(Option<DateTime> maintenanceExpiryDate) {
                this.maintenanceExpiryDate = maintenanceExpiryDate;
                return this;
            }

            public Builder licenseType(String licenseType) {
                this.licenseType = licenseType;
                return this;
            }

            public LicenseAttributes build() {
                return new LicenseAttributes(this);
            }
        }
    }
}

