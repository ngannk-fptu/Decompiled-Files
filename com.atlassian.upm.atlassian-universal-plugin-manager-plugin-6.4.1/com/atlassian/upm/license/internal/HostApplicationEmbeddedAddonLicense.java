/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.license.internal;

import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginMetadata;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;

public class HostApplicationEmbeddedAddonLicense {
    private final boolean evaluation;
    private final boolean dataCenter;
    private final boolean legacyEnterprise;
    private final boolean autoRenewal;
    private final boolean active;
    private final boolean subscription;
    private final LicenseType licenseType;
    private final String pluginKey;
    private final String serverId;
    private final String rawLicense;
    private final String description;
    private final Option<String> sen;
    private final Option<SubscriptionPeriod> subscriptionPeriod;
    private final Option<Integer> edition;
    private final LicenseEditionType editionType;
    private final Option<RoleBasedPluginMetadata> roleBasedPluginMetadata;
    private final Option<DateTime> lastModified;
    private final Option<DateTime> subscriptionEndDate;
    private final Option<DateTime> maintenanceExpiryDate;
    private final Option<DateTime> expiryDate;
    private final DateTime purchaseDate;
    private final DateTime creationDate;
    private final Option<Integer> licenseVersion;
    private final Option<Partner> partner;
    private final List<Contact> contacts;
    private final Organization organization;
    private final boolean stack;

    public HostApplicationEmbeddedAddonLicense(boolean evaluation, boolean dataCenter, boolean legacyEnterprise, boolean autoRenewal, boolean active, boolean subscription, LicenseType licenseType, String pluginKey, String serverId, String rawLicense, String description, Option<String> sen, Option<SubscriptionPeriod> subscriptionPeriod, Option<Integer> edition, LicenseEditionType editionType, Option<RoleBasedPluginMetadata> roleBasedPluginMetadata, Option<DateTime> lastModified, Option<DateTime> subscriptionEndDate, Option<DateTime> maintenanceExpiryDate, Option<DateTime> expiryDate, DateTime purchaseDate, DateTime creationDate, Option<Integer> licenseVersion, Option<Partner> partner, List<Contact> contacts, Organization organization, boolean stack) {
        this.evaluation = evaluation;
        this.dataCenter = dataCenter;
        this.legacyEnterprise = legacyEnterprise;
        this.autoRenewal = autoRenewal;
        this.active = active;
        this.subscription = subscription;
        this.licenseType = licenseType;
        this.pluginKey = pluginKey;
        this.serverId = serverId;
        this.rawLicense = rawLicense;
        this.description = description;
        this.sen = sen;
        this.subscriptionPeriod = subscriptionPeriod;
        this.edition = edition;
        this.editionType = editionType;
        this.roleBasedPluginMetadata = roleBasedPluginMetadata;
        this.lastModified = lastModified;
        this.subscriptionEndDate = subscriptionEndDate;
        this.maintenanceExpiryDate = maintenanceExpiryDate;
        this.expiryDate = expiryDate;
        this.purchaseDate = purchaseDate;
        this.creationDate = creationDate;
        this.licenseVersion = licenseVersion;
        this.partner = partner;
        this.contacts = Collections.unmodifiableList(contacts);
        this.organization = organization;
        this.stack = stack;
    }

    public boolean isEvaluation() {
        return this.evaluation;
    }

    public boolean isDataCenter() {
        return this.dataCenter;
    }

    public boolean isLegacyEnterprise() {
        return this.legacyEnterprise;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public LicenseType getLicenseType() {
        return this.licenseType;
    }

    public String getServerId() {
        return this.serverId;
    }

    public String getRawLicense() {
        return this.rawLicense;
    }

    public Option<String> getSen() {
        return this.sen;
    }

    public Option<SubscriptionPeriod> getSubscriptionPeriod() {
        return this.subscriptionPeriod;
    }

    public Option<Integer> getEdition() {
        return this.edition;
    }

    public Option<DateTime> getLastModified() {
        return this.lastModified;
    }

    public boolean isAutoRenewal() {
        return this.autoRenewal;
    }

    public boolean isSubscription() {
        return this.subscription;
    }

    public boolean isActive() {
        return this.active;
    }

    public Option<DateTime> getSubscriptionEndDate() {
        return this.subscriptionEndDate;
    }

    public Option<DateTime> getMaintenanceExpiryDate() {
        return this.maintenanceExpiryDate;
    }

    public Option<DateTime> getExpiryDate() {
        return this.expiryDate;
    }

    public DateTime getPurchaseDate() {
        return this.purchaseDate;
    }

    public DateTime getCreationDate() {
        return this.creationDate;
    }

    public Option<Partner> getPartner() {
        return this.partner;
    }

    public List<Contact> getContacts() {
        return this.contacts;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public String getDescription() {
        return this.description;
    }

    public LicenseEditionType getEditionType() {
        return this.editionType;
    }

    public Option<RoleBasedPluginMetadata> getRoleBasedPluginMetadata() {
        return this.roleBasedPluginMetadata;
    }

    public Option<Integer> getLicenseVersion() {
        return this.licenseVersion;
    }

    public boolean isStack() {
        return this.stack;
    }
}

