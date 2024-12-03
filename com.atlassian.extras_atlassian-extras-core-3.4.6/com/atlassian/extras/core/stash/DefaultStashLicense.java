/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.stash.StashLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.stash;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.stash.StashLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultStashLicense
extends DefaultProductLicense
implements StashLicense {
    DefaultStashLicense(Product product, LicenseProperties properties) {
        super(product, properties);
    }
}

