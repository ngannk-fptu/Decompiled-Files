/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.DateEditor
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.license.decoder;

import com.atlassian.extras.common.DateEditor;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.license.License;
import com.atlassian.license.LicenseType;
import java.util.Collection;
import java.util.Date;

@Deprecated
public class LicenseAdaptor
implements License {
    private static final long _1_YEAR = 31622400000L;
    private final LicenseType licenseType;
    private final Date creationDate;
    private final Date purchaseDate;
    private final Date expiryDate;
    private final Date maintenanceExpiryDate;
    private final boolean evaluation;
    private final String supportEntitlementNumber;
    private final int permittedClusterNodes;
    private final String organisation;
    private final String partner;
    private final int maximumNumberOfUsers;

    public LicenseAdaptor(LicenseProperties licenseProperties, LicenseType licenseType) {
        this.licenseType = licenseType;
        this.creationDate = DateEditor.getDate((String)licenseProperties.getProperty("CreationDate"));
        this.expiryDate = DateEditor.getDate((String)licenseProperties.getProperty("LicenseExpiryDate"));
        this.evaluation = licenseProperties.getBoolean("Evaluation");
        this.purchaseDate = DateEditor.getDate((String)licenseProperties.getProperty("PurchaseDate"));
        this.maintenanceExpiryDate = DateEditor.getDate((String)licenseProperties.getProperty("MaintenanceExpiryDate"));
        this.supportEntitlementNumber = licenseProperties.getProperty("SEN");
        this.permittedClusterNodes = licenseProperties.getInt("NumberOfClusterNodes", 0);
        this.organisation = licenseProperties.getProperty("Organisation");
        this.partner = licenseProperties.getProperty("PartnerName");
        int maxUsers = licenseProperties.getInt("NumberOfUsers", 0);
        this.maximumNumberOfUsers = maxUsers == -1 ? 10000 : maxUsers;
    }

    @Override
    public Date getDateCreated() {
        Date updatedCreationDate = this.maintenanceExpiryDate != null ? new Date(this.maintenanceExpiryDate.getTime() - 31622400000L) : new Date(this.creationDate.getTime());
        return updatedCreationDate;
    }

    @Override
    public Date getDatePurchased() {
        return this.purchaseDate;
    }

    @Override
    public Date getExpiryDate() {
        return this.licenseType.expires() || this.evaluation ? this.expiryDate : null;
    }

    @Override
    public long getLicenseDuration() {
        return 0L;
    }

    @Override
    public String getLicenseId() {
        return this.supportEntitlementNumber;
    }

    @Override
    public LicenseType getLicenseType() {
        return this.licenseType;
    }

    @Override
    public String getOrganisation() {
        return this.organisation;
    }

    @Override
    public String getPartnerName() {
        return this.partner;
    }

    @Override
    public int getPermittedClusteredNodes() {
        return this.permittedClusterNodes;
    }

    @Override
    public int getUsers() {
        if (this.licenseType.requiresUserLimit()) {
            return this.maximumNumberOfUsers;
        }
        return -1;
    }

    @Override
    public boolean isExpired() {
        return this.expiryDate != null && System.currentTimeMillis() > this.expiryDate.getTime();
    }

    @Override
    public boolean isLicenseLevel(Collection levels) {
        String description = this.getLicenseType().getDescription().toLowerCase();
        for (String level : levels) {
            if (description.indexOf(level.toLowerCase()) == -1) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }
}

