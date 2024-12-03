/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.extras.api.ProductLicense;
import com.atlassian.upm.license.compatibility.CompatibleLicenseStatus;
import java.net.URI;

public interface CompatiblePluginLicenseManager {
    public ProductLicense setLicense(String var1);

    public ProductLicense getCurrentLicense();

    public ProductLicense getLicense(String var1);

    public CompatibleLicenseStatus getCurrentLicenseStatus();

    public CompatibleLicenseStatus getLicenseStatus(String var1);

    public URI getPluginLicenseAdministrationUri();
}

