/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 */
package com.atlassian.upm.license.internal;

import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.HostApplicationEmbeddedAddonLicense;
import com.atlassian.upm.license.internal.HostApplicationLicense;

public interface HostApplicationLicenseFactory {
    public HostApplicationLicense getHostLicense(ProductLicense var1, String var2);

    public HostApplicationLicense getHostLicense(SingleProductLicenseDetailsView var1, MultiProductLicenseDetails var2, String var3, String var4);

    public HostApplicationEmbeddedAddonLicense getEmbeddedAddonLicense(ProductLicense var1, Product var2, String var3, Option<Plugin> var4);
}

