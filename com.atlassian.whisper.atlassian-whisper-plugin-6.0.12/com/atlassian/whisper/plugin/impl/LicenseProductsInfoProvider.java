/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class LicenseProductsInfoProvider {
    private final LicenseHandler licenseHandler;

    @Inject
    public LicenseProductsInfoProvider(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public boolean isProductLicensed(String productKey) {
        try {
            return this.licenseHandler.getProductLicenseDetails(productKey) != null;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Nullable
    public String getProductSEN(String productKey) {
        try {
            return Optional.ofNullable(this.licenseHandler.getProductLicenseDetails(productKey)).map(BaseLicenseDetails::getSupportEntitlementNumber).orElse(null);
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    public Instant getMaintenanceExpiryDate(String productKey) {
        return this.getLicenseDate(productKey, BaseLicenseDetails::getMaintenanceExpiryDate);
    }

    @Nullable
    public Instant getLicenseExpiryDate(String productKey) {
        return this.getLicenseDate(productKey, BaseLicenseDetails::getLicenseExpiryDate);
    }

    @Nullable
    private Instant getLicenseDate(String productKey, Function<BaseLicenseDetails, Date> dateExtractor) {
        try {
            return Optional.ofNullable(this.licenseHandler.getProductLicenseDetails(productKey)).map(dateExtractor).map(Date::toInstant).orElse(null);
        }
        catch (Exception ex) {
            return null;
        }
    }
}

