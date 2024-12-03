/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.api;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import java.util.Collection;

public interface AtlassianLicense {
    public Collection<ProductLicense> getProductLicenses();

    public ProductLicense getProductLicense(Product var1);
}

