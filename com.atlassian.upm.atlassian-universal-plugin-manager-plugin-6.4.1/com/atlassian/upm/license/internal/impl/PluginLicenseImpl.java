/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.joda.time.Period
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.impl.DateUtil;
import com.atlassian.upm.license.internal.impl.PluginLicenseBuilder;
import com.atlassian.upm.license.internal.impl.role.RoleBasedPluginMetadata;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;

public class PluginLicenseImpl
implements PluginLicense {
    private final String pluginKey;
    private final String rawLicense;
    private final Option<Integer> licenseVersion;
    private final String description;
    private final String serverId;
    private final Organization organization;
    private final Option<Partner> partner;
    private final Iterable<Contact> contacts;
    private final DateTime creationDate;
    private final DateTime purchaseDate;
    private final Option<DateTime> expiryDate;
    private final Option<String> supportEntitlementNumber;
    private final Option<DateTime> maintenanceExpiryDate;
    private final Option<DateTime> subscriptionEndDate;
    private final Option<DateTime> buildDate;
    private final Option<Integer> edition;
    private final Option<Integer> hostLicenseEdition;
    private final LicenseEditionType editionType;
    private final Option<RoleBasedPluginMetadata> roleBasedPluginMetadata;
    private final boolean evaluation;
    private final boolean hostEvaluation;
    private final boolean dataCenter;
    private final boolean hostDataCenter;
    private final boolean legacyEnterprise;
    private final boolean subscription;
    private final LicenseType licenseType;
    private final Option<LicenseType> hostLicenseType;
    private final String pluginName;
    private final boolean embeddedWithinHostLicense;
    private final boolean active;
    private final boolean autoRenewal;
    private final Option<SubscriptionPeriod> subscriptionPeriod;
    private final boolean dataCenterCompatibleApp;
    private final boolean appStackLicense;
    private final boolean hostStackLicense;
    private final boolean forged;

    public PluginLicenseImpl(PluginLicenseBuilder builder) {
        this.rawLicense = builder.rawLicense;
        this.licenseVersion = builder.licenseVersion;
        this.description = builder.description;
        this.serverId = builder.serverId;
        this.organization = builder.organization;
        this.partner = builder.partner;
        this.contacts = builder.contacts;
        this.creationDate = builder.creationDate;
        this.purchaseDate = builder.purchaseDate;
        this.expiryDate = builder.expiryDate;
        this.supportEntitlementNumber = builder.supportEntitlementNumber;
        this.maintenanceExpiryDate = builder.maintenanceExpiryDate;
        this.subscriptionEndDate = builder.subscriptionEndDate;
        this.buildDate = builder.buildDate;
        this.edition = builder.edition;
        this.hostLicenseEdition = builder.hostLicenseEdition;
        this.editionType = builder.editionType;
        this.roleBasedPluginMetadata = builder.roleBasedPluginMetadata;
        this.hostLicenseType = builder.hostLicenseType;
        this.evaluation = builder.evaluation;
        this.hostEvaluation = builder.hostEvaluation;
        this.dataCenter = builder.dataCenter;
        this.hostDataCenter = builder.hostDataCenter;
        this.legacyEnterprise = builder.legacyEnterprise;
        this.subscription = builder.subscription;
        this.licenseType = builder.licenseType;
        this.pluginName = builder.pluginName;
        this.pluginKey = builder.pluginKey;
        this.embeddedWithinHostLicense = builder.embeddedWithinHostLicense;
        this.active = builder.active;
        this.autoRenewal = builder.autoRenewal;
        this.subscriptionPeriod = builder.subscriptionPeriod;
        this.dataCenterCompatibleApp = builder.dataCenterCompatibleApp;
        this.appStackLicense = builder.appStackLicense;
        this.hostStackLicense = builder.hostStackLicense;
        this.forged = builder.forged;
    }

    @Override
    public boolean isValid() {
        return !this.getError().isDefined();
    }

    @Override
    public boolean isValidForDc() {
        if (this.isDataCenter()) {
            return true;
        }
        if (this.purchaseDate.isAfter((ReadableInstant)this.getServerLicenseCutoffDate())) {
            return false;
        }
        Iterator<DateTime> iterator = this.maintenanceExpiryDate.iterator();
        if (iterator.hasNext()) {
            DateTime med = iterator.next();
            if (med.isAfter((ReadableInstant)this.getServerLicenseCutoffDate())) {
                return med.isAfterNow();
            }
            return this.getServerLicenseCutoffDate().isAfterNow();
        }
        return this.getServerLicenseCutoffDate().isAfterNow();
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean isAutoRenewal() {
        return this.autoRenewal;
    }

    @Override
    public Option<LicenseError> getError() {
        if (this.hasExceededRole()) {
            return Option.some(LicenseError.ROLE_EXCEEDED);
        }
        if (this.isRoleUndefinedError()) {
            return Option.some(LicenseError.ROLE_UNDEFINED);
        }
        if (this.hasUserMismatch()) {
            return Option.some(LicenseError.USER_MISMATCH);
        }
        if (this.hasEditionMismatch()) {
            return Option.some(LicenseError.EDITION_MISMATCH);
        }
        if (!this.hasValidType()) {
            return Option.some(LicenseError.TYPE_MISMATCH);
        }
        if (this.isExpired()) {
            return Option.some(LicenseError.EXPIRED);
        }
        if (this.hasVersionMismatch()) {
            return Option.some(LicenseError.VERSION_MISMATCH);
        }
        return Option.none();
    }

    @Override
    public String getRawLicense() {
        return this.rawLicense;
    }

    @Override
    public Option<Integer> getLicenseVersion() {
        return this.licenseVersion;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getServerId() {
        return this.serverId;
    }

    @Override
    public Organization getOrganization() {
        return this.organization;
    }

    @Override
    public Option<Partner> getPartner() {
        return this.partner;
    }

    @Override
    public Iterable<Contact> getContacts() {
        return this.contacts;
    }

    @Override
    public DateTime getCreationDate() {
        return this.creationDate;
    }

    @Override
    public ZonedDateTime getCreationZonedDate() {
        return DateUtil.toZonedDate(this.creationDate);
    }

    @Override
    public DateTime getPurchaseDate() {
        return this.purchaseDate;
    }

    @Override
    public ZonedDateTime getPurchaseZonedDate() {
        return DateUtil.toZonedDate(this.purchaseDate);
    }

    @Override
    public Option<DateTime> getExpiryDate() {
        if (this.maybeUseServerCutoffDate()) {
            if (this.getPurchaseDate().isAfter((ReadableInstant)this.getServerLicenseCutoffDate())) {
                return Option.some(this.getServerLicenseCutoffDate());
            }
            return Option.some(this.max(this.getMaintenanceExpiryDate(), this.getServerLicenseCutoffDate()));
        }
        return this.expiryDate;
    }

    @Override
    public Optional<ZonedDateTime> getExpiryZonedDate() {
        if (this.maybeUseServerCutoffDate()) {
            if (this.getPurchaseZonedDate().isAfter(this.getServerLicenseCutoffZonedDate())) {
                return Optional.of(this.getServerLicenseCutoffZonedDate());
            }
            return Optional.of(DateUtil.toZonedDate(this.max(this.getMaintenanceExpiryDate(), this.getServerLicenseCutoffDate())));
        }
        return DateUtil.toOptionalZonedDate(this.expiryDate);
    }

    @Override
    public Option<Period> getTimeBeforeExpiry() {
        if (!this.isActive()) {
            return Option.some(Period.ZERO);
        }
        Iterator<DateTime> iterator = this.getExpiryDate().iterator();
        if (iterator.hasNext()) {
            DateTime date = iterator.next();
            return Option.some(new Period((ReadableInstant)new DateTime(), (ReadableInstant)date));
        }
        return Option.none(Period.class);
    }

    @Override
    public Optional<Duration> getDurationBeforeExpiry() {
        if (!this.isActive()) {
            return Optional.of(Duration.ZERO);
        }
        return this.getExpiryZonedDate().map(expiryDate -> Duration.between(ZonedDateTime.now(), expiryDate));
    }

    private boolean isExpired() {
        if (!this.isActive()) {
            return true;
        }
        for (DateTime date : this.getExpiryDate()) {
            if (!date.isBeforeNow()) continue;
            return true;
        }
        return false;
    }

    @Override
    public Option<String> getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }

    @Override
    public Option<DateTime> getMaintenanceExpiryDate() {
        return this.maintenanceExpiryDate;
    }

    @Override
    public Optional<ZonedDateTime> getMaintenanceExpiryZonedDate() {
        return DateUtil.toOptionalZonedDate(this.maintenanceExpiryDate);
    }

    @Override
    public Option<Period> getTimeBeforeMaintenanceExpiry() {
        Option<DateTime> maintenanceExpiry = this.getMaintenanceExpiryDate();
        Iterator<DateTime> iterator = maintenanceExpiry.iterator();
        if (iterator.hasNext()) {
            DateTime date = iterator.next();
            return Option.some(new Period((ReadableInstant)new DateTime(), (ReadableInstant)date));
        }
        return Option.none(Period.class);
    }

    @Override
    public Optional<Duration> getDurationBeforeMaintenanceExpiry() {
        Optional<ZonedDateTime> maintenanceExpiry = this.getMaintenanceExpiryZonedDate();
        return maintenanceExpiry.map(expiryDate -> Duration.between(ZonedDateTime.now(), expiryDate));
    }

    @Override
    public Option<DateTime> getSubscriptionEndDate() {
        return this.subscriptionEndDate;
    }

    @Override
    public Optional<ZonedDateTime> getSubscriptionEndZonedDate() {
        return DateUtil.toOptionalZonedDate(this.subscriptionEndDate);
    }

    @Override
    public Option<Integer> getMaximumNumberOfUsers() {
        if (!this.getEditionType().equals((Object)LicenseEditionType.USER_COUNT)) {
            return Option.some(0);
        }
        return this.getEdition();
    }

    @Override
    public boolean isUnlimitedNumberOfUsers() {
        return this.isUnlimitedEdition();
    }

    @Override
    public Option<Integer> getEdition() {
        return this.edition;
    }

    @Override
    public boolean isUnlimitedEdition() {
        return !this.edition.isDefined();
    }

    private boolean isHostLicenseUnlimitedEdition() {
        return !this.hostLicenseEdition.isDefined();
    }

    private boolean hasValidType() {
        if (this.evaluation || this.hostEvaluation) {
            return true;
        }
        if (this.maybeUseServerCutoffDate() && !this.isValidForDc()) {
            return false;
        }
        Iterator<LicenseType> iterator = this.hostLicenseType.iterator();
        if (iterator.hasNext()) {
            LicenseType hostType = iterator.next();
            LicenseType addonType = this.getLicenseType();
            return !hostType.isPaidType() || addonType.isPaidType();
        }
        return false;
    }

    private boolean maybeUseServerCutoffDate() {
        boolean hostHasFullDCLicense = this.hostDataCenter && !this.hostEvaluation;
        boolean appIsServerAndHasDCVersion = this.dataCenterCompatibleApp && !this.dataCenter;
        return hostHasFullDCLicense && appIsServerAndHasDCVersion && !this.evaluation;
    }

    private boolean hasUserMismatch() {
        return this.getEditionType().equals((Object)LicenseEditionType.USER_COUNT) && this.hasEditionMismatch();
    }

    private boolean hasEditionMismatch() {
        if (this.hostStackLicense && this.appStackLicense) {
            return false;
        }
        if (this.evaluation || this.hostEvaluation) {
            return false;
        }
        if (this.getEditionType().equals((Object)LicenseEditionType.ROLE_COUNT)) {
            return false;
        }
        if (!this.isUnlimitedEdition() && !this.isHostLicenseUnlimitedEdition()) {
            int possibleTolerance;
            int n = possibleTolerance = this.getEditionType().equals((Object)LicenseEditionType.USER_COUNT) ? 1 : 0;
            if (this.hostLicenseEdition.getOrElse(0) > this.getEdition().getOrElse(0) + possibleTolerance) {
                return true;
            }
        } else if (this.isHostLicenseUnlimitedEdition() && !this.isUnlimitedEdition()) {
            return true;
        }
        return false;
    }

    private boolean hasVersionMismatch() {
        Iterator<DateTime> iterator = this.getMaintenanceExpiryDate().iterator();
        if (iterator.hasNext()) {
            DateTime maintenanceExpiry = iterator.next();
            Iterator<DateTime> iterator2 = this.buildDate.iterator();
            if (iterator2.hasNext()) {
                DateTime maybeBuildDate = iterator2.next();
                return maintenanceExpiry.isBefore((ReadableInstant)maybeBuildDate);
            }
            return maintenanceExpiry.isBeforeNow();
        }
        return false;
    }

    private boolean hasExceededRole() {
        if (this.evaluation || this.hostEvaluation) {
            return false;
        }
        for (int current : this.getCurrentRoleCount()) {
            for (RoleBasedPluginMetadata data : this.roleBasedPluginMetadata) {
                Iterator<Integer> iterator = data.getLicensedRoleCount().iterator();
                if (!iterator.hasNext()) continue;
                int licensed = iterator.next();
                return current > licensed;
            }
        }
        return false;
    }

    private boolean isRoleUndefinedError() {
        if (this.evaluation || this.hostEvaluation) {
            return false;
        }
        Iterator<RoleBasedPluginMetadata> iterator = this.roleBasedPluginMetadata.iterator();
        if (iterator.hasNext()) {
            RoleBasedPluginMetadata data = iterator.next();
            return data.isRoleUndefined();
        }
        return false;
    }

    @Override
    public boolean isEvaluation() {
        return this.evaluation;
    }

    @Override
    public boolean isMaintenanceExpired() {
        Iterator<DateTime> iterator = this.getMaintenanceExpiryDate().iterator();
        if (iterator.hasNext()) {
            DateTime maintenanceExpiry = iterator.next();
            return maintenanceExpiry.isBeforeNow();
        }
        return false;
    }

    @Override
    public boolean isSubscription() {
        return this.subscription;
    }

    @Override
    public LicenseType getLicenseType() {
        switch (this.licenseType) {
            case STARTER: {
                return LicenseType.COMMERCIAL;
            }
            case OPEN_SOURCE: {
                return LicenseType.COMMUNITY;
            }
        }
        return this.licenseType;
    }

    @Override
    public String getLicenseTypeDescriptionKey() {
        return this.licenseType.name();
    }

    @Override
    public String getPluginName() {
        return this.pluginName;
    }

    @Override
    public String getPluginKey() {
        return this.pluginKey;
    }

    @Override
    public boolean isEmbeddedWithinHostLicense() {
        return this.embeddedWithinHostLicense;
    }

    @Override
    public boolean isEnterprise() {
        return this.legacyEnterprise || this.dataCenter;
    }

    @Override
    public boolean isDataCenter() {
        return this.dataCenter;
    }

    @Override
    public boolean isForged() {
        return this.forged;
    }

    @Override
    public Option<SubscriptionPeriod> getSubscriptionPeriod() {
        return this.subscriptionPeriod;
    }

    protected String[] getToStringFields() {
        return new String[]{"valid=" + this.isValid(), "error=" + this.getError(), "active=" + this.isActive(), "rawLicense=" + this.getRawLicense(), "licenseVersion=" + this.getLicenseVersion(), "description=" + this.getDescription(), "serverId=" + this.getServerId(), "organization=" + this.getOrganization(), "partner=" + this.getPartner(), "contacts=" + this.getContacts(), "creationDate=" + this.getCreationDate(), "purchaseDate=" + this.getPurchaseDate(), "expiryDate=" + this.getExpiryDate(), "supportEntitlementNumber=" + this.getSupportEntitlementNumber(), "maintenanceExpiryDate=" + this.getMaintenanceExpiryDate(), "subscriptionEndDate=" + this.getSubscriptionEndDate(), "maximumNumberOfUsers=" + this.getMaximumNumberOfUsers(), "edition=" + this.getEdition(), "evaluation=" + this.isEvaluation(), "subscription=" + this.isSubscription(), "autoRenewal=" + this.isAutoRenewal(), "licenseType=" + (Object)((Object)this.getLicenseType()), "pluginName=" + this.getPluginName(), "pluginKey=" + this.getPluginKey(), "editionType=" + (Object)((Object)this.getEditionType()), "dataCenter=" + this.isDataCenter(), "forged=" + this.isForged(), "embeddedWithinHostLicense=" + this.isEmbeddedWithinHostLicense()};
    }

    public String toString() {
        return "PluginLicenseImpl[" + StringUtils.join((Object[])this.getToStringFields(), (String)", ") + "]";
    }

    @Override
    public LicenseEditionType getEditionType() {
        return this.editionType;
    }

    protected Option<Integer> getCurrentRoleCount() {
        Iterator<RoleBasedPluginMetadata> iterator = this.roleBasedPluginMetadata.iterator();
        if (iterator.hasNext()) {
            RoleBasedPluginMetadata data = iterator.next();
            return data.getCurrentRoleCount();
        }
        return Option.none();
    }

    protected DateTime getServerLicenseCutoffDate() {
        return SERVER_LICENSE_CUTOFF_DATE;
    }

    protected ZonedDateTime getServerLicenseCutoffZonedDate() {
        return SERVER_LICENSE_CUTOFF_ZONED_DATE;
    }

    private DateTime max(Option<DateTime> odate1, DateTime date2) {
        Iterator<DateTime> iterator = odate1.iterator();
        if (iterator.hasNext()) {
            DateTime date1 = iterator.next();
            if (date1.isBefore((ReadableInstant)date2)) {
                return date2;
            }
            return date1;
        }
        return date2;
    }
}

