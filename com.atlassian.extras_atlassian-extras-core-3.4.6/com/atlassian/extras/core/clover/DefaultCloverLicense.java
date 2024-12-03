/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.clover.CloverLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.clover;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.clover.CloverLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultCloverLicense
extends DefaultProductLicense
implements CloverLicense {
    DefaultCloverLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
    }
}

