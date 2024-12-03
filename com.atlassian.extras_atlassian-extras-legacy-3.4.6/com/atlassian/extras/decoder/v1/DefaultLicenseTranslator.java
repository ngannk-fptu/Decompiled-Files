/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.DateEditor
 *  com.atlassian.extras.common.LicensePropertiesConstants
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 */
package com.atlassian.extras.decoder.v1;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.DateEditor;
import com.atlassian.extras.common.LicensePropertiesConstants;
import com.atlassian.extras.common.log.Logger;
import com.atlassian.extras.decoder.v1.LicenseTranslator;
import com.atlassian.license.DefaultLicense;
import com.atlassian.license.License;
import com.atlassian.license.LicenseUtils;
import java.util.Date;
import java.util.Properties;

@Deprecated
public class DefaultLicenseTranslator
implements LicenseTranslator {
    private static final Logger.Log log = Logger.getInstance(DefaultLicenseTranslator.class);
    private final Product product;

    public DefaultLicenseTranslator(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product must NOT be null!");
        }
        this.product = product;
    }

    @Override
    public final Properties translate(License license) {
        Properties properties = new Properties();
        this.setProperties(properties, license);
        return properties;
    }

    protected void setProperties(Properties properties, License license) {
        this.setDescription(properties, license);
        this.setActiveFlag(properties);
        this.setIsEvaluation(properties, license);
        this.setLicenseId(properties, license);
        this.setCreationDate(properties, license);
        this.setExpiryDate(properties, license);
        this.setMaintenanceExpiryDate(properties, license);
        this.setPurchaseDate(properties, license);
        this.setPartner(properties, license);
        this.setOrganisation(properties, license);
        this.setMaximumNumberOfUsers(properties, license);
        this.setLicenseType(properties, license);
        this.setLicenseEdition(properties, license);
        this.setSupportEntitlementNumber(properties, license);
    }

    private void setSupportEntitlementNumber(Properties properties, License license) {
        if (license.getSupportEntitlementNumber() != null) {
            this.setProperty(properties, "SEN", license.getSupportEntitlementNumber());
        }
    }

    private void setLicenseEdition(Properties properties, License license) {
        if (license.getLicenseType().getEdition() != null) {
            this.setProperty(properties, "LicenseEdition", license.getLicenseType().getEdition().name());
        }
    }

    private void setLicenseType(Properties properties, License license) {
        this.setProperty(properties, "LicenseTypeName", license.getLicenseType().getNewLicenseTypeName());
    }

    private void setActiveFlag(Properties properties) {
        this.setProperty(properties, "active", "true");
    }

    protected void setIsEvaluation(Properties properties, License license) {
        if (this.isEvaluationLicense(license)) {
            this.setProperty(properties, "Evaluation", "true");
        }
    }

    protected void setLicenseId(Properties properties, License license) {
        this.setProperty(properties, "LicenseID", license.getLicenseId());
    }

    protected void setDescription(Properties properties, License license) {
        this.setProperty(properties, "Description", license.getLicenseType().getDescription());
    }

    protected void setCreationDate(Properties properties, License license) {
        Date creationDate = license.getDateCreated();
        log.debug((Object)("Decoded creation date of <" + license + "> is <" + creationDate + ">"));
        this.setProperty(properties, "CreationDate", DateEditor.getString((Date)creationDate));
    }

    protected void setExpiryDate(Properties properties, License license) {
        Date expiryDate = this.isEvaluationLicense(license) ? new Date(license.getDateCreated().getTime() + DefaultLicense.EVALUATION_PERIOD) : (license.getExpiryDate() != null ? license.getExpiryDate() : null);
        this.setProperty(properties, "LicenseExpiryDate", DateEditor.getString((Date)expiryDate));
    }

    protected void setMaintenanceExpiryDate(Properties properties, License license) {
        Date maintenanceExpiryDate = this.isEvaluationLicense(license) ? new Date(license.getDateCreated().getTime() + DefaultLicense.EVALUATION_PERIOD) : new Date(LicenseUtils.getSupportPeriodEnd(license));
        this.setProperty(properties, "MaintenanceExpiryDate", DateEditor.getString((Date)maintenanceExpiryDate));
    }

    protected void setPurchaseDate(Properties properties, License license) {
        Date purchaseDate = license.getDatePurchased();
        log.debug((Object)("Decoded purchase date of <" + license + "> is <" + purchaseDate + ">"));
        this.setProperty(properties, "PurchaseDate", DateEditor.getString((Date)purchaseDate));
    }

    protected void setOrganisation(Properties properties, License license) {
        this.setProperty(properties, "Organisation", license.getOrganisation());
    }

    protected void setPartner(Properties properties, License license) {
        this.setProperty(properties, "PartnerName", license.getPartnerName());
    }

    protected void setMaximumNumberOfUsers(Properties properties, License license) {
        int users = license.getUsers();
        int usersProperty = users >= 10000 ? -1 : users;
        this.setProperty(properties, "NumberOfUsers", Integer.toString(usersProperty));
    }

    protected final void setProperty(Properties properties, String key, String value) {
        if (key != null && value != null) {
            log.debug((Object)("Setting property <" + key + "> to value <" + value + "> for " + this.product + "."));
            properties.setProperty(LicensePropertiesConstants.getKey((Product)this.product, (String)key), value);
        }
    }

    protected boolean isEvaluationLicense(License license) {
        return license.getLicenseType().isEvaluationLicenseType();
    }
}

