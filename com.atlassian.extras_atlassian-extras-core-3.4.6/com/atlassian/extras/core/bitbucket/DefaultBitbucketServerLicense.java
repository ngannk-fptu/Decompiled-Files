/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.bitbucket.BitbucketServerLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.bitbucket;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.bitbucket.BitbucketServerLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultBitbucketServerLicense
extends DefaultProductLicense
implements BitbucketServerLicense {
    DefaultBitbucketServerLicense(Product product, LicenseProperties properties) {
        super(product, properties);
    }
}

