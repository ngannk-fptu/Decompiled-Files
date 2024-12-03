/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.fisheye.FisheyeLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.fisheye;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.fisheye.FisheyeLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultFisheyeLicense
extends DefaultProductLicense
implements FisheyeLicense {
    DefaultFisheyeLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
    }
}

