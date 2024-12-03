/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.Period
 */
package com.atlassian.upm.api.license.entity;

import com.atlassian.upm.api.license.entity.Contact;
import com.atlassian.upm.api.license.entity.LicenseEditionType;
import com.atlassian.upm.api.license.entity.LicenseError;
import com.atlassian.upm.api.license.entity.LicenseType;
import com.atlassian.upm.api.license.entity.Organization;
import com.atlassian.upm.api.license.entity.Partner;
import com.atlassian.upm.api.license.entity.SubscriptionPeriod;
import com.atlassian.upm.api.util.Option;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.Period;

public interface PluginLicense {
    @Deprecated
    public static final DateTime SERVER_LICENSE_CUTOFF_DATE = new DateTime().withYear(2019).withMonthOfYear(9).withDayOfMonth(3);
    public static final ZonedDateTime SERVER_LICENSE_CUTOFF_ZONED_DATE = ZonedDateTime.of(2019, 9, 3, 0, 0, 0, 0, ZoneId.systemDefault());

    public boolean isValid();

    public boolean isValidForDc();

    public boolean isActive();

    public boolean isAutoRenewal();

    public Option<LicenseError> getError();

    public String getRawLicense();

    public Option<Integer> getLicenseVersion();

    public String getPluginName();

    public String getDescription();

    public String getServerId();

    public Organization getOrganization();

    public Option<Partner> getPartner();

    public Iterable<Contact> getContacts();

    @Deprecated
    public DateTime getCreationDate();

    default public ZonedDateTime getCreationZonedDate() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    @Deprecated
    public DateTime getPurchaseDate();

    default public ZonedDateTime getPurchaseZonedDate() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    @Deprecated
    public Option<DateTime> getExpiryDate();

    default public Optional<ZonedDateTime> getExpiryZonedDate() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    @Deprecated
    public Option<Period> getTimeBeforeExpiry();

    default public Optional<Duration> getDurationBeforeExpiry() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    public Option<String> getSupportEntitlementNumber();

    @Deprecated
    public Option<DateTime> getMaintenanceExpiryDate();

    public Optional<ZonedDateTime> getMaintenanceExpiryZonedDate();

    @Deprecated
    public Option<Period> getTimeBeforeMaintenanceExpiry();

    default public Optional<Duration> getDurationBeforeMaintenanceExpiry() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    @Deprecated
    public Option<DateTime> getSubscriptionEndDate();

    default public Optional<ZonedDateTime> getSubscriptionEndZonedDate() {
        throw new UnsupportedOperationException("Please override this method in the child type");
    }

    @Deprecated
    public Option<Integer> getMaximumNumberOfUsers();

    public Option<Integer> getEdition();

    public LicenseEditionType getEditionType();

    @Deprecated
    public boolean isUnlimitedNumberOfUsers();

    public boolean isUnlimitedEdition();

    public boolean isEvaluation();

    public boolean isSubscription();

    public boolean isMaintenanceExpired();

    public LicenseType getLicenseType();

    public String getLicenseTypeDescriptionKey();

    public String getPluginKey();

    public boolean isEmbeddedWithinHostLicense();

    @Deprecated
    public boolean isEnterprise();

    public boolean isDataCenter();

    public boolean isForged();

    public Option<SubscriptionPeriod> getSubscriptionPeriod();
}

