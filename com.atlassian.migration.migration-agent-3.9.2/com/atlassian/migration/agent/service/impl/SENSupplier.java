/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.service.catalogue.model.ConfluenceLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;

@ParametersAreNonnullByDefault
public class SENSupplier
implements Supplier<String> {
    private final String sen;
    private final SortedSet<String> sens;
    private final LicenseHandler licenseHandler;

    public SENSupplier(LicenseHandler licenseHandler) {
        this.sens = licenseHandler.getAllSupportEntitlementNumbers();
        this.licenseHandler = licenseHandler;
        this.sen = this.sens.stream().findFirst().orElse("");
    }

    @Override
    public String get() {
        return this.sen;
    }

    public ConfluenceLicenseDetails getLicenseDetails() {
        SingleProductLicenseDetailsView singleProductLicenseDetailsView = this.licenseHandler.getProductLicenseDetails("conf");
        return new ConfluenceLicenseDetails(singleProductLicenseDetailsView != null ? Integer.valueOf(singleProductLicenseDetailsView.getNumberOfUsers()) : null);
    }

    @Generated
    public SortedSet<String> getSens() {
        return this.sens;
    }
}

