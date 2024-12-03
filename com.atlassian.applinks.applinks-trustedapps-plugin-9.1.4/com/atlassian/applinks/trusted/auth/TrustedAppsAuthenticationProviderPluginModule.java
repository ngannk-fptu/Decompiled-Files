/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet
 *  com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate$Type
 *  com.atlassian.applinks.core.util.Holder
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.spi.auth.AuthenticationDirection
 *  com.atlassian.applinks.spi.auth.AuthenticationScenario
 *  com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  javax.servlet.http.HttpServletRequest
 *  org.osgi.framework.Version
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet;
import com.atlassian.applinks.core.auth.OrphanedTrustAwareAuthenticatorProviderPluginModule;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.core.util.Holder;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.spi.auth.AuthenticationDirection;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.auth.IncomingTrustAuthenticationProviderPluginModule;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.applinks.trusted.auth.TrustedApplicationsRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Version;

public class TrustedAppsAuthenticationProviderPluginModule
implements OrphanedTrustAwareAuthenticatorProviderPluginModule,
IncomingTrustAuthenticationProviderPluginModule {
    public static final String CONSUMER_SERVLET_LOCATION_UAL = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/trusted/outbound-ual/";
    public static final String CONSUMER_SERVLET_LOCATION_LEGACY = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/trusted/outbound-non-ual/";
    public static final String PROVIDER_SERVLET_LOCATION_UAL = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/trusted/inbound-ual/";
    public static final String PROVIDER_SERVLET_LOCATION_LEGACY = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/trusted/inbound-non-ual/";
    public static final String AUTOCONFIGURE_SERVLET_LOCATION = ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/trusted/autoconfig/";
    private final HostApplication hostApplication;
    private final AuthenticationConfigurationManager configurationManager;
    private final TrustedApplicationsManager trustedApplicationsManager;
    private final RequestFactory requestFactory;
    private final TrustConfigurator trustConfigurator;
    private final UserManager userManager;

    public TrustedAppsAuthenticationProviderPluginModule(HostApplication hostApplication, AuthenticationConfigurationManager configurationManager, TrustedApplicationsManager trustedApplicationsManager, RequestFactory requestFactory, TrustConfigurator trustConfigurator, UserManager userManager) {
        this.hostApplication = hostApplication;
        this.configurationManager = configurationManager;
        this.requestFactory = requestFactory;
        this.trustedApplicationsManager = trustedApplicationsManager;
        this.trustConfigurator = trustConfigurator;
        this.userManager = userManager;
    }

    public AuthenticationProvider getAuthenticationProvider(ApplicationLink link) {
        TrustedAppsAuthenticationProvider provider = null;
        if (this.configurationManager.isConfigured(link.getId(), this.getAuthenticationProviderClass())) {
            provider = new TrustedAppsAuthenticationProvider(){

                public ApplicationLinkRequestFactory getRequestFactory(String username) {
                    return new TrustedApplicationsRequestFactory(TrustedAppsAuthenticationProviderPluginModule.this.trustedApplicationsManager.getCurrentApplication(), TrustedAppsAuthenticationProviderPluginModule.this.requestFactory, TrustedAppsAuthenticationProviderPluginModule.this.userManager);
                }
            };
        }
        return provider;
    }

    public String getConfigUrl(ApplicationLink link, Version applicationLinksVersion, AuthenticationDirection direction, HttpServletRequest request) {
        boolean peerHasUAL = applicationLinksVersion != null;
        switch (direction) {
            case INBOUND: {
                return URIUtil.uncheckedConcatenate((URI)RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()), (String[])new String[]{(peerHasUAL ? PROVIDER_SERVLET_LOCATION_UAL : PROVIDER_SERVLET_LOCATION_LEGACY) + link.getId().toString()}).toString();
            }
        }
        if (peerHasUAL) {
            return URIUtil.uncheckedConcatenate((URI)link.getDisplayUrl(), (String[])new String[]{PROVIDER_SERVLET_LOCATION_UAL + this.hostApplication.getId().toString()}) + "?" + AbstractAdminOnlyAuthServlet.HOST_URL_PARAM + "=" + URIUtil.utf8Encode((URI)RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()));
        }
        return URIUtil.uncheckedConcatenate((URI)RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()), (String[])new String[]{CONSUMER_SERVLET_LOCATION_LEGACY + link.getId().toString()}).toString();
    }

    public Class<? extends AuthenticationProvider> getAuthenticationProviderClass() {
        return TrustedAppsAuthenticationProvider.class;
    }

    public void enable(RequestFactory authenticatedRequestFactory, ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.enableRemoteTrust(authenticatedRequestFactory, applicationLink);
        try {
            this.trustConfigurator.issueInboundTrust(applicationLink);
            this.trustConfigurator.issueOutboundTrust(applicationLink);
        }
        catch (TrustConfigurator.ConfigurationException ce) {
            throw new AuthenticationConfigurationException("Error configuring Trusted Applications: " + ce.getMessage(), (Throwable)ce);
        }
    }

    private void enableRemoteTrust(RequestFactory<Request<Request<?, Response>, Response>> requestFactory, ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.configureRemoteTrust(requestFactory, applicationLink, Request.MethodType.PUT);
    }

    private void disableRemoteTrust(RequestFactory requestFactory, ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.configureRemoteTrust(requestFactory, applicationLink, Request.MethodType.DELETE);
    }

    private void configureRemoteTrust(RequestFactory<Request<Request<?, Response>, Response>> requestFactory, ApplicationLink applicationLink, Request.MethodType action) throws AuthenticationConfigurationException {
        final Holder success = new Holder((Object)false);
        final Holder errorMessage = new Holder();
        URI autoConfigUrl = URIUtil.uncheckedConcatenate((URI)applicationLink.getRpcUrl(), (String[])new String[]{AUTOCONFIGURE_SERVLET_LOCATION + this.hostApplication.getId().toString()});
        try {
            Request request = requestFactory.createRequest(action, autoConfigUrl.toString());
            request.addHeader("X-Atlassian-Token", "no-check");
            request.execute((ResponseHandler)new ResponseHandler<Response>(){

                public void handle(Response response) throws ResponseException {
                    if (response.isSuccessful()) {
                        success.set((Object)true);
                    } else {
                        errorMessage.set((Object)String.format("Response code: %d: %s", response.getStatusCode(), response.getResponseBodyAsString()));
                    }
                }
            });
        }
        catch (ResponseException re) {
            errorMessage.set((Object)("Communication error: " + re.getMessage()));
        }
        if (!((Boolean)success.get()).booleanValue()) {
            throw new AuthenticationConfigurationException("Error configuring peer: " + (String)errorMessage.get());
        }
    }

    public boolean isApplicable(AuthenticationScenario authenticationScenario, ApplicationLink applicationLink) {
        return false;
    }

    public boolean isApplicable(String certificateType) {
        return OrphanedTrustCertificate.Type.TRUSTED_APPS.name().equals(certificateType);
    }

    public void disable(RequestFactory authenticatedRequestFactory, ApplicationLink applicationLink) throws AuthenticationConfigurationException {
        this.trustConfigurator.revokeInboundTrust(applicationLink);
        this.trustConfigurator.revokeOutboundTrust(applicationLink);
        this.disableRemoteTrust(authenticatedRequestFactory, applicationLink);
    }

    public boolean incomingEnabled(ApplicationLink applicationLink) {
        return this.trustConfigurator.inboundTrustEnabled(applicationLink);
    }
}

