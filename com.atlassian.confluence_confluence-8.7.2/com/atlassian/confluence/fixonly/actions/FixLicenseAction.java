/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.fixonly.actions;

import com.atlassian.confluence.admin.actions.AbstractUpdateLicenseAction;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public class FixLicenseAction
extends AbstractUpdateLicenseAction {
    private JohnsonEventContainer johnsonEventContainer;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.johnsonEventContainer.hasEvents();
    }

    public void setJohnsonEventContainer(JohnsonEventContainer johnsonEventContainer) {
        this.johnsonEventContainer = johnsonEventContainer;
    }
}

