/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseException
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.stash.StashLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.stash;

import com.atlassian.extras.api.LicenseException;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.stash.StashLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.AbstractProductLicenseFactory;
import com.atlassian.extras.core.stash.DefaultStashLicense;

public class StashProductLicenseFactory
extends AbstractProductLicenseFactory {
    protected StashLicense getLicenseInternal(Product product, LicenseProperties licenseProperties) {
        if (Product.STASH.equals((Object)product)) {
            return new DefaultStashLicense(product, licenseProperties);
        }
        throw new LicenseException("Could not create license for " + product);
    }
}

