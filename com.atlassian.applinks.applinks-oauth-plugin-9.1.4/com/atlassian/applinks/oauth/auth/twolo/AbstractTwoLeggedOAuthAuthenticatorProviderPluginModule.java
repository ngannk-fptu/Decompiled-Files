/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.oauth.auth.twolo;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.oauth.auth.OAuthAuthenticatorProviderPluginModule;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;

public abstract class AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule
implements AuthenticationProviderPluginModule,
IncomingTrustAuthenticationProviderPluginModule {
    private final InternalHostApplication hostApplication;

    public AbstractTwoLeggedOAuthAuthenticatorProviderPluginModule(InternalHostApplication hostApplication) {
        this.hostApplication = hostApplication;
    }

    public String getConfigUrl(ApplicationLink link, Version applicationLinksVersion, AuthenticationDirection direction, HttpServletRequest request) {
        boolean supportsAppLinks = applicationLinksVersion != null;
        boolean oAuthPluginInstalled = OAuthHelper.isOAuthPluginInstalled(link);
        if (direction == AuthenticationDirection.INBOUND) {
            if (supportsAppLinks || oAuthPluginInstalled) {
                return RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + OAuthAuthenticatorProviderPluginModule.ADD_CONSUMER_BY_URL_SERVLET_LOCATION + link.getId().toString() + "?" + "uiposition" + "=local";
            }
            return RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + OAuthAuthenticatorProviderPluginModule.ADD_CONSUMER_MANUALLY_SERVLET_LOCATION + link.getId().toString();
        }
        return null;
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return OAuthAuthenticationProvider.class;
    }
}

