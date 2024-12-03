/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.bamboo.BambooLicense
 *  com.atlassian.extras.common.LicenseTypeAndEditionResolver
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.bamboo;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.bamboo.BambooLicense;
import com.atlassian.extras.common.LicenseTypeAndEditionResolver;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;
import com.atlassian.extras.core.bamboo.BambooLicenseAttributes;

class DefaultBambooLicense
extends DefaultProductLicense
implements BambooLicense {
    private final int maximumNumberOfRemoteAgents;
    private final int maximumNumberOfLocalAgents;
    private final int maximumNumberOfPlans;
    private final LicenseEdition licenseEdition;

    DefaultBambooLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
        this.licenseEdition = LicenseTypeAndEditionResolver.getLicenseEdition((String)licenseProperties.getProperty("LicenseEdition"));
        Integer value = BambooLicenseAttributes.extractValue(licenseProperties, "NumberOfBambooRemoteAgents");
        this.maximumNumberOfRemoteAgents = value == null ? BambooLicenseAttributes.calculateRemoteAgents(this.getLicenseType(), this.licenseEdition) : value;
        value = BambooLicenseAttributes.extractValue(licenseProperties, "NumberOfBambooLocalAgents");
        this.maximumNumberOfLocalAgents = value == null ? BambooLicenseAttributes.calculateLocalAgents(this.getLicenseType(), this.licenseEdition) : value;
        value = BambooLicenseAttributes.extractValue(licenseProperties, "NumberOfBambooPlans");
        this.maximumNumberOfPlans = value == null ? BambooLicenseAttributes.calculatePlans(this.getLicenseType()) : value;
    }

    public LicenseEdition getLicenseEdition() {
        return this.licenseEdition;
    }

    public int getMaximumNumberOfRemoteAgents() {
        return this.maximumNumberOfRemoteAgents;
    }

    public int getMaximumNumberOfLocalAgents() {
        return this.maximumNumberOfLocalAgents;
    }

    public int getMaximumNumberOfPlans() {
        return this.maximumNumberOfPlans;
    }

    public boolean isUnlimitedRemoteAgents() {
        return -1 == this.maximumNumberOfRemoteAgents;
    }

    public boolean isUnlimitedLocalAgents() {
        return -1 == this.maximumNumberOfLocalAgents;
    }

    public boolean isUnlimitedPlans() {
        return -1 == this.maximumNumberOfPlans;
    }
}

