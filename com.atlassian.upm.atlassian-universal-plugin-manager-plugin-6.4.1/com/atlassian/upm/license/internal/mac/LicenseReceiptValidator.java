/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.mac;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.Objects;

public class LicenseReceiptValidator {
    private final PluginLicenseRepository pluginLicenseRepository;

    public LicenseReceiptValidator(PluginLicenseRepository pluginLicenseRepository) {
        this.pluginLicenseRepository = Objects.requireNonNull(pluginLicenseRepository, "pluginLicenseRepository");
    }

    public Option<ValidationError> validateReceivedLicense(PluginLicense newLicense, String pluginKey) {
        for (PluginLicense oldLicense : this.pluginLicenseRepository.getPluginLicense(pluginKey)) {
            if (!(this.valueIsEquivalentOrHigher(newLicense.getExpiryDate(), oldLicense.getExpiryDate()) && this.valueIsEquivalentOrHigher(newLicense.getMaintenanceExpiryDate(), oldLicense.getMaintenanceExpiryDate()) || this.isUpgradingToDataCenter(oldLicense, newLicense))) {
                return Option.some(ValidationError.EXPIRY_DATE_DOWNGRADE);
            }
            if (newLicense.isEvaluation() && !oldLicense.isEvaluation()) {
                return Option.some(ValidationError.EVAL_DOWNGRADE);
            }
            if (oldLicense.isEvaluation() || this.valueIsEquivalentOrHigher(newLicense.getEdition(), oldLicense.getEdition())) continue;
            return Option.some(ValidationError.EDITION_DOWNGRADE);
        }
        return Option.none();
    }

    private boolean isUpgradingToDataCenter(PluginLicense l1, PluginLicense l2) {
        return !l1.isDataCenter() && l2.isDataCenter();
    }

    private <T extends Comparable> boolean valueIsEquivalentOrHigher(Option<T> maybeNewValue, Option<T> maybeOldValue) {
        return (Boolean)maybeNewValue.map(newValue -> (Boolean)maybeOldValue.map(oldValue -> newValue.compareTo(oldValue) >= 0).getOrElse(false)).getOrElse(true);
    }

    public static enum ValidationError {
        EXPIRY_DATE_DOWNGRADE,
        EVAL_DOWNGRADE,
        EDITION_DOWNGRADE;

    }
}

