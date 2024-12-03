/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.core.AtlassianLicenseFactory
 *  com.atlassian.extras.core.DefaultLicenseManager
 *  com.atlassian.extras.decoder.api.LicenseDecoder
 *  com.atlassian.extras.decoder.v2.Version2LicenseDecoder
 *  com.atlassian.license.LicenseManager
 *  com.atlassian.license.LicenseRegistry
 *  com.atlassian.license.LicenseTypeStore
 *  com.atlassian.license.applications.confluence.ConfluenceLicenseTypeStore
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.license;

import com.atlassian.confluence.setup.ConfluenceLicenseRegistry;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultLicenseManager;
import com.atlassian.extras.decoder.api.LicenseDecoder;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.license.LicenseManager;
import com.atlassian.license.LicenseRegistry;
import com.atlassian.license.LicenseTypeStore;
import com.atlassian.license.applications.confluence.ConfluenceLicenseTypeStore;
import org.springframework.beans.factory.FactoryBean;

public class LicenseManagerFactoryBean
implements FactoryBean {
    private final AtlassianLicenseFactory atlassianLicenseFactory;

    public LicenseManagerFactoryBean(AtlassianLicenseFactory atlassianLicenseFactory) {
        this.atlassianLicenseFactory = atlassianLicenseFactory;
    }

    public Object getObject() throws Exception {
        LicenseManager.getInstance().addLicenseConfiguration("CONF", (LicenseTypeStore)new ConfluenceLicenseTypeStore(), (LicenseRegistry)new ConfluenceLicenseRegistry());
        Version2LicenseDecoder licenseDecoder = new Version2LicenseDecoder(true, true);
        return new DefaultLicenseManager((LicenseDecoder)licenseDecoder, this.atlassianLicenseFactory);
    }

    public Class getObjectType() {
        return com.atlassian.extras.api.LicenseManager.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

