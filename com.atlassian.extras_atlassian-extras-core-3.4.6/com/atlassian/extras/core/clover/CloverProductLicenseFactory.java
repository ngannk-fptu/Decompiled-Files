/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.clover;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.AbstractProductLicenseFactory;
import com.atlassian.extras.core.clover.DefaultCloverLicense;

public class CloverProductLicenseFactory
extends AbstractProductLicenseFactory {
    @Override
    public ProductLicense getLicenseInternal(Product product, LicenseProperties licenseProperties) {
        if (Product.CLOVER.equals((Object)product)) {
            return new DefaultCloverLicense(product, licenseProperties);
        }
        throw new LicenseException("Could not create license for " + product);
    }
}

