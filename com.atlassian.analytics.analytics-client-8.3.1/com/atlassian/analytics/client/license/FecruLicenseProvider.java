/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.fecru.util.LicenseInfoService
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.exception.NoLicenseException;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.fecru.util.LicenseInfoService;
import java.util.Date;

public class FecruLicenseProvider
implements LicenseProvider {
    private final LicenseInfoService licenseInfoService;

    public FecruLicenseProvider(LicenseInfoService licenseInfoService) {
        this.licenseInfoService = licenseInfoService;
    }

    @Override
    public Date getLicenseCreationDate() throws NoLicenseException {
        ProductLicense fisheye = this.licenseInfoService.getFisheyeLicense();
        ProductLicense crucible = this.licenseInfoService.getCrucibleLicense();
        if (fisheye != null && crucible != null) {
            Date cruDate;
            Date feDate = fisheye.getCreationDate();
            return feDate.after(cruDate = crucible.getCreationDate()) ? feDate : cruDate;
        }
        if (fisheye != null) {
            return fisheye.getCreationDate();
        }
        if (crucible != null) {
            return crucible.getCreationDate();
        }
        throw new NoLicenseException();
    }
}

