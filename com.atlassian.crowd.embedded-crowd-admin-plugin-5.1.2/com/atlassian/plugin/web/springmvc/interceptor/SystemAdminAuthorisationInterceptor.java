/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserRole
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugin.web.springmvc.interceptor;

import com.atlassian.plugin.web.springmvc.interceptor.AuthorisationInterceptor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserRole;
import org.osgi.framework.BundleContext;

public final class SystemAdminAuthorisationInterceptor
extends AuthorisationInterceptor {
    public SystemAdminAuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties) {
        super(userManager, loginUriProvider, applicationProperties);
    }

    public SystemAdminAuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties, BundleContext bundleContext) {
        super(userManager, loginUriProvider, applicationProperties, bundleContext);
    }

    @Override
    protected UserRole getRole() {
        return UserRole.SYSADMIN;
    }

    @Override
    protected boolean checkPermission(UserKey userKey) {
        return this.userManager.isSystemAdmin(userKey);
    }
}

