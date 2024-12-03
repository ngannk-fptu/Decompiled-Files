/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.License;
import com.atlassian.license.LicenseType;
import java.util.Collection;
import java.util.Date;

@Deprecated
public class DefaultLicense
implements License {
    public static long EVALUATION_PERIOD = 2678400000L;
    protected Date dateCreated;
    protected Date datePurchased;
    protected Date dateExpired;
    protected String organisation;
    protected LicenseType licenseType;
    private int users;
    private String partnerName;
    private String licenseId;
    private int permittedClusteredNodes;
    private long duration = -1L;
    private String sen = null;

    public DefaultLicense(Date dateCreated, Date datePurchased, String organisation, LicenseType licenseType, int users, String partnerName, String sen) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, "", 0, sen);
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, Date expires, String organisationName, LicenseType licenseType, int users, String partnerName, String sen) {
        this(dateCreated, datePurchased, expires, organisationName, licenseType, users, partnerName, "", 0, sen);
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes, String sen) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, licenseId, permittedClusteredNodes);
        this.sen = sen;
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, long duration, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes, String sen) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, licenseId, permittedClusteredNodes, sen);
        this.duration = duration;
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, Date dateExpired, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes, String sen) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, licenseId, permittedClusteredNodes, sen);
        this.dateExpired = dateExpired;
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, String organisation, LicenseType licenseType, int users, String partnerName) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, "", 0);
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, Date expires, String organisationName, LicenseType licenseType, int users, String partnerName) {
        this(dateCreated, datePurchased, expires, organisationName, licenseType, users, partnerName, "", 0);
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes) {
        this.dateCreated = dateCreated;
        this.datePurchased = datePurchased;
        this.organisation = organisation;
        this.licenseType = licenseType;
        this.users = users;
        this.partnerName = partnerName;
        this.licenseId = licenseId;
        this.permittedClusteredNodes = permittedClusteredNodes;
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, long duration, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, licenseId, permittedClusteredNodes);
        this.duration = duration;
    }

    public DefaultLicense(Date dateCreated, Date datePurchased, Date dateExpired, String organisation, LicenseType licenseType, int users, String partnerName, String licenseId, int permittedClusteredNodes) {
        this(dateCreated, datePurchased, organisation, licenseType, users, partnerName, licenseId, permittedClusteredNodes);
        this.dateExpired = dateExpired;
    }

    @Override
    public Date getDateCreated() {
        return this.dateCreated;
    }

    @Override
    public Date getDatePurchased() {
        return this.datePurchased;
    }

    @Override
    public String getOrganisation() {
        return this.organisation;
    }

    @Override
    public LicenseType getLicenseType() {
        return this.licenseType;
    }

    @Override
    public String toString() {
        return this.licenseType.getNiceName() + " licensed to " + this.organisation;
    }

    @Override
    public boolean isExpired() {
        Date expiry = this.getExpiryDate();
        if (expiry == null) {
            return false;
        }
        return expiry.getTime() < System.currentTimeMillis();
    }

    @Override
    public Date getExpiryDate() {
        Date expiry = this.dateExpired;
        if (expiry == null && this.licenseType.isEvaluationLicenseType()) {
            expiry = new Date(this.datePurchased.getTime() + EVALUATION_PERIOD);
        }
        return expiry;
    }

    @Override
    public String getPartnerName() {
        return this.partnerName;
    }

    @Override
    public boolean isLicenseLevel(Collection levels) {
        for (String level : levels) {
            if (this.getLicenseType().getDescription().toLowerCase().indexOf(level.toLowerCase()) == -1) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getUsers() {
        if (this.licenseType.requiresUserLimit()) {
            return this.users;
        }
        return -1;
    }

    @Override
    public String getLicenseId() {
        return this.licenseId;
    }

    @Override
    public int getPermittedClusteredNodes() {
        return this.permittedClusteredNodes;
    }

    @Override
    public long getLicenseDuration() {
        return this.duration;
    }

    @Override
    public String getSupportEntitlementNumber() {
        return this.sen;
    }
}

