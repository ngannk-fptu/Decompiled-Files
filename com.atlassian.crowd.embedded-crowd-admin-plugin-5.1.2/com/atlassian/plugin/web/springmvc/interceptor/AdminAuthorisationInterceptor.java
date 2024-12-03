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

public final class AdminAuthorisationInterceptor
extends AuthorisationInterceptor {
    public AdminAuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties) {
        super(userManager, loginUriProvider, applicationProperties);
    }

    public AdminAuthorisationInterceptor(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties, BundleContext bundleContext) {
        super(userManager, loginUriProvider, applicationProperties, bundleContext);
    }

    @Override
    protected boolean checkPermission(UserKey remoteKey) {
        return this.userManager.isAdmin(remoteKey);
    }

    @Override
    protected UserRole getRole() {
        return UserRole.ADMIN;
    }
}

