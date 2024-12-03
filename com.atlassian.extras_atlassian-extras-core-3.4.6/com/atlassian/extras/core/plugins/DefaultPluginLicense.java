/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.plugin.PluginLicense
 *  com.atlassian.extras.common.util.LicenseProperties
 */
package com.atlassian.extras.core.plugins;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.plugin.PluginLicense;
import com.atlassian.extras.common.util.LicenseProperties;
import com.atlassian.extras.core.DefaultProductLicense;

public class DefaultPluginLicense
extends DefaultProductLicense
implements PluginLicense {
    public DefaultPluginLicense(Product product, LicenseProperties licenseProperties) {
        super(product, licenseProperties);
    }
}

