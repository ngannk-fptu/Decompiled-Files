/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 *  com.atlassian.extras.common.util.ProductLicenseProperties
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.common.util.ProductLicenseProperties;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultAtlassianLicense;
import com.atlassian.extras.core.ProductLicenseFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class DefaultAtlassianLicenseFactory
implements AtlassianLicenseFactory {
    private final Map<Product, ProductLicenseFactory> productLicenseFactoryMap;

    public DefaultAtlassianLicenseFactory(Map<Product, ProductLicenseFactory> productLicenseFactoryMap) {
        this.productLicenseFactoryMap = Collections.unmodifiableMap(new HashMap<Product, ProductLicenseFactory>(productLicenseFactoryMap));
    }

    @Override
    public AtlassianLicense getLicense(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("licenceProperties must NOT be null!");
        }
        ArrayList<ProductLicense> productLicenses = new ArrayList<ProductLicense>();
        for (Map.Entry<Product, ProductLicenseFactory> entry : this.productLicenseFactoryMap.entrySet()) {
            ProductLicenseProperties productLicenseProperties;
            Product product = entry.getKey();
            ProductLicenseFactory licenseFactory = entry.getValue();
            if (!licenseFactory.hasLicense(product, (LicenseProperties)(productLicenseProperties = new ProductLicenseProperties(product, properties)))) continue;
            productLicenses.add(licenseFactory.getLicense(product, (LicenseProperties)productLicenseProperties));
        }
        return new DefaultAtlassianLicense(productLicenses);
    }
}

