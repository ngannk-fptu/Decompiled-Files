/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.cache.CacheFactory;
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
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class BitbucketHostLicenseProvider
extends AbstractHostLicenseProvider {
    private final LicenseManagerProvider licenseManagerProvider;
    private final LicenseService licenseService;

    public BitbucketHostLicenseProvider(LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, CacheFactory cacheFactory, LicenseManagerProvider licenseManagerProvider, LicenseService licenseService, UpmAppManager appManager) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager, Option.some(cacheFactory));
        this.licenseManagerProvider = Objects.requireNonNull(licenseManagerProvider, "licenseManagerProvider");
        this.licenseService = Objects.requireNonNull(licenseService, "licenseService");
        this.setCachingEnabled(true);
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        String licenseString = this.licenseService.getAsString();
        if (!StringUtils.isBlank((CharSequence)licenseString)) {
            for (AtlassianLicense atlassianLicense : Option.option(this.licenseManagerProvider.getLicenseManager().getLicense(licenseString))) {
                Iterator<ProductLicense> iterator = Option.option(atlassianLicense.getProductLicense(Product.STASH)).iterator();
                if (!iterator.hasNext()) continue;
                ProductLicense productLicense = iterator.next();
                return Option.some(this.hostApplicationLicenseFactory.getHostLicense(productLicense, licenseString));
            }
        }
        return Option.none();
    }
}

