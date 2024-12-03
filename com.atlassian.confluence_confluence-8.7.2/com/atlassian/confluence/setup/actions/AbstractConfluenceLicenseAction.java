/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  io.atlassian.fugue.Either
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.license.LicenseWebFacade;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import io.atlassian.fugue.Either;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class AbstractConfluenceLicenseAction
extends AbstractSetupAction {
    public static final String CONF_LICENSE_STRING = "confLicenseString";
    protected LicenseWebFacade webLicenseFacade;
    protected String confLicenseString;

    protected void validateLicense() {
        Either<String, ConfluenceLicense> validationResult = this.webLicenseFacade.validateLicense(this.getConfLicenseString());
        if (validationResult.isLeft()) {
            this.addFieldError(CONF_LICENSE_STRING, (String)validationResult.left().get());
        }
    }

    public void setWebLicenseFacade(LicenseWebFacade webLicenseFacade) {
        this.webLicenseFacade = webLicenseFacade;
    }

    public String getConfLicenseString() {
        return this.confLicenseString;
    }

    public void setConfLicenseString(String confLicenseString) {
        this.confLicenseString = confLicenseString;
    }

    protected Optional<String> getConfiguredLicenseString() {
        return Optional.ofNullable(this.getBootstrapStatusProvider().getApplicationConfig()).map(cfg -> (String)cfg.getProperty((Object)"atlassian.license.message")).filter(StringUtils::isNotBlank);
    }
}

