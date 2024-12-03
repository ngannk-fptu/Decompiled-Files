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

@WebSudoRequired
@SystemAdminOnly
public class EvalLicenseAction
extends AbstractConfluenceLicenseAction {
    private static final String H2_DEV_MODE = "dev.test.h2db";

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        this.validateLicense();
        if (this.hasFieldErrors()) {
            return "error";
        }
        Either<String, ConfluenceLicense> installationResult = this.webLicenseFacade.installLicense(this.getConfLicenseString());
        if (installationResult.isLeft()) {
            this.addFieldError("confLicenseString", (String)installationResult.left().get());
            return "error";
        }
        boolean cdcLicensed = ((ConfluenceLicense)installationResult.right().get()).isClusteringEnabled();
        boolean isH2DevMode = Boolean.getBoolean(H2_DEV_MODE);
        String setupType = this.getSetupPersister().getSetupType();
        if (cdcLicensed && !isH2DevMode && !"standalone.to.cluster".equals(setupType)) {
            this.getSetupPersister().setSetupType("cluster");
            this.getSetupPersister().synchSetupStackWithConfigRecord("setuplicense");
        }
        this.getSetupPersister().progessSetupStep();
        return cdcLicensed && !isH2DevMode ? "cluster-setup" : "success";
    }
}

