/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.license.LicenseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.license.ConfluenceLicenseHelper;
import com.atlassian.confluence.extra.calendar3.license.LicenseAbstract;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.extra.calendar3.license.LicenseServiceProvider;
import com.atlassian.confluence.extra.calendar3.license.UserCountCache;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.license.LicenseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.upm.license.compatibility.CompatibleLicenseStatus;
import com.atlassian.upm.license.compatibility.LegacyCompatiblePluginLicenseManager;
import java.net.URI;
import org.apache.commons.lang.StringUtils;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class DefaultPluginLicenseManager
implements LegacyCompatiblePluginLicenseManager {
    private static final String LICENSE_KEY = LicenseAccessor.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPluginLicenseManager.class);
    private final ConfluenceLicenseHelper confluenceLicenseHelper;
    private final BandanaManager bandanaManager;
    private final BuildInformationManager buildInformationManager;
    private final LocaleManager localeManager;
    private final LicenseServiceProvider licenseServiceProvider;
    private final UserCountCache userCountCache;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;

    @Autowired
    public DefaultPluginLicenseManager(ConfluenceLicenseHelper confluenceLicenseHelper, @ComponentImport BandanaManager bandanaManager, BuildInformationManager buildInformationManager, @ComponentImport LocaleManager localeManager, UserCountCache userCountCache, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, LicenseServiceProvider licenseServiceProvider) {
        this.confluenceLicenseHelper = confluenceLicenseHelper;
        this.bandanaManager = bandanaManager;
        this.buildInformationManager = buildInformationManager;
        this.localeManager = localeManager;
        this.userCountCache = userCountCache;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.licenseServiceProvider = licenseServiceProvider;
    }

    @Override
    public ProductLicense setLicense(String rawLicense) {
        if (rawLicense == null) {
            this.bandanaManager.removeValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, LICENSE_KEY);
            return null;
        }
        ProductLicense license = this.getLicense(rawLicense);
        if (license != null) {
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, LICENSE_KEY, (Object)rawLicense);
        }
        return license;
    }

    @Override
    public ProductLicense getCurrentLicense() {
        LicenseAbstract confluenceLicenseAbstract = this.getConfluenceLicenseAbstract();
        if (this.isPiggyBackingOnConfluenceEvaluationLicense(confluenceLicenseAbstract) || !this.isLicenseSetup()) {
            return null;
        }
        return this.getLicenseService().validate(this.getCalendarPluginLicenseString(), Product.TEAM_CALENDARS);
    }

    @Override
    public ProductLicense getLicense(String rawLicense) {
        ProductLicense tcLicense = null;
        try {
            tcLicense = this.getLicenseService().validate(StringUtils.defaultString(rawLicense), Product.TEAM_CALENDARS);
        }
        catch (Exception licenseError) {
            LOG.error("Unable to read Team Calendars license string", (Throwable)licenseError);
        }
        return tcLicense;
    }

    @Override
    public CompatibleLicenseStatus getCurrentLicenseStatus() {
        if (!this.isLicenseSetup()) {
            return CompatibleLicenseStatus.INACTIVE;
        }
        return this.getInvalidLicenseReasonsInternal(this.getLicenseAbstract());
    }

    @Override
    public CompatibleLicenseStatus getLicenseStatus(String rawLicense) {
        ProductLicense license = this.getLicense(rawLicense);
        if (license == null) {
            return CompatibleLicenseStatus.INVALID;
        }
        LicenseAbstract calendarPluginLicenseAbstract = new LicenseAbstract(license, this.jodaIcal4jTimeZoneMapper, this.localeManager);
        return this.getInvalidLicenseReasonsInternal(calendarPluginLicenseAbstract);
    }

    @Override
    public URI getPluginLicenseAdministrationUri() {
        return URI.create("/admin/calendar/viewlicense.action");
    }

    private LicenseAbstract getLicenseAbstract() {
        ProductLicense tcLicense = null;
        LicenseAbstract confluenceLicenseAbstract = this.getConfluenceLicenseAbstract();
        if (this.isPiggyBackingOnConfluenceEvaluationLicense(confluenceLicenseAbstract)) {
            return confluenceLicenseAbstract;
        }
        if (this.isLicenseSetup()) {
            tcLicense = this.getLicenseService().validate(this.getCalendarPluginLicenseString(), Product.TEAM_CALENDARS);
        }
        return new LicenseAbstract(tcLicense, this.jodaIcal4jTimeZoneMapper, this.localeManager);
    }

    private LicenseService getLicenseService() {
        return this.licenseServiceProvider.getLicenseService();
    }

    private String getCalendarPluginLicenseString() {
        return this.getStoredLicense();
    }

    private String getStoredLicense() {
        LOG.debug("Getting license from Bandana");
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, LICENSE_KEY);
    }

    private boolean isLicenseSetup() {
        return this.isPiggyBackingOnConfluenceEvaluationLicense() || StringUtils.isNotBlank(this.getStoredLicense());
    }

    private CompatibleLicenseStatus getInvalidLicenseReasonsInternal(LicenseAbstract calendarPluginLicenseAbstract) {
        String calendarPluginLicenseType = calendarPluginLicenseAbstract.getLicenseType();
        if (null == calendarPluginLicenseType) {
            return CompatibleLicenseStatus.INVALID;
        }
        LicenseAbstract confluenceLicenseAbstract = this.getConfluenceLicenseAbstract();
        String confluenceLicenseType = confluenceLicenseAbstract.getLicenseType();
        if (!(calendarPluginLicenseAbstract.isEvaluation() || confluenceLicenseAbstract.isEvaluation() || StringUtils.equals(calendarPluginLicenseType, confluenceLicenseType))) {
            return CompatibleLicenseStatus.INCOMPATIBLE_TYPE;
        }
        int maxActiveUsersAllowed = calendarPluginLicenseAbstract.getNumberOfUserLicensed();
        int activeUsers = this.userCountCache.getActiveUserCount();
        if (-1 != maxActiveUsersAllowed && activeUsers > maxActiveUsersAllowed) {
            return CompatibleLicenseStatus.USER_MISMATCH;
        }
        if (calendarPluginLicenseAbstract.isExpired()) {
            return calendarPluginLicenseAbstract.isEvaluation() ? CompatibleLicenseStatus.EVALUATION_EXPIRED : CompatibleLicenseStatus.EXPIRED;
        }
        if (calendarPluginLicenseAbstract.isMaintenanceExpired() && this.buildInformationManager.getBuildDate().isAfter((ReadableInstant)calendarPluginLicenseAbstract.getMaintenanceExpiry())) {
            return CompatibleLicenseStatus.MAINTENANCE_EXPIRED;
        }
        return calendarPluginLicenseAbstract.isEvaluation() ? CompatibleLicenseStatus.EVALUATION : CompatibleLicenseStatus.ACTIVE;
    }

    private boolean isPiggyBackingOnConfluenceEvaluationLicense(LicenseAbstract confluenceLicenseAbstract) {
        return StringUtils.isBlank(this.getStoredLicense()) && confluenceLicenseAbstract.isEvaluation() && !confluenceLicenseAbstract.isExpired();
    }

    private LicenseAbstract getConfluenceLicenseAbstract() {
        ConfluenceLicense confluenceLicense = null;
        try {
            confluenceLicense = this.getLicenseService().validate(this.getProductLicenseString());
        }
        catch (LicenseException confluenceLicenseProblem) {
            LOG.error("There seems to be a problem with the Confluence license...", (Throwable)confluenceLicenseProblem);
        }
        return new LicenseAbstract((ProductLicense)confluenceLicense, this.jodaIcal4jTimeZoneMapper, this.localeManager);
    }

    private String getProductLicenseString() throws LicenseException {
        return this.confluenceLicenseHelper.getProductLicenseString();
    }

    private boolean isPiggyBackingOnConfluenceEvaluationLicense() {
        return this.isPiggyBackingOnConfluenceEvaluationLicense(this.getConfluenceLicenseAbstract());
    }
}

