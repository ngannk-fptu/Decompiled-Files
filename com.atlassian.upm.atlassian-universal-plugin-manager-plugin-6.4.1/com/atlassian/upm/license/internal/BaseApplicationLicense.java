/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import org.joda.time.DateTime;

public abstract class BaseApplicationLicense {
    private final Option<Integer> edition;
    private final LicenseType licenseType;
    private final boolean evaluation;
    private final boolean dataCenter;
    private final boolean autoRenewal;
    private final Option<String> sen;
    private final Option<DateTime> lastModifiedDate;
    private final Option<DateTime> expiryDate;
    private final Option<SubscriptionPeriod> subscriptionPeriod;
    private final boolean stack;

    public BaseApplicationLicense(Option<Integer> edition, LicenseType licenseType, boolean evaluation, boolean dataCenter, boolean autoRenewal, Option<String> sen, Option<DateTime> lastModifiedDate, Option<DateTime> expiryDate, Option<SubscriptionPeriod> subscriptionPeriod, boolean stack) {
        this.edition = edition;
        this.licenseType = licenseType;
        this.evaluation = evaluation;
        this.dataCenter = dataCenter;
        this.autoRenewal = autoRenewal;
        this.sen = sen;
        this.lastModifiedDate = lastModifiedDate;
        this.expiryDate = expiryDate;
        this.subscriptionPeriod = subscriptionPeriod;
        this.stack = stack;
    }

    public Option<Integer> getEdition() {
        return this.edition;
    }

    public LicenseType getLicenseType() {
        return this.licenseType;
    }

    public boolean isEvaluation() {
        return this.evaluation;
    }

    public boolean isDataCenter() {
        return this.dataCenter;
    }

    public boolean isAutoRenewal() {
        return this.autoRenewal;
    }

    public Option<String> getSen() {
        return this.sen;
    }

    public Option<DateTime> getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Option<SubscriptionPeriod> getSubscriptionPeriod() {
        return this.subscriptionPeriod;
    }

    public Option<DateTime> getExpiryDate() {
        return this.expiryDate;
    }

    public boolean isStack() {
        return this.stack;
    }
}

