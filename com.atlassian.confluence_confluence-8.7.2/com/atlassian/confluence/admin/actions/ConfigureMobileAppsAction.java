/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.util.MobileUtils;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@WebSudoRequired
@AdminOnly
public class ConfigureMobileAppsAction
extends ConfluenceActionSupport {
    @Override
    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public boolean isMobilePluginEnabled() {
        return this.pluginAccessor.isPluginEnabled(MobileUtils.getMobilePluginKey());
    }
}

