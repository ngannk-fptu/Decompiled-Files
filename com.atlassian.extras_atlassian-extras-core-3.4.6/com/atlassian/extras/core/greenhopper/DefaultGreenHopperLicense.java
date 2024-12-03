/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseEdition
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.greenhopper.GreenHopperLicense
 *  com.atlassian.extras.common.LicenseTypeAndEditionResolver
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.greenhopper;

import com.atlassian.extras.api.LicenseEdition;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.greenhopper.GreenHopperLicense;
import com.atlassian.extras.common.LicenseTypeAndEditionResolver;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.plugins.DefaultPluginLicense;

class DefaultGreenHopperLicense
extends DefaultPluginLicense
implements GreenHopperLicense {
    private final LicenseEdition licenseEdition;

    DefaultGreenHopperLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
        String editionProperty = licenseProperties.getProperty("LicenseEdition");
        this.licenseEdition = LicenseTypeAndEditionResolver.getLicenseEdition((String)editionProperty);
    }

    public LicenseEdition getLicenseEdition() {
        return this.licenseEdition;
    }
}

