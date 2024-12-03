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

public interface LicensePropertiesTransformer {
    public LicenseProperties transform(Product var1, LicenseProperties var2);
}

