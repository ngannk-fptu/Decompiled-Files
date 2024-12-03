/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu.impl;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.zdu.LicenseService;
import java.util.Objects;
import javax.annotation.Nonnull;

public class LicenseServiceImpl
implements LicenseService {
    private final LicenseHandler licenseHandler;

    public LicenseServiceImpl(@Nonnull LicenseHandler licenseHandler) {
        this.licenseHandler = Objects.requireNonNull(licenseHandler);
    }

    @Override
    public boolean isDataCenter() {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(BaseLicenseDetails::isDataCenter);
    }
}

