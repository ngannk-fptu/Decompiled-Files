/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.admin.actions.AbstractUpdateLicenseAction;
import com.atlassian.confluence.event.events.admin.ViewLicenseEvent;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.event.Event;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Optional;

@WebSudoRequired
@AdminOnly
public class ViewOrUpdateLicenseAction
extends AbstractUpdateLicenseAction {
    @Override
    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) || this.permissionManager.isSystemAdministrator(this.getAuthenticatedUser());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.eventManager.publishEvent((Event)new ViewLicenseEvent(this));
        return "success";
    }

    public boolean isEvaluationLicense() {
        return Optional.ofNullable(this.getConfluenceLicense()).map(ProductLicense::isEvaluation).orElse(false);
    }

    @Override
    public String doUpdate() throws Exception {
        return super.doUpdate();
    }
}

