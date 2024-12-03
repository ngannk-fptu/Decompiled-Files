/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.api.Product
 */
package com.atlassian.upm.license.internal;

import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.api.Product;

public interface LicenseManagerProvider {
    public LicenseManager getLicenseManager();

    public LicenseManager registerPlugin(Product var1);
}

