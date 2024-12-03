/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.transformer;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.transformer.LicensePropertiesTransformer;
import java.util.ArrayList;
import java.util.List;

public class DelegatingLicensePropertiesTransformer
implements LicensePropertiesTransformer {
    private final List<LicensePropertiesTransformer> delegates;

    public DelegatingLicensePropertiesTransformer(List<LicensePropertiesTransformer> delegates) {
        if (delegates == null) {
            throw new IllegalArgumentException("delegates can not be null");
        }
        this.delegates = new ArrayList<LicensePropertiesTransformer>(delegates);
    }

    @Override
    public LicenseProperties transform(Product product, LicenseProperties licenseProperties) {
        for (LicensePropertiesTransformer transformer : this.delegates) {
            licenseProperties = transformer.transform(product, licenseProperties);
        }
        return licenseProperties;
    }
}

