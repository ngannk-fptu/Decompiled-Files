/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.fisheye;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.AbstractProductLicenseFactory;
import com.atlassian.extras.core.fisheye.DefaultFisheyeLicense;

public class FisheyeProductLicenseFactory
extends AbstractProductLicenseFactory {
    @Override
    public ProductLicense getLicenseInternal(Product product, LicenseProperties licenseProperties) {
        if (Product.FISHEYE.equals((Object)product)) {
            return new DefaultFisheyeLicense(product, licenseProperties);
        }
        throw new LicenseException("Could not create license for " + product);
    }
}

