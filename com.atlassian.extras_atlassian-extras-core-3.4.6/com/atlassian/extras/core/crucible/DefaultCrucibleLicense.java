/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.crucible.CrucibleLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.crucible;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.crucible.CrucibleLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

class DefaultCrucibleLicense
extends DefaultProductLicense
implements CrucibleLicense {
    DefaultCrucibleLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
    }
}

