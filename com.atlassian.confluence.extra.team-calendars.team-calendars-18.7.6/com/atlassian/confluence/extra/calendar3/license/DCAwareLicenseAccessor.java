/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.license.ConfluenceLicenseHelper;
import com.atlassian.confluence.extra.calendar3.license.DCAwareLicenseVerifier;
import com.atlassian.confluence.extra.calendar3.license.LicenseAbstract;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.extra.calendar3.license.LicenseServiceProvider;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.license.compatibility.PluginLicenseManagerAccessor;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DCAwareLicenseAccessor
extends LicenseAccessor
implements DCAwareLicenseVerifier {
    private static Logger logger = LoggerFactory.getLogger(DCAwareLicenseAccessor.class);

    public DCAwareLicenseAccessor(ConfluenceLicenseHelper confluenceLicenseHelper, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, PluginLicenseManagerAccessor pluginLicenseManagerAccessor, EventPublisher eventPublisher, LicenseServiceProvider licenseServiceProvider, DocumentationBeanFactory documentationBeanFactory) {
        super(confluenceLicenseHelper, localeManager, i18NBeanFactory, buildInformationManager, jodaIcal4jTimeZoneMapper, pluginLicenseManagerAccessor, eventPublisher, licenseServiceProvider, documentationBeanFactory);
    }

    @Override
    public boolean isLicenseSetup() {
        boolean dcLicenseCheck = this.isUsingConfluenceDCLicense();
        if (!dcLicenseCheck) {
            return super.isLicenseSetup();
        }
        boolean isAllow = this.isLicensedForDataCenterOrExempt();
        if (!isAllow) {
            logger.debug("Fallback to normal isLicenseSetup");
            isAllow = super.isLicenseSetup();
        }
        return isAllow;
    }

    @Override
    public boolean isLicenseExpired() {
        boolean dcLicenseCheck = this.isUsingConfluenceDCLicense();
        if (!dcLicenseCheck) {
            return super.isLicenseExpired();
        }
        boolean isAllow = this.isLicensedForDataCenterOrExempt();
        if (!isAllow) {
            logger.debug("Fallback to normal isLicenseExpired");
            return super.isLicenseExpired();
        }
        return this.isConfluenceLicenseExpired();
    }

    public boolean isConfluenceLicenseExpired() {
        LicenseAbstract confluenceLicense = this.getConfluenceLicenseAbstract();
        return confluenceLicense.isExpired();
    }

    @Override
    public Collection<String> getInvalidLicenseReasons() {
        boolean dcLicenseCheck = this.isUsingConfluenceDCLicense();
        if (!dcLicenseCheck) {
            return super.getInvalidLicenseReasons();
        }
        boolean isAllow = this.isLicensedForDataCenterOrExempt();
        if (!isAllow) {
            logger.debug("Fallback to normal getInvalidLicenseReasons");
            return super.getInvalidLicenseReasons();
        }
        return Collections.emptySet();
    }

    private boolean isLicensedForDataCenterOrExempt() {
        LicenseService licenseService = this.licenseServiceProvider.getLicenseService();
        if (licenseService == null) {
            logger.error("Could not obtain LicenseService from Confluence. Could not check license");
            return false;
        }
        return licenseService.isLicensedForDataCenterOrExempt();
    }

    @Override
    public boolean isUsingConfluenceDCLicense() {
        logger.debug("Using Confluence DC license for TC license check [{}]", (Object)true);
        return true;
    }
}

