/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.setup.ConfluenceLicenseRegistry
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.setup.ConfluenceLicenseRegistry;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
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
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceHostLicenseProvider
extends AbstractHostLicenseProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceHostLicenseProvider.class);
    private final ConfluenceLicenseRegistry licenseRegistry;
    private final LicenseManagerProvider licenseManagerProvider;

    public ConfluenceHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, CacheFactory cacheFactory, LicenseManagerProvider licenseManagerProvider, UpmAppManager appManager) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager, Option.some(cacheFactory));
        this.licenseManagerProvider = Objects.requireNonNull(licenseManagerProvider, "licenseManagerProvider");
        this.licenseRegistry = new ConfluenceLicenseRegistry();
        this.setCachingEnabled(true);
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        try {
            String message = this.licenseRegistry.getLicenseMessage();
            String hash = this.licenseRegistry.getLicenseHash();
            if (StringUtils.isBlank((CharSequence)message) || StringUtils.isBlank((CharSequence)hash)) {
                logger.debug("Host product is currently unlicensed.");
                return Option.none();
            }
            LicensePair licPair = new LicensePair(message, hash);
            String licenseString = licPair.getOriginalLicenseString();
            Iterator<AtlassianLicense> iterator = this.parseLicense(licenseString).iterator();
            if (iterator.hasNext()) {
                AtlassianLicense masterLicense = iterator.next();
                return Option.some(this.hostApplicationLicenseFactory.getHostLicense(masterLicense.getProductLicense(Product.CONFLUENCE), licenseString));
            }
        }
        catch (LicenseException e) {
            logger.error("Error getting product license", (Throwable)e);
        }
        return Option.none();
    }

    private Option<AtlassianLicense> parseLicense(String licenseString) {
        try {
            return Option.option(this.licenseManagerProvider.getLicenseManager().getLicense(licenseString));
        }
        catch (Exception e) {
            logger.warn("Error parsing license: " + e);
            return Option.none();
        }
    }
}

