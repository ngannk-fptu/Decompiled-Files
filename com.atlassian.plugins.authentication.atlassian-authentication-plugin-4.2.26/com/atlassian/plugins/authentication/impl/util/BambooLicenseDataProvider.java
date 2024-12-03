/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.Product
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.extras.api.Product;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import java.util.function.Predicate;
import javax.inject.Inject;

@BambooComponent
public class BambooLicenseDataProvider
implements ProductLicenseDataProvider {
    public static final String DATA_CENTER_FAKE_PROPERTY_KEY = "com.atlassian.plugins.authentication.impl.web.LicenseFilter";
    public static final String DATA_CENTER_FAKE_PROPERTY_VALUE = "bypass";
    private final LicenseHandler licenseHandler;
    private final Predicate<SingleProductLicenseDetailsView> internalBambooPredicate = license -> DATA_CENTER_FAKE_PROPERTY_VALUE.equals(license.getProperty(DATA_CENTER_FAKE_PROPERTY_KEY));

    @Inject
    public BambooLicenseDataProvider(@ComponentImport LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    @Override
    public boolean isDataCenterProduct() {
        return this.internalBambooPredicate.test(this.licenseHandler.getProductLicenseDetails(Product.BAMBOO.getNamespace()));
    }

    @Override
    public boolean isServiceManagementProduct() {
        return false;
    }
}

