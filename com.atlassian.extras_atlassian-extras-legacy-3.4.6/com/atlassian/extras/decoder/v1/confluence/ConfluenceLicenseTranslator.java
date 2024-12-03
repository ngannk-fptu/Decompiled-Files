/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 */
package com.atlassian.extras.decoder.v1.confluence;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.decoder.v1.DefaultLicenseTranslator;
import com.atlassian.license.License;
import java.util.Properties;

@Deprecated
public class ConfluenceLicenseTranslator
extends DefaultLicenseTranslator {
    public ConfluenceLicenseTranslator(Product product) {
        super(product);
        if (!product.equals((Object)Product.CONFLUENCE)) {
            throw new IllegalStateException("product must be " + Product.CONFLUENCE);
        }
    }

    @Override
    protected void setProperties(Properties properties, License license) {
        super.setProperties(properties, license);
        this.setMaximumClusterNodes(properties, license);
    }

    private void setMaximumClusterNodes(Properties properties, License license) {
        this.setProperty(properties, "NumberOfClusterNodes", Integer.toString(license.getPermittedClusteredNodes()));
    }
}

