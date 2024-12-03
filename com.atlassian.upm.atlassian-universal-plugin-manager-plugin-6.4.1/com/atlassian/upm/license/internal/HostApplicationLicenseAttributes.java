/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.base.AbstractInstant
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.BaseApplicationLicense;
import com.atlassian.upm.license.internal.LicenseDateFormatter;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;

public class HostApplicationLicenseAttributes
extends BaseApplicationLicense {
    public HostApplicationLicenseAttributes(Option<Integer> edition, LicenseType licenseType, boolean evaluation, boolean dataCenter, boolean autoRenewal, Option<String> sen, Option<DateTime> lastModified, Option<DateTime> expiryDate, Option<SubscriptionPeriod> subscriptionPeriod, boolean stack) {
        super(edition, licenseType, evaluation, dataCenter, autoRenewal, sen, lastModified, expiryDate, subscriptionPeriod, stack);
    }

    @Override
    public Option<Integer> getEdition() {
        return super.getEdition();
    }

    @Override
    public LicenseType getLicenseType() {
        return super.getLicenseType();
    }

    @Override
    public boolean isEvaluation() {
        return super.isEvaluation();
    }

    @Override
    public boolean isDataCenter() {
        return super.isDataCenter();
    }

    @Override
    public boolean isAutoRenewal() {
        return super.isAutoRenewal();
    }

    @Override
    public Option<String> getSen() {
        return super.getSen();
    }

    @Override
    public Option<DateTime> getLastModifiedDate() {
        return super.getLastModifiedDate();
    }

    @Override
    public Option<SubscriptionPeriod> getSubscriptionPeriod() {
        return super.getSubscriptionPeriod();
    }

    @Override
    public Option<DateTime> getExpiryDate() {
        return super.getExpiryDate();
    }

    public static Option<Date> getExpiryDate(HostApplicationLicenseAttributes attrs) {
        return attrs.getExpiryDate().map(AbstractInstant::toDate);
    }

    public static Option<String> getExpiryDateString(HostApplicationLicenseAttributes attrs, LicenseDateFormatter dateFormatter) {
        return attrs.getExpiryDate().map(dateFormatter::formatDate);
    }

    public static boolean isExpired(HostApplicationLicenseAttributes attrs) {
        return (Boolean)attrs.getExpiryDate().map(AbstractInstant::isBeforeNow).getOrElse(false);
    }
}

