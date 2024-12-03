/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicensePair;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.AbstractHostLicenseProvider;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BambooHostLicenseProvider
extends AbstractHostLicenseProvider {
    private static final Logger logger = LoggerFactory.getLogger(BambooHostLicenseProvider.class);
    static final String LICENSE_HASH = "license.hash";
    static final String LICENSE_MESSAGE = "license.message";
    static final String LICENSE_STRING = "license.string";
    private final AtlassianBootstrapManager bootstrapManager;
    private final LicenseManagerProvider licenseManagerProvider;

    public BambooHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        this(licenseHandler, hostApplicationLicenseFactory, BootstrapUtils.getBootstrapManager(), licenseManagerProvider, appManager);
    }

    BambooHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, AtlassianBootstrapManager bootstrapManager, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager);
        this.bootstrapManager = bootstrapManager;
        this.licenseManagerProvider = licenseManagerProvider;
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        for (String licenseString : this.getLicenseString()) {
            AtlassianLicense masterLicense = this.licenseManagerProvider.getLicenseManager().getLicense(licenseString);
            Iterator<ProductLicense> iterator = Option.option(masterLicense.getProductLicense(Product.BAMBOO)).iterator();
            if (!iterator.hasNext()) continue;
            ProductLicense hostLicense = iterator.next();
            return Option.some(this.hostApplicationLicenseFactory.getHostLicense(hostLicense, licenseString));
        }
        return Option.none();
    }

    private Option<String> getLicenseString() {
        String licenseString = (String)this.bootstrapManager.getProperty(LICENSE_STRING);
        if (StringUtils.isBlank((CharSequence)licenseString)) {
            String message = (String)this.bootstrapManager.getProperty(LICENSE_MESSAGE);
            String hash = (String)this.bootstrapManager.getProperty(LICENSE_HASH);
            if (StringUtils.isBlank((CharSequence)message) || StringUtils.isBlank((CharSequence)hash)) {
                logger.debug("Host product is currently unlicensed.");
                return Option.none();
            }
            try {
                LicensePair licPair = new LicensePair(message, hash);
                licenseString = licPair.getOriginalLicenseString();
            }
            catch (LicenseException e) {
                logger.error("Error getting product license", (Throwable)e);
                return Option.none();
            }
        }
        return Option.option(licenseString);
    }
}

