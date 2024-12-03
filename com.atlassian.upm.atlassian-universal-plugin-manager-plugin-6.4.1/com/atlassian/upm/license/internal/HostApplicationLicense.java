/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.BaseApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import org.joda.time.DateTime;

public class HostApplicationLicense
extends BaseApplicationLicense {
    private final boolean legacyEnterprise;
    private final boolean roleBased;
    private final boolean starter;
    private final String productKey;
    private final String encodedProductKey;
    private final String productName;
    private final String serverId;
    private final String rawLicense;
    private final DateTime purchaseDate;
    private final Iterable<HostApplicationEmbeddedAddonLicense> embeddedAddonLicenses;

    public HostApplicationLicense(boolean evaluation, boolean dataCenter, boolean legacyEnterprise, boolean roleBased, boolean starter, boolean autoRenewal, String productKey, String encodedProductKey, String productName, LicenseType licenseType, String serverId, String rawLicense, DateTime purchaseDate, Option<String> sen, Option<SubscriptionPeriod> subscriptionPeriod, Option<Integer> edition, Iterable<HostApplicationEmbeddedAddonLicense> embeddedAddonLicenses, Option<DateTime> lastModified, Option<DateTime> expiryDate, boolean stack) {
        super(edition, licenseType, evaluation, dataCenter, autoRenewal, sen, lastModified, expiryDate, subscriptionPeriod, stack);
        this.legacyEnterprise = legacyEnterprise;
        this.roleBased = roleBased;
        this.starter = starter;
        this.productKey = productKey;
        this.encodedProductKey = encodedProductKey;
        this.productName = productName;
        this.serverId = serverId;
        this.rawLicense = rawLicense;
        this.purchaseDate = purchaseDate;
        this.embeddedAddonLicenses = embeddedAddonLicenses;
    }

    public boolean isLegacyEnterprise() {
        return this.legacyEnterprise;
    }

    public boolean isRoleBased() {
        return this.roleBased;
    }

    public boolean isStarter() {
        return this.starter;
    }

    public String getProductKey() {
        return this.productKey;
    }

    public String getEncodedProductKey() {
        return this.encodedProductKey;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getRawLicense() {
        return this.rawLicense;
    }

    public DateTime getPurchaseDate() {
        return this.purchaseDate;
    }

    public Iterable<HostApplicationEmbeddedAddonLicense> getEmbeddedAddonLicenses() {
        return this.embeddedAddonLicenses;
    }

    public Option<HostApplicationEmbeddedAddonLicense> getEmbeddedAddonLicense(String pluginKey) {
        return Iterables.findOption(this.embeddedAddonLicenses, addonLicense -> addonLicense.getPluginKey().equals(pluginKey));
    }
}

