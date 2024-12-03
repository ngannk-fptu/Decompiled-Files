/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.LicenseException
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.bamboo;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.AbstractProductLicenseFactory;
import com.atlassian.extras.core.bamboo.DefaultBambooLicense;
import com.atlassian.extras.core.transformer.DelegatingLicensePropertiesTransformer;
import com.atlassian.extras.core.transformer.LicenseEditionPropertyTransformer;
import com.atlassian.extras.core.transformer.LicensePropertiesTransformer;
import com.atlassian.extras.core.transformer.Version1LicenseTypeTransformer;
import java.util.Arrays;

public class BambooProductLicenseFactory
extends AbstractProductLicenseFactory {
    private final LicensePropertiesTransformer transformer = new DelegatingLicensePropertiesTransformer(Arrays.asList(new Version1LicenseTypeTransformer(), new LicenseEditionPropertyTransformer()));

    @Override
    protected LicensePropertiesTransformer getTransformer() {
        return this.transformer;
    }

    @Override
    public ProductLicense getLicenseInternal(Product product, LicenseProperties licenseProperties) {
        if (Product.BAMBOO.equals((Object)product)) {
            return new DefaultBambooLicense(product, licenseProperties);
        }
        throw new LicenseException("Could not create license for " + product);
    }
}

