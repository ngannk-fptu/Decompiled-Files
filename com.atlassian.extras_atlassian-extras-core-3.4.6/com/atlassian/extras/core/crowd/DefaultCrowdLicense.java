/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.crowd.CrowdLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.crowd;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.crowd.CrowdLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultCrowdLicense
extends DefaultProductLicense
implements CrowdLicense {
    DefaultCrowdLicense(Product product, LicenseProperties properties) {
        super(product, properties);
    }
}

