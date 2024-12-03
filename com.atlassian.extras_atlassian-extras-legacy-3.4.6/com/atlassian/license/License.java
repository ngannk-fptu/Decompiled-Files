/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

import com.atlassian.license.LicenseType;
import java.util.Collection;
import java.util.Date;

@Deprecated
public interface License {
    public Date getDateCreated();

    public Date getDatePurchased();

    public String getOrganisation();

    public LicenseType getLicenseType();

    public boolean isExpired();

    public Date getExpiryDate();

    public String toString();

    public boolean isLicenseLevel(Collection var1);

    public int getUsers();

    public String getPartnerName();

    public String getLicenseId();

    public int getPermittedClusteredNodes();

    public long getLicenseDuration();

    public String getSupportEntitlementNumber();
}

