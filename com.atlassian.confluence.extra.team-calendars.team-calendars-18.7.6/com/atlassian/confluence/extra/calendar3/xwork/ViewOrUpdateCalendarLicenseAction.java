/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.license.LicenseAbstract;
import com.atlassian.confluence.extra.calendar3.license.LicenseAccessor;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

@ReadOnlyAccessAllowed
@WebSudoRequired
public class ViewOrUpdateCalendarLicenseAction
extends ConfluenceActionSupport
implements BreadcrumbAware {
    private static final String REDIRECT_UPM = "redirectupm";
    private LicenseAccessor licenseAccessor;
    private SystemInformationService systemInformationService;
    private String licenseString;
    private boolean licenseUpdated;
    private String deleteLicense;

    public void setLicenseAccessor(LicenseAccessor licenseAccessor) {
        this.licenseAccessor = licenseAccessor;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    public LicenseAbstract getLicenseAbstract() {
        return this.licenseAccessor.getLicenseAbstract();
    }

    public String getLicenseString() {
        return this.licenseString;
    }

    public void setLicenseString(String licenseString) {
        this.licenseString = licenseString;
    }

    public boolean isLicenseUpdated() {
        return this.licenseUpdated;
    }

    public void setLicenseUpdated(boolean licenseUpdated) {
        this.licenseUpdated = licenseUpdated;
    }

    public String getDeleteLicense() {
        return this.deleteLicense;
    }

    public void setDeleteLicense(String deleteLicense) {
        this.deleteLicense = deleteLicense;
    }

    public boolean isLicenseSetup() {
        return this.licenseAccessor.isLicenseSetup();
    }

    public boolean isPiggyBackingOnConfluenceEvaluationLicense() {
        return this.licenseAccessor.isPiggyBackingOnConfluenceEvaluationLicense();
    }

    public boolean isOnDemandLicense() {
        return false;
    }

    public String getEvaluationLicenseMacDanceUrl(HttpServletRequest httpServletRequest) {
        return String.format("https://my.atlassian.com/license/evaluation?product=%s&licensefieldname=licenseString&version=%s&build=%s&sid=%s&callback=%s&ref=prod", GeneralUtil.urlEncode((String)"Team Calendars"), GeneralUtil.urlEncode((String)GeneralUtil.getVersionNumber()), GeneralUtil.getBuildNumber(), GeneralUtil.urlEncode((String)this.systemInformationService.getConfluenceInfo().getServerId()), GeneralUtil.urlEncode((String)this.getMacDanceCallbackUrl(httpServletRequest)));
    }

    private String getMacDanceCallbackUrl(HttpServletRequest httpServletRequest) {
        return String.format("%s/admin/calendar/viewlicense.action", GeneralUtil.lookupDomainName((HttpServletRequest)httpServletRequest));
    }

    public String doDefault() throws Exception {
        return this.licenseAccessor.useUpmPluginLicenseManager() ? REDIRECT_UPM : super.doDefault();
    }

    public String execute() throws Exception {
        if (this.licenseAccessor.useUpmPluginLicenseManager()) {
            return REDIRECT_UPM;
        }
        if (StringUtils.isBlank(this.getDeleteLicense())) {
            for (String licenseUpdateError : this.licenseAccessor.updateLicense(this.getLicenseString())) {
                this.addActionError(licenseUpdateError);
            }
        } else {
            this.licenseAccessor.deleteLicense();
        }
        return this.hasActionErrors() ? "input" : "success";
    }

    public void validate() {
        if (this.licenseAccessor.useUpmPluginLicenseManager()) {
            return;
        }
        super.validate();
        if (StringUtils.isBlank(this.getDeleteLicense())) {
            if (StringUtils.isBlank(this.getLicenseString())) {
                this.addFieldError("licenseString", this.getText("calendar3.licensing.invalid"));
            } else {
                Collection<String> licenseValidationErrors = this.licenseAccessor.validateLicense(this.getLicenseString());
                for (String licenseValidationError : licenseValidationErrors) {
                    this.addFieldError("licenseString", licenseValidationError);
                }
            }
        }
    }

    protected List<String> getPermissionTypes() {
        List requiredPermissions = super.getPermissionTypes();
        requiredPermissions.add("ADMINISTRATECONFLUENCE");
        return requiredPermissions;
    }

    public Breadcrumb getBreadcrumb() {
        return AdminBreadcrumb.getInstance();
    }

    public Collection<String> getErrorsOfField(String fieldName) {
        if (this.hasFieldError(fieldName)) {
            return (Collection)this.getFieldErrors().get(fieldName);
        }
        return Collections.emptySet();
    }

    public boolean hasFieldError(String fieldName) {
        return this.getFieldErrors().containsKey(fieldName);
    }

    public Collection<String> getLicenseInvalidatedMessages() {
        return this.licenseAccessor.getInvalidLicenseReasons();
    }
}

