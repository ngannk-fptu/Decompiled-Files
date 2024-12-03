/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.extras.api.AtlassianLicense
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.jira.license.JiraLicenseManager
 *  com.atlassian.jira.license.LicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.license.internal.host;

import com.atlassian.cache.CacheFactory;
import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.jira.license.JiraLicenseManager;
import com.atlassian.jira.license.LicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.license.internal.HostApplicationLicense;
import com.atlassian.upm.license.internal.HostApplicationLicenseFactory;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import com.atlassian.upm.license.internal.host.AbstractHostLicenseProvider;
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class JiraHostLicenseProvider
extends AbstractHostLicenseProvider {
    private final JiraLicenseManager jiraLicenseManager;
    private final LicenseManagerProvider licenseManagerProvider;

    public JiraHostLicenseProvider(JiraLicenseManager jiraLicenseManager, LicenseManagerProvider licenseManagerProvider, LicenseHandler licenseHandler, HostApplicationLicenseFactory hostApplicationLicenseFactory, UpmAppManager appManager, CacheFactory cacheFactory) {
        super(licenseHandler, hostApplicationLicenseFactory, appManager, Option.some(cacheFactory));
        this.jiraLicenseManager = Objects.requireNonNull(jiraLicenseManager, "jiraLicenseManager");
        this.licenseManagerProvider = Objects.requireNonNull(licenseManagerProvider, "licenseManagerProvider");
        this.setCachingEnabled(true);
    }

    @Override
    protected Option<HostApplicationLicense> getSingleHostLicenseInternal() {
        for (LicenseDetails details : this.getLicense()) {
            String licenseString = details.getLicenseString();
            if (StringUtils.isBlank((CharSequence)licenseString)) continue;
            for (AtlassianLicense masterLicense : Option.option(this.licenseManagerProvider.getLicenseManager().getLicense(licenseString))) {
                Iterator<ProductLicense> iterator = Option.option(masterLicense.getProductLicense(Product.JIRA)).iterator();
                if (!iterator.hasNext()) continue;
                ProductLicense productLicense = iterator.next();
                return Option.some(this.hostApplicationLicenseFactory.getHostLicense(productLicense, licenseString));
            }
        }
        return Option.none();
    }

    private Option<LicenseDetails> getLicense() {
        return Iterables.toOption(this.jiraLicenseManager.getLicenses());
    }
}

