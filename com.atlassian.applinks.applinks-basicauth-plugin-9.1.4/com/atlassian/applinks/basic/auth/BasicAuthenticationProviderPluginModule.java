/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.sal.api.net.RequestFactory
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.basic.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.basic.auth.BasicAuthRequestFactoryImpl;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.sal.api.net.RequestFactory;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;

public class BasicAuthenticationProviderPluginModule
implements AuthenticationProviderPluginModule {
    private static final String SERVLET_LOCATION = "/plugins/servlet/applinks/auth/conf/basic/";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final HostApplication hostApplication;
    private final RequestFactory requestFactory;

    public BasicAuthenticationProviderPluginModule(AuthenticationConfigurationManager authenticationConfigurationManager, InternalHostApplication hostApplication, RequestFactory requestFactory) {
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.hostApplication = hostApplication;
        this.requestFactory = requestFactory;
    }

    public BasicAuthenticationProvider getAuthenticationProvider(final ApplicationLink link) {
        BasicAuthenticationProvider provider = null;
        if (this.authenticationConfigurationManager.isConfigured(link.getId(), BasicAuthenticationProvider.class)) {
            provider = new BasicAuthenticationProvider(){

                public ApplicationLinkRequestFactory getRequestFactory() {
                    return new BasicAuthRequestFactoryImpl(BasicAuthenticationProviderPluginModule.this.authenticationConfigurationManager, link, BasicAuthenticationProviderPluginModule.this.requestFactory);
                }
            };
        }
        return provider;
    }

    public String getConfigUrl(ApplicationLink link, Version applicationLinksVersion, AuthenticationDirection direction, HttpServletRequest request) {
        String baseUrl;
        if (direction == AuthenticationDirection.INBOUND) {
            if (link == null || applicationLinksVersion == null) {
                return null;
            }
            baseUrl = link.getDisplayUrl() + SERVLET_LOCATION + this.hostApplication.getId();
        } else {
            baseUrl = RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + SERVLET_LOCATION + link.getId().toString();
        }
        return baseUrl;
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return BasicAuthenticationProvider.class;
    }
}

