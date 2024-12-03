/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseException
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.bitbucket.BitbucketServerLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.bitbucket;

import com.atlassian.extras.api.LicenseException;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.bitbucket.BitbucketServerLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.AbstractProductLicenseFactory;
import com.atlassian.extras.core.bitbucket.DefaultBitbucketServerLicense;

public class BitbucketServerProductLicenseFactory
extends AbstractProductLicenseFactory {
    protected BitbucketServerLicense getLicenseInternal(Product product, LicenseProperties licenseProperties) {
        if (Product.BITBUCKET_SERVER.equals((Object)product)) {
            return new DefaultBitbucketServerLicense(product, licenseProperties);
        }
        throw new LicenseException("Could not create license for " + product);
    }
}

