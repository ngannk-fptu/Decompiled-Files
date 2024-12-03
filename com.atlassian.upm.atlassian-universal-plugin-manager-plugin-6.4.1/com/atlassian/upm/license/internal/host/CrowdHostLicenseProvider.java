/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.sal.api.license.LicenseHandler
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.AbstractHostLicenseProvider;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class CrowdHostLicenseProvider
extends AbstractHostLicenseProvider {
    private static final String CFG_LICENSE = "license";
    private final AtlassianBootstrapManager atlassianBootstrapManager;
    private final LicenseManagerProvider licenseManagerProvider;

    public CrowdHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager, AtlassianBootstrapManager atlassianBootstrapManager, LicenseManagerProvider licenseManagerProvider) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager);
        this.atlassianBootstrapManager = Objects.requireNonNull(atlassianBootstrapManager, "atlassianBootstrapManager");
        this.licenseManagerProvider = Objects.requireNonNull(licenseManagerProvider, "licenseManagerProvider");
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        String licenseString = (String)this.atlassianBootstrapManager.getProperty(CFG_LICENSE);
        return Optional.ofNullable(licenseString).filter(CrowdHostLicenseProvider::isNotBlank).map(licenseStr -> this.licenseManagerProvider.getLicenseManager().getLicense(licenseString)).map(atlassianLicense -> atlassianLicense.getProductLicense(Product.CROWD)).map(productLicense -> this.hostApplicationLicenseFactory.getHostLicense((ProductLicense)productLicense, licenseString)).map(Option::some).orElseGet(Option::none);
    }

    private static boolean isNotBlank(@Nullable String string) {
        return !Objects.isNull(string) && !string.trim().isEmpty();
    }
}

