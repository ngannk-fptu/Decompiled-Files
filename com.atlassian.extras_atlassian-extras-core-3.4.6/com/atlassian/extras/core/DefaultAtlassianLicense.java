/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Contact
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.Organisation
 *  com.atlassian.extras.api.Partner
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 *  com.atlassian.extras.common.util.ProductLicenseProperties
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Contact;
import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.Organisation;
import com.atlassian.extras.api.Partner;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.common.util.ProductLicenseProperties;
import com.atlassian.extras.core.plugins.DefaultPluginLicense;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class DefaultAtlassianLicense
implements AtlassianLicense {
    private final Map<Product, ProductLicense> productLicenseMap;

    DefaultAtlassianLicense(Collection<? extends ProductLicense> productLicenses) {
        this.productLicenseMap = new HashMap<Product, ProductLicense>(productLicenses.size());
        for (ProductLicense productLicense : productLicenses) {
            this.productLicenseMap.put(productLicense.getProduct(), productLicense);
        }
    }

    public Collection<ProductLicense> getProductLicenses() {
        return Collections.unmodifiableCollection(this.productLicenseMap.values());
    }

    public ProductLicense getProductLicense(Product product) {
        ProductLicense license = this.productLicenseMap.get(product);
        if (license != null) {
            return license;
        }
        if (product.isPlugin() && this.productLicenseMap.containsKey(Product.ALL_PLUGINS)) {
            ProductLicense delegate = this.productLicenseMap.get(Product.ALL_PLUGINS);
            return new DelegatingPluginLicense(product, delegate);
        }
        return null;
    }

    private static final class DelegatingPluginLicense
    extends DefaultPluginLicense {
        private final ProductLicense delegate;

        private DelegatingPluginLicense(Product product, ProductLicense delegate) {
            super(product, DelegatingPluginLicense.newLicenseProperties(product, delegate));
            this.delegate = delegate;
        }

        private static LicenseProperties newLicenseProperties(Product product, ProductLicense license) {
            Properties props = new Properties();
            props.setProperty("LicenseTypeName", license.getProperty("LicenseTypeName"));
            return new ProductLicenseProperties(product, props);
        }

        @Override
        public int getLicenseVersion() {
            return this.delegate.getLicenseVersion();
        }

        @Override
        public String getDescription() {
            return this.delegate.getDescription();
        }

        @Override
        public String getServerId() {
            return this.delegate.getServerId();
        }

        @Override
        public Partner getPartner() {
            return this.delegate.getPartner();
        }

        @Override
        public Organisation getOrganisation() {
            return this.delegate.getOrganisation();
        }

        @Override
        public Collection<Contact> getContacts() {
            return this.delegate.getContacts();
        }

        @Override
        public Date getCreationDate() {
            return this.delegate.getCreationDate();
        }

        @Override
        public Date getPurchaseDate() {
            return this.delegate.getPurchaseDate();
        }

        @Override
        public Date getExpiryDate() {
            return this.delegate.getExpiryDate();
        }

        @Override
        public int getNumberOfDaysBeforeExpiry() {
            return this.delegate.getNumberOfDaysBeforeExpiry();
        }

        @Override
        public boolean isExpired() {
            return this.delegate.isExpired();
        }

        @Override
        public Date getGracePeriodEndDate() {
            return this.delegate.getGracePeriodEndDate();
        }

        @Override
        public int getNumberOfDaysBeforeGracePeriodExpiry() {
            return this.delegate.getNumberOfDaysBeforeGracePeriodExpiry();
        }

        @Override
        public boolean isWithinGracePeriod() {
            return this.delegate.isWithinGracePeriod();
        }

        @Override
        public boolean isGracePeriodExpired() {
            return this.delegate.isGracePeriodExpired();
        }

        @Override
        public String getSupportEntitlementNumber() {
            return this.delegate.getSupportEntitlementNumber();
        }

        @Override
        public Date getMaintenanceExpiryDate() {
            return this.delegate.getMaintenanceExpiryDate();
        }

        @Override
        public int getNumberOfDaysBeforeMaintenanceExpiry() {
            return this.delegate.getNumberOfDaysBeforeMaintenanceExpiry();
        }

        @Override
        public boolean isMaintenanceExpired() {
            return this.delegate.isMaintenanceExpired();
        }

        @Override
        public int getMaximumNumberOfUsers() {
            return this.delegate.getMaximumNumberOfUsers();
        }

        @Override
        public boolean isUnlimitedNumberOfUsers() {
            return this.delegate.isUnlimitedNumberOfUsers();
        }

        @Override
        public boolean isEvaluation() {
            return this.delegate.isEvaluation();
        }

        @Override
        public boolean isSubscription() {
            return this.delegate.isSubscription();
        }

        @Override
        public boolean isClusteringEnabled() {
            return this.delegate.isClusteringEnabled();
        }

        @Override
        public LicenseType getLicenseType() {
            return this.delegate.getLicenseType();
        }

        @Override
        public String getProperty(String name) {
            return this.delegate.getProperty(name);
        }
    }
}

