/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.ProductLicenseFactory;
import com.atlassian.extras.core.transformer.LicensePropertiesTransformer;
import com.atlassian.extras.core.transformer.Version1LicenseTypeTransformer;

public abstract class AbstractProductLicenseFactory
implements ProductLicenseFactory {
    private final LicensePropertiesTransformer transformer = new Version1LicenseTypeTransformer();

    @Override
    public boolean hasLicense(Product product, LicenseProperties licenseProperties) {
        return "true".equals(licenseProperties.getProperty("active"));
    }

    @Override
    public final ProductLicense getLicense(Product product, LicenseProperties licenseProperties) {
        return this.getLicenseInternal(product, this.getTransformer().transform(product, licenseProperties));
    }

    protected abstract ProductLicense getLicenseInternal(Product var1, LicenseProperties var2);

    protected LicensePropertiesTransformer getTransformer() {
        return this.transformer;
    }
}

