/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.applinks.spi.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.sal.api.net.RequestFactory;

public interface AutoConfiguringAuthenticatorProviderPluginModule
extends AuthenticationProviderPluginModule {
    public boolean isApplicable(AuthenticationScenario var1, ApplicationLink var2);

    public void enable(RequestFactory var1, ApplicationLink var2) throws AuthenticationConfigurationException;

    public void disable(RequestFactory var1, ApplicationLink var2) throws AuthenticationConfigurationException;
}

