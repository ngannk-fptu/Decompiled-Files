/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.Organisation
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.user.User
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.user.User;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;

public final class LicenseAbstract {
    public static final int UNLIMITED = -1;
    private final ProductLicense productLicense;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    private final LocaleManager localeManager;

    public LicenseAbstract(ProductLicense productLicense, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, LocaleManager localeManager) {
        this.productLicense = productLicense;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.localeManager = localeManager;
    }

    public boolean isDataCenter() {
        return this.productLicense == null ? false : this.productLicense.isClusteringEnabled();
    }

    public String getOrganizationName() {
        Organisation organization = null == this.productLicense ? null : this.productLicense.getOrganisation();
        return null == organization ? null : organization.getName();
    }

    public DateTime getDatePurchased() {
        Date datePurchased = null == this.productLicense ? null : this.productLicense.getPurchaseDate();
        return null == datePurchased ? null : new DateTime(datePurchased.getTime());
    }

    public String getDatePurchasedFormatted(ConfluenceUser user) {
        DateTime datePurchased = this.getDatePurchased();
        if (null != datePurchased) {
            DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(user));
            return DateTimeFormat.longDateTime().withZone(userTimeZone).withLocale(this.localeManager.getLocale((User)user)).print((ReadableInstant)datePurchased);
        }
        return null;
    }

    public String getLicenseType() {
        LicenseType licenseType = null == this.productLicense ? null : this.productLicense.getLicenseType();
        return null == licenseType ? null : licenseType.name();
    }

    public String getLicenseTypeName() {
        return null == this.productLicense ? null : (this.productLicense.isEvaluation() ? "EVALUATION" : this.productLicense.getLicenseType().name());
    }

    public int getNumberOfUserLicensed() {
        return null == this.productLicense ? 0 : (this.productLicense.isUnlimitedNumberOfUsers() ? -1 : this.productLicense.getMaximumNumberOfUsers());
    }

    public DateTime getExpiry() {
        Date expiryDate = null == this.productLicense ? null : this.productLicense.getExpiryDate();
        return null == expiryDate ? null : new DateTime(expiryDate.getTime());
    }

    public String getExpiryFormatted(ConfluenceUser user) {
        DateTime expiryDate = this.getExpiry();
        if (null != expiryDate) {
            DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(user));
            return DateTimeFormat.longDateTime().withZone(userTimeZone).withLocale(this.localeManager.getLocale((User)user)).print((ReadableInstant)expiryDate);
        }
        return null;
    }

    public boolean isExpired() {
        return null == this.productLicense || this.productLicense.isExpired();
    }

    public DateTime getMaintenanceExpiry() {
        return null == this.productLicense ? null : new DateTime((Object)this.productLicense.getMaintenanceExpiryDate());
    }

    public String getMaintenanceExpiryFormatted(ConfluenceUser user) {
        DateTime maintenanceExpiry = this.getMaintenanceExpiry();
        if (null != maintenanceExpiry) {
            DateTimeZone userTimeZone = DateTimeZone.forID((String)this.jodaIcal4jTimeZoneMapper.getUserTimeZoneIdJoda(user));
            return DateTimeFormat.longDateTime().withZone(userTimeZone).withLocale(this.localeManager.getLocale((User)user)).print((ReadableInstant)maintenanceExpiry);
        }
        return null;
    }

    public boolean isMaintenanceExpired() {
        return null == this.productLicense || this.productLicense.isMaintenanceExpired();
    }

    public String getSupportEntitlementNumber() {
        return null == this.productLicense ? null : this.productLicense.getSupportEntitlementNumber();
    }

    public boolean isEvaluation() {
        return null != this.productLicense && this.productLicense.isEvaluation();
    }
}

