/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Contact
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.Organisation
 *  com.atlassian.extras.api.Partner
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.LicensePropertiesConstants
 *  com.atlassian.extras.common.LicenseTypeAndEditionResolver
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.Contact;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.Partner;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.LicensePropertiesConstants;
import com.atlassian.extras.common.LicenseTypeAndEditionResolver;
import com.atlassian.extras.common.util.LicenseProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class DefaultProductLicense
implements ProductLicense {
    private static final long MILLIS_IN_A_DAY = 86400000L;
    private final int licenseVersion;
    private final String description;
    private final Product product;
    private final String serverId;
    private final Partner partner;
    private final Organisation organisation;
    private final Collection<Contact> contacts;
    private final Date creationDate;
    private final Date purchaseDate;
    private final int maximumNumberOfUsers;
    private final Date expiryDate;
    private final Date gracePeriodEndDate;
    private final Date maintenanceExpiryDate;
    private final String supportEntitlementNumber;
    private final boolean evaluation;
    private final boolean subscription;
    private final boolean clusteringEnabled;
    private final LicenseType licenseType;
    private final LicenseProperties properties;
    private final Iterable<Product> products;

    protected DefaultProductLicense(Product product, LicenseProperties properties) {
        this.licenseVersion = Integer.valueOf(properties.getProperty("licenseVersion", String.valueOf(0)));
        this.description = properties.getProperty("Description");
        this.product = product;
        this.evaluation = properties.getBoolean("Evaluation");
        this.subscription = properties.getBoolean("Subscription");
        this.clusteringEnabled = properties.getBoolean(LicensePropertiesConstants.getKey((Product)product, (String)"DataCenter"));
        this.serverId = properties.getProperty("ServerID");
        this.partner = DefaultProductLicense.getPartner(properties);
        this.organisation = new DefaultOrganisation(properties.getProperty("Organisation"));
        this.contacts = DefaultProductLicense.getContacts(properties);
        this.creationDate = properties.getDate("CreationDate", LicensePropertiesConstants.DEFAULT_CREATION_DATE);
        this.purchaseDate = properties.getDate("PurchaseDate", this.creationDate);
        this.expiryDate = properties.getDate("LicenseExpiryDate", LicensePropertiesConstants.DEFAULT_EXPIRY_DATE);
        this.gracePeriodEndDate = this.getGracePeriodEndDate(properties, this.expiryDate);
        this.maintenanceExpiryDate = properties.getDate("MaintenanceExpiryDate", LicensePropertiesConstants.DEFAULT_EXPIRY_DATE);
        this.supportEntitlementNumber = properties.getProperty("SEN");
        this.maximumNumberOfUsers = properties.getInt("NumberOfUsers", 0);
        this.licenseType = LicenseTypeAndEditionResolver.getLicenseType((String)properties.getProperty("LicenseTypeName"));
        this.properties = properties;
        ArrayList<Product> productList = new ArrayList<Product>();
        for (String property : properties.getPropertiesEndingWith(".active").keySet()) {
            productList.add(Product.fromNamespace((String)property.substring(0, property.indexOf(".active"))));
        }
        this.products = productList;
    }

    private Date getGracePeriodEndDate(LicenseProperties properties, Date expiryDate) {
        if (expiryDate == null) {
            return null;
        }
        int gracePeriod = properties.getInt("GracePeriod", 0);
        return new Date(expiryDate.getTime() + 86400000L * (long)gracePeriod);
    }

    public Product getProduct() {
        return this.product;
    }

    public String getServerId() {
        return this.serverId;
    }

    public Partner getPartner() {
        return this.partner;
    }

    public Organisation getOrganisation() {
        return this.organisation;
    }

    public Collection<Contact> getContacts() {
        return this.contacts;
    }

    public Date getCreationDate() {
        return new Date(this.creationDate.getTime());
    }

    public Date getPurchaseDate() {
        return new Date(this.purchaseDate.getTime());
    }

    public Date getExpiryDate() {
        return this.expiryDate != null ? new Date(this.expiryDate.getTime()) : null;
    }

    public int getNumberOfDaysBeforeExpiry() {
        if (this.expiryDate == null) {
            return Integer.MAX_VALUE;
        }
        return this.getDaysBeforeDate(this.expiryDate);
    }

    public boolean isExpired() {
        return this.expiryDate != null && this.expiryDate.compareTo(new Date()) < 0;
    }

    public Date getGracePeriodEndDate() {
        return this.gracePeriodEndDate != null ? new Date(this.gracePeriodEndDate.getTime()) : null;
    }

    public int getNumberOfDaysBeforeGracePeriodExpiry() {
        if (this.gracePeriodEndDate == null) {
            return Integer.MAX_VALUE;
        }
        return this.getDaysBeforeDate(this.gracePeriodEndDate);
    }

    public boolean isWithinGracePeriod() {
        return this.isExpired() && !this.isGracePeriodExpired();
    }

    public boolean isGracePeriodExpired() {
        return this.gracePeriodEndDate != null && this.gracePeriodEndDate.compareTo(new Date()) < 0;
    }

    public Date getMaintenanceExpiryDate() {
        return this.maintenanceExpiryDate != null ? new Date(this.maintenanceExpiryDate.getTime()) : null;
    }

    public int getNumberOfDaysBeforeMaintenanceExpiry() {
        if (this.maintenanceExpiryDate == null) {
            return Integer.MAX_VALUE;
        }
        return this.getDaysBeforeDate(this.maintenanceExpiryDate);
    }

    public boolean isMaintenanceExpired() {
        return this.maintenanceExpiryDate != null && this.maintenanceExpiryDate.compareTo(new Date()) < 0;
    }

    public String getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }

    public int getMaximumNumberOfUsers() {
        return this.maximumNumberOfUsers;
    }

    public boolean isUnlimitedNumberOfUsers() {
        return this.maximumNumberOfUsers == -1;
    }

    public boolean isEvaluation() {
        return this.evaluation;
    }

    public boolean isSubscription() {
        return this.subscription;
    }

    public boolean isClusteringEnabled() {
        return this.clusteringEnabled;
    }

    public String getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Iterable<Product> getProducts() {
        return this.products;
    }

    private int getDaysBeforeDate(Date date) {
        return (int)((date.getTime() - System.currentTimeMillis()) / 86400000L);
    }

    private static Partner getPartner(LicenseProperties properties) {
        String partnerName = properties.getProperty("PartnerName");
        return partnerName != null ? new DefaultPartner(partnerName) : null;
    }

    private static Collection<Contact> getContacts(LicenseProperties properties) {
        String contactEmail = properties.getProperty("ContactEMail");
        String contactName = properties.getProperty("ContactName");
        if (contactEmail != null || contactName != null) {
            return Collections.singletonList(new DefaultContact(contactName, contactEmail));
        }
        return Collections.emptyList();
    }

    public int getLicenseVersion() {
        return this.licenseVersion;
    }

    public String getDescription() {
        return this.description;
    }

    public LicenseType getLicenseType() {
        return this.licenseType;
    }

    private static final class DefaultContact
    implements Contact {
        private final String name;
        private final String email;

        DefaultContact(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return this.name;
        }

        public String getEmail() {
            return this.email;
        }
    }

    private static final class DefaultOrganisation
    implements Organisation {
        private final String name;

        DefaultOrganisation(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private static final class DefaultPartner
    implements Partner {
        private final String name;

        DefaultPartner(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

