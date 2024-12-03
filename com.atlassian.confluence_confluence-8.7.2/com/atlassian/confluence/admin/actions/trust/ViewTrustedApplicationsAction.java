/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.trust;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.admin.actions.trust.AbstractTrustedApplicationAction;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;

@WebSudoRequired
@AdminOnly
public class ViewTrustedApplicationsAction
extends AbstractTrustedApplicationAction {
    private Collection applications;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        this.applications = this.trustedApplicationsManager.getAllTrustedApplications();
        return super.execute();
    }

    public Collection getTrustedApplications() {
        return this.applications;
    }
}

