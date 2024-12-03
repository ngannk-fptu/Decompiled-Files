/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  io.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractConfluenceLicenseAction;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import io.atlassian.fugue.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupLicenseAction
extends AbstractConfluenceLicenseAction {
    private static final Logger log = LoggerFactory.getLogger(SetupLicenseAction.class);
    private static final String H2_DEV_MODE = "dev.test.h2db";

    @Override
    public void validate() {
        if (this.getConfLicenseString() == null) {
            this.getConfiguredLicenseString().ifPresent(this::setConfLicenseString);
        }
        this.validateLicense();
    }

    @Override
    public String doDefault() throws Exception {
        return this.getConfiguredLicenseString().map(license -> "skipToNextStep").orElse(super.doDefault());
    }

    @PermittedMethods(value={HttpMethod.POST, HttpMethod.GET})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        this.validate();
        if (this.hasFieldErrors()) {
            return "error";
        }
        Either<String, ConfluenceLicense> installationResult = this.webLicenseFacade.installLicense(this.getConfLicenseString());
        if (installationResult.isLeft()) {
            this.addFieldError("confLicenseString", (String)installationResult.left().get());
            return "error";
        }
        String setupType = this.getSetupPersister().getSetupType();
        boolean cdcLicensed = ((ConfluenceLicense)installationResult.right().get()).isClusteringEnabled();
        boolean isH2DevMode = Boolean.getBoolean(H2_DEV_MODE);
        log.debug("setupType = {}, cdcLicensed = {}", (Object)setupType, (Object)cdcLicensed);
        if (cdcLicensed && !isH2DevMode && !"standalone.to.cluster".equals(setupType)) {
            setupType = "cluster";
            this.getSetupPersister().setSetupType(setupType);
        }
        log.debug("setupType (after cluster decision) = {}", (Object)setupType);
        this.getSetupPersister().progessSetupStep();
        if ("custom".equals(setupType)) {
            return "custom-setup";
        }
        if ("install".equals(setupType)) {
            return "quick-setup";
        }
        if (this.getSetupPersister().isSetupTypeClustered()) {
            return "cluster-setup";
        }
        throw new RuntimeException("unexpected setup type " + setupType);
    }
}

