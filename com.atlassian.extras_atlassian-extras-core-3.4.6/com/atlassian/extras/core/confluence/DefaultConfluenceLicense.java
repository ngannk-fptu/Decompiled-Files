/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.confluence;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultConfluenceLicense
extends DefaultProductLicense
implements ConfluenceLicense {
    private final int maximumNumberClusterNodes;

    DefaultConfluenceLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
        int maxClustNodes = licenseProperties.getInt("NumberOfClusterNodes", 0);
        this.maximumNumberClusterNodes = maxClustNodes != -1 ? maxClustNodes : Integer.MAX_VALUE;
    }

    public int getMaximumNumberOfClusterNodes() {
        return this.maximumNumberClusterNodes;
    }
}

