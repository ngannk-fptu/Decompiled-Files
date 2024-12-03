/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.cenqua.fisheye.AppConfig
 *  com.cenqua.fisheye.config1.ConfigDocument
 *  com.cenqua.fisheye.config1.LicenseType
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.AbstractHostLicenseProvider;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.LicenseType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FecruHostLicenseProvider
extends AbstractHostLicenseProvider {
    private static final Logger log = LoggerFactory.getLogger(FecruHostLicenseProvider.class);
    private final ConfigDocument configDoc = AppConfig.getsConfig().getConfigDocument();
    private final LicenseManagerProvider licenseManagerProvider;

    public FecruHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager);
        this.licenseManagerProvider = Objects.requireNonNull(licenseManagerProvider, "licenseManagerProvider");
    }

    @Override
    protected Iterable<HostApplicationLicense> getHostLicensesInternal() {
        ArrayList<HostApplicationLicense> builder = new ArrayList<HostApplicationLicense>();
        LicenseType licenseType = this.configDoc.getConfig().getLicense();
        String crucibleLicString = licenseType.getCrucible();
        String fisheyeLicString = licenseType.getFisheye();
        this.addProductLicense(builder, crucibleLicString, Product.CRUCIBLE);
        this.addProductLicense(builder, fisheyeLicString, Product.FISHEYE);
        return Collections.unmodifiableList(builder);
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        throw new UnsupportedOperationException("FeCru supports multiple licenses");
    }

    private void addProductLicense(List<HostApplicationLicense> builder, String licenseString, Product product) {
        try {
            if (this.isValid(licenseString)) {
                for (AtlassianLicense masterLicense : Option.option(this.licenseManagerProvider.getLicenseManager().getLicense(licenseString))) {
                    for (ProductLicense pl : Option.option(masterLicense.getProductLicense(product))) {
                        builder.add(this.hostApplicationLicenseFactory.getHostLicense(pl, licenseString));
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn("Unexpected error decoding stored license: " + licenseString);
            log.debug("", (Throwable)e);
        }
    }

    private boolean isValid(String licenseString) {
        return !StringUtils.isBlank((CharSequence)licenseString) && !licenseString.trim().equalsIgnoreCase("Disabled");
    }
}

