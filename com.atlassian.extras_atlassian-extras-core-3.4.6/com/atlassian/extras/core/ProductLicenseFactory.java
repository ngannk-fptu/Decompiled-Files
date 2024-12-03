/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.common.util.LicenseProperties;

public interface ProductLicenseFactory {
    public boolean hasLicense(Product var1, LicenseProperties var2);

    public ProductLicense getLicense(Product var1, LicenseProperties var2);
}

