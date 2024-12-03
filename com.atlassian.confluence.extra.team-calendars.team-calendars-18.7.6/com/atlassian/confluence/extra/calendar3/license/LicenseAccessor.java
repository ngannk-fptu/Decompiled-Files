/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.license.LicenseException
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.events.PluginLicensedEvent;
import com.atlassian.confluence.extra.calendar3.license.ConfluenceLicenseHelper;
import com.atlassian.confluence.extra.calendar3.license.LicenseAbstract;
import com.atlassian.confluence.extra.calendar3.license.LicenseServiceProvider;
import com.atlassian.confluence.extra.calendar3.license.LicenseVerifier;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.license.LicenseException;
import com.atlassian.upm.license.compatibility.CompatibleLicenseStatus;
import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.PluginLicenseManagerAccessor;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseAccessor
implements LicenseVerifier {
    private static final Logger LOG = LoggerFactory.getLogger(LicenseAccessor.class);
    private final ConfluenceLicenseHelper confluenceLicenseHelper;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final BuildInformationManager buildInformationManager;
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;
    protected final LicenseServiceProvider licenseServiceProvider;
    private final PluginLicenseManagerAccessor pluginLicenseManagerAccessor;
    private final EventPublisher eventPublisher;
    private final DocumentationBeanFactory documentationBeanFactory;

    public LicenseAccessor(ConfluenceLicenseHelper confluenceLicenseHelper, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper, PluginLicenseManagerAccessor pluginLicenseManagerAccessor, EventPublisher eventPublisher, LicenseServiceProvider licenseServiceProvider, DocumentationBeanFactory documentationBeanFactory) {
        this.confluenceLicenseHelper = confluenceLicenseHelper;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.buildInformationManager = buildInformationManager;
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
        this.pluginLicenseManagerAccessor = pluginLicenseManagerAccessor;
        this.eventPublisher = eventPublisher;
        this.licenseServiceProvider = licenseServiceProvider;
        this.documentationBeanFactory = documentationBeanFactory;
    }

    public LicenseAbstract getLicenseAbstract() {
        return new LicenseAbstract(this.getPluginLicenseManager().getCurrentLicense(), this.jodaIcal4jTimeZoneMapper, this.localeManager);
    }

    private ProductLicense getLicense() {
        return this.getPluginLicenseManager().getCurrentLicense();
    }

    public void deleteLicense() {
        this.getPluginLicenseManager().setLicense(null);
    }

    public Collection<String> updateLicense(String licenseString) {
        Collection<String> errorMessages = this.validateLicense(licenseString);
        if (errorMessages.isEmpty()) {
            this.getPluginLicenseManager().setLicense(licenseString);
            this.eventPublisher.publish((Object)new PluginLicensedEvent(this, AuthenticatedUserThreadLocal.get()));
        }
        return new LinkedHashSet<String>(errorMessages);
    }

    public Collection<String> validateLicense(String licenseString) {
        CompatibleLicenseStatus status = this.isPiggyBackingOnConfluenceEvaluationLicense() ? CompatibleLicenseStatus.EVALUATION : this.getPluginLicenseManager().getLicenseStatus(licenseString);
        return this.getValidationErrorMessages(status);
    }

    public String getLicenseManagerUrl() {
        return this.getPluginLicenseManager().getPluginLicenseAdministrationUri().toASCIIString();
    }

    public boolean useUpmPluginLicenseManager() {
        return this.pluginLicenseManagerAccessor.isUpmPluginLicenseManagerResolved();
    }

    protected Collection<String> getValidationErrorMessages(CompatibleLicenseStatus status) {
        LicenseAbstract calendarPluginLicenseAbstract = this.getLicenseAbstract();
        LicenseAbstract confluenceLicenseAbstract = this.getConfluenceLicenseAbstract();
        LinkedHashSet<String> messages = new LinkedHashSet<String>();
        I18NBean i18nBean = this.getI18NBean(AuthenticatedUserThreadLocal.get());
        switch (status) {
            case ACTIVE: 
            case EVALUATION: {
                break;
            }
            case EVALUATION_EXPIRED: 
            case EXPIRED: {
                messages.add(i18nBean.getText("calendar3.licensing.expired"));
                break;
            }
            case INACTIVE: {
                messages.add(i18nBean.getText("calendar3.licensing.notsetup"));
                break;
            }
            case INCOMPATIBLE_FORMAT: {
                break;
            }
            case INCOMPATIBLE_TYPE: {
                messages.add(i18nBean.getText("calendar3.licensing.productandpluginlicensetypenotthesame", Arrays.asList(calendarPluginLicenseAbstract.getLicenseTypeName(), confluenceLicenseAbstract.getLicenseTypeName())));
                break;
            }
            case INVALID: {
                messages.add(i18nBean.getText("calendar3.licensing.invalid"));
                break;
            }
            case MAINTENANCE_EXPIRED: {
                messages.add(i18nBean.getText("calendar3.licensing.buildtoonew", Collections.singletonList(this.buildInformationManager.getVersion())));
                break;
            }
            case REQUIRES_RESTART: {
                break;
            }
            case USER_MISMATCH: {
                int maxActiveUsersAllowed = calendarPluginLicenseAbstract.getNumberOfUserLicensed();
                messages.add(i18nBean.getText("calendar3.licensing.toomanyactiveusers", Arrays.asList(maxActiveUsersAllowed, confluenceLicenseAbstract.getNumberOfUserLicensed())));
                break;
            }
        }
        return messages;
    }

    private I18NBean getI18NBean(ConfluenceUser user) {
        return this.i18NBeanFactory.getI18NBean(this.getUserLocale(user));
    }

    private Locale getUserLocale(ConfluenceUser user) {
        return this.localeManager.getLocale((User)user);
    }

    public boolean isLicenseSetup() {
        return this.isPiggyBackingOnConfluenceEvaluationLicense() || this.getLicense() != null;
    }

    @Override
    public boolean isLicenseExpired() {
        CompatibleLicenseStatus status = this.getPluginLicenseManager().getCurrentLicenseStatus();
        return status == CompatibleLicenseStatus.EXPIRED || status == CompatibleLicenseStatus.EVALUATION_EXPIRED;
    }

    @Override
    public boolean isLicenseInvalidated() {
        return this.isLicenseExpired() || !this.getInvalidLicenseReasons().isEmpty();
    }

    public Collection<String> getInvalidLicenseReasons() {
        CompatibleLicenseStatus status = this.getPluginLicenseManager().getCurrentLicenseStatus();
        boolean isProductDCLicense = this.getConfluenceLicenseAbstract().isDataCenter();
        boolean isPluginDCLicense = this.getLicenseAbstract().isDataCenter();
        if (CompatibleLicenseStatus.ACTIVE.equals((Object)status) && isProductDCLicense != isPluginDCLicense) {
            I18NBean i18nBean = this.getI18NBean(AuthenticatedUserThreadLocal.get());
            return Lists.newArrayList((Object[])new String[]{i18nBean.getText("calendar3.licensing.invalid.dcapp.license.usage", Collections.singletonList(this.documentationBeanFactory.getDocumentationBean().getLink("help.teamcal.migration.server.dc")))});
        }
        return this.getValidationErrorMessages(status);
    }

    @Override
    @Deprecated
    public boolean isOnDemandLicense() {
        return false;
    }

    private boolean isPiggyBackingOnConfluenceEvaluationLicense(LicenseAbstract confluenceLicenseAbstract) {
        return this.getLicense() == null && confluenceLicenseAbstract.isEvaluation() && !confluenceLicenseAbstract.isExpired();
    }

    protected LicenseAbstract getConfluenceLicenseAbstract() {
        ProductLicense confluenceLicense = null;
        try {
            confluenceLicense = this.licenseServiceProvider.getLicenseService().validate(this.getProductLicenseString(), Product.CONFLUENCE);
        }
        catch (LicenseException confluenceLicenseProblem) {
            LOG.error("There seems to be a problem with the Confluence license...", (Throwable)confluenceLicenseProblem);
        }
        return new LicenseAbstract(confluenceLicense, this.jodaIcal4jTimeZoneMapper, this.localeManager);
    }

    private String getProductLicenseString() throws LicenseException {
        return this.confluenceLicenseHelper.getProductLicenseString();
    }

    public boolean isPiggyBackingOnConfluenceEvaluationLicense() {
        return this.isPiggyBackingOnConfluenceEvaluationLicense(this.getConfluenceLicenseAbstract());
    }

    private CompatiblePluginLicenseManager getPluginLicenseManager() {
        return this.pluginLicenseManagerAccessor.getPluginLicenseManager();
    }
}

