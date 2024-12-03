/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.cors.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.CorsAuthenticationProvider;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;

public class CorsAuthenticationProviderPluginModule
implements AuthenticationProviderPluginModule {
    public static final String SERVLET_LOCATION = "/plugins/servlet/applinks/auth/conf/cors/";
    private final HostApplication hostApplication;

    public CorsAuthenticationProviderPluginModule(HostApplication hostApplication) {
        this.hostApplication = hostApplication;
    }

    public AuthenticationProvider getAuthenticationProvider(ApplicationLink link) {
        return null;
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return CorsAuthenticationProvider.class;
    }

    public String getConfigUrl(ApplicationLink link, Version version, AuthenticationDirection direction, HttpServletRequest request) {
        String url = null;
        if (AuthenticationDirection.INBOUND == direction) {
            url = RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + SERVLET_LOCATION + link.getId();
        } else if (this.isCorsSupportedOn(version)) {
            url = link.getDisplayUrl() + SERVLET_LOCATION + this.hostApplication.getId();
        }
        return url;
    }

    private boolean isCorsSupportedOn(Version version) {
        boolean supported = false;
        if (version != null) {
            int check = version.getMajor() * 10 + version.getMinor();
            supported = check >= 37;
        }
        return supported;
    }
}

