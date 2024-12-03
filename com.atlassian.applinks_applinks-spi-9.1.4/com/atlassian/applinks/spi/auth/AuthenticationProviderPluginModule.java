/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.google.common.base.Function
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.spi.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.google.common.base.Function;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;

public interface AuthenticationProviderPluginModule {
    public static final Function<AuthenticationProviderPluginModule, Class<? extends AuthenticationProvider>> TO_PROVIDER_CLASS = AuthenticationProviderPluginModule::getAuthenticationProviderClass;

    public AuthenticationProvider getAuthenticationProvider(ApplicationLink var1);

    public String getConfigUrl(ApplicationLink var1, Version var2, AuthenticationDirection var3, HttpServletRequest var4);

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass();
}

