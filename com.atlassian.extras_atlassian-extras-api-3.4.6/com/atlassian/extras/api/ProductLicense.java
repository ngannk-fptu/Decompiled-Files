/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

import com.atlassian.extras.api.Contact;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.Partner;
import com.atlassian.extras.api.Product;
import java.util.Collection;
import java.util.Date;

public interface ProductLicense {
    public int getLicenseVersion();

    public String getDescription();

    public Product getProduct();

    public Iterable<Product> getProducts();

    public String getServerId();

    public Partner getPartner();

    public Organisation getOrganisation();

    public Collection<Contact> getContacts();

    public Date getCreationDate();

    public Date getPurchaseDate();

    public Date getExpiryDate();

    public int getNumberOfDaysBeforeExpiry();

    public boolean isExpired();

    public Date getGracePeriodEndDate();

    public int getNumberOfDaysBeforeGracePeriodExpiry();

    public boolean isWithinGracePeriod();

    public boolean isGracePeriodExpired();

    public String getSupportEntitlementNumber();

    public Date getMaintenanceExpiryDate();

    public int getNumberOfDaysBeforeMaintenanceExpiry();

    public boolean isMaintenanceExpired();

    public int getMaximumNumberOfUsers();

    public boolean isUnlimitedNumberOfUsers();

    public boolean isEvaluation();

    public boolean isSubscription();

    public boolean isClusteringEnabled();

    public LicenseType getLicenseType();

    public String getProperty(String var1);
}

