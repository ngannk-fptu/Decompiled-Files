/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationException
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseStatusException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthAutoConfigurator;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthConfigurator;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.ConsumerInformationUnavailableException;
import com.atlassian.applinks.internal.common.exception.ServiceExceptionFactory;
import com.atlassian.applinks.internal.common.rest.model.oauth.RestConsumer;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.rest.RestUrl;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.RestVersion;
import com.atlassian.applinks.internal.rest.model.auth.compatibility.RestAuthenticationProvider;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOAuthAutoConfigurator
implements OAuthAutoConfigurator {
    private static final Logger log = LoggerFactory.getLogger(DefaultOAuthAutoConfigurator.class);
    private static final String APPLINKS_OAUTH_REST_MODULE = "applinks-oauth";
    private final InternalHostApplication internalHostApplication;
    private final RemoteCapabilitiesService capabilitiesService;
    private final OAuthConfigurator oAuthConfigurator;

    @Autowired
    public DefaultOAuthAutoConfigurator(AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerTokenStoreService consumerTokenStoreService, InternalHostApplication internalHostApplication, RemoteCapabilitiesService capabilitiesService, ServiceProviderStoreService serviceProviderStoreService, ServiceExceptionFactory serviceExceptionFactory) {
        this(internalHostApplication, capabilitiesService, new OAuthConfigurator(authenticationConfigurationManager, consumerTokenStoreService, serviceProviderStoreService, serviceExceptionFactory));
    }

    @VisibleForTesting
    DefaultOAuthAutoConfigurator(InternalHostApplication internalHostApplication, RemoteCapabilitiesService capabilitiesService, OAuthConfigurator oAuthConfigurator) {
        this.internalHostApplication = internalHostApplication;
        this.capabilitiesService = capabilitiesService;
        this.oAuthConfigurator = oAuthConfigurator;
    }

    @Override
    public void enable(@Nonnull OAuthConfig authLevel, @Nonnull ApplicationLink applink, @Nonnull RequestFactory requestFactory) throws AuthenticationConfigurationException {
        if (!authLevel.isEnabled()) {
            this.disable(applink, requestFactory);
        }
        RequestFactoryAdapter adapter = new RequestFactoryAdapter(requestFactory);
        this.enable(authLevel, authLevel, applink, adapter);
    }

    @Override
    public void enable(@Nonnull OAuthConfig incoming, @Nonnull OAuthConfig outgoing, @Nonnull ApplicationLink applink, @Nonnull ApplicationLinkRequestFactory requestFactory) throws AuthenticationConfigurationException {
        this.enable(incoming, outgoing, applink, new RequestFactoryAdapter(requestFactory));
    }

    private void enable(@Nonnull OAuthConfig incoming, @Nonnull OAuthConfig outgoing, @Nonnull ApplicationLink applink, @Nonnull RequestFactoryAdapter requestFactoryAdapter) throws AuthenticationConfigurationException {
        RemoteApplicationCapabilities capabilities = this.getCapabilitiesUnsecured(applink);
        if (DefaultOAuthAutoConfigurator.isApplinksPre40(capabilities)) {
            log.info("Remote Applinks version {} of applink '{}' is too old (pre-4.0). Skipping OAuth auto-configuration", (Object)capabilities.getApplinksVersion(), (Object)applink.getId());
            return;
        }
        if (capabilities.getCapabilities().contains((Object)ApplinksCapabilities.STATUS_API)) {
            this.remoteEnableUsingStatusApi(outgoing, incoming, applink, requestFactoryAdapter);
        } else {
            this.remoteEnableUsingAuthenticationApi(outgoing, incoming, applink, requestFactoryAdapter, capabilities);
        }
        this.setLocalOAuthConfig(applink, incoming, outgoing);
    }

    @Override
    public void disable(@Nonnull ApplicationLink applink, @Nonnull RequestFactory requestFactory) throws AuthenticationConfigurationException {
        this.disableInternal(applink, new RequestFactoryAdapter(requestFactory));
    }

    private void disableInternal(@Nonnull ApplicationLink applink, @Nonnull RequestFactoryAdapter requestFactoryAdapter) throws AuthenticationConfigurationException {
        RemoteApplicationCapabilities capabilities = this.getCapabilitiesUnsecured(applink);
        if (capabilities.getCapabilities().contains((Object)ApplinksCapabilities.STATUS_API)) {
            this.remoteDisableUsingStatusApi(applink, requestFactoryAdapter);
        } else {
            this.remoteDisableUsingAutoConfigurationServlet(applink, requestFactoryAdapter);
        }
        this.setLocalOAuthConfig(applink, OAuthConfig.createDisabledConfig(), OAuthConfig.createDisabledConfig());
    }

    private RemoteApplicationCapabilities getCapabilitiesUnsecured(ApplicationLink applink) throws AuthenticationConfigurationException {
        try {
            return this.capabilitiesService.getCapabilities(applink);
        }
        catch (Exception e) {
            throw new AuthenticationConfigurationException("Unexpected error when retrieving capabilities", (Throwable)e);
        }
    }

    private void setLocalOAuthConfig(ApplicationLink applink, OAuthConfig incoming, OAuthConfig outgoing) throws AuthenticationConfigurationException {
        try {
            this.oAuthConfigurator.updateIncomingConfig(applink, incoming);
            this.oAuthConfigurator.updateOutgoingConfig(applink, outgoing);
        }
        catch (ConsumerInformationUnavailableException e) {
            throw new AuthenticationConfigurationException((Throwable)e);
        }
    }

    private void remoteEnableUsingStatusApi(OAuthConfig incoming, OAuthConfig outgoing, ApplicationLink applink, RequestFactoryAdapter requestFactoryAdapter) throws AuthenticationConfigurationException {
        try {
            this.setRemoteStatus(new ApplinkOAuthStatus(incoming, outgoing), applink, requestFactoryAdapter);
        }
        catch (CredentialsRequiredException | ResponseException e) {
            throw new AuthenticationConfigurationException(e);
        }
    }

    private void remoteEnableUsingAuthenticationApi(OAuthConfig incoming, OAuthConfig outgoing, ApplicationLink applink, RequestFactoryAdapter requestFactoryAdapter, RemoteApplicationCapabilities capabilities) throws AuthenticationConfigurationException {
        try {
            if (incoming.isEnabled()) {
                DefaultOAuthAutoConfigurator.createDefaultJsonRequest(requestFactoryAdapter, Request.MethodType.PUT, this.getAuthenticationConsumerResourceUrl(applink, capabilities)).setEntity((Object)DefaultOAuthAutoConfigurator.getRestConsumer(incoming)).executeAndReturn((ReturningResponseHandler)LoggingReturningResponseHandler.INSTANCE);
            }
            if (outgoing.isEnabled()) {
                String authenticationProviderUrl = this.getAuthenticationProviderResourceUrl(applink);
                for (Class<? extends AuthenticationProvider> providerClass : DefaultOAuthAutoConfigurator.getProviders(outgoing)) {
                    DefaultOAuthAutoConfigurator.createDefaultJsonRequest(requestFactoryAdapter, Request.MethodType.PUT, authenticationProviderUrl).setEntity((Object)new RestAuthenticationProvider(providerClass)).executeAndReturn((ReturningResponseHandler)LoggingReturningResponseHandler.INSTANCE);
                }
            }
        }
        catch (CredentialsRequiredException | ResponseException e) {
            throw new AuthenticationConfigurationException(e);
        }
    }

    private void remoteDisableUsingStatusApi(ApplicationLink applink, RequestFactoryAdapter requestFactoryAdapter) throws AuthenticationConfigurationException {
        try {
            this.setRemoteStatus(ApplinkOAuthStatus.OFF, applink, requestFactoryAdapter);
        }
        catch (CredentialsRequiredException | ResponseException e) {
            throw new AuthenticationConfigurationException(e);
        }
    }

    private void remoteDisableUsingAutoConfigurationServlet(ApplicationLink applink, RequestFactoryAdapter requestFactoryAdapter) throws AuthenticationConfigurationException {
        try {
            DefaultOAuthAutoConfigurator.createDefaultRequest(requestFactoryAdapter, Request.MethodType.DELETE, this.getAutoConfigServletUrl(applink)).executeAndReturn((ReturningResponseHandler)LoggingReturningResponseHandler.INSTANCE);
        }
        catch (CredentialsRequiredException | ResponseException e) {
            throw new AuthenticationConfigurationException(e);
        }
    }

    private void setRemoteStatus(ApplinkOAuthStatus status, ApplicationLink applink, RequestFactoryAdapter requestFactoryAdapter) throws ResponseException, CredentialsRequiredException {
        DefaultOAuthAutoConfigurator.createDefaultJsonRequest(requestFactoryAdapter, Request.MethodType.PUT, this.getStatusResourceUrl(applink)).setEntity((Object)new RestApplinkOAuthStatus(status)).executeAndReturn((ReturningResponseHandler)LoggingReturningResponseHandler.INSTANCE);
    }

    private String getStatusResourceUrl(ApplicationLink applink) {
        return new RestUrlBuilder().to(applink).version(RestVersion.V3).addPath("status").addApplicationId(this.internalHostApplication.getId()).addPath("oauth").toString();
    }

    private String getAuthenticationConsumerResourceUrl(ApplicationLink applink, RemoteApplicationCapabilities capabilities) {
        RestUrlBuilder url = new RestUrlBuilder().to(applink);
        url = DefaultOAuthAutoConfigurator.isApplinks5OrLater(capabilities) ? url.module(APPLINKS_OAUTH_REST_MODULE).version(RestVersion.LATEST) : url.version(RestVersion.V2);
        return url.addPath("applicationlink").addApplicationId(this.internalHostApplication.getId()).addPath("authentication").addPath("consumer").queryParam("autoConfigure", Boolean.TRUE.toString()).toString();
    }

    private String getAuthenticationProviderResourceUrl(ApplicationLink applink) {
        return new RestUrlBuilder().to(applink).version(RestVersion.V2).addPath("applicationlink").addApplicationId(this.internalHostApplication.getId()).addPath("authentication").addPath("provider").toString();
    }

    private String getAutoConfigServletUrl(ApplicationLink applink) {
        return RestUrl.forPath(applink.getRpcUrl().toASCIIString()).add(ServletPathConstants.APPLINKS_CONFIG_SERVLET_URL).add("oauth").add("autoconfig").add(this.internalHostApplication.getId().toString()).toString();
    }

    private static Request<?, ?> createDefaultRequest(RequestFactoryAdapter requestFactoryAdapter, Request.MethodType methodType, String url) throws CredentialsRequiredException {
        return requestFactoryAdapter.createRequest(methodType, url).setFollowRedirects(true).addHeader("X-Atlassian-Token", "no-check");
    }

    private static Request<?, ?> createDefaultJsonRequest(RequestFactoryAdapter requestFactoryAdapter, Request.MethodType methodType, String url) throws CredentialsRequiredException {
        return DefaultOAuthAutoConfigurator.createDefaultRequest(requestFactoryAdapter, methodType, url).addHeader("Content-Type", "application/json").addHeader("Accept", "application/json");
    }

    private static boolean isApplinks5OrLater(RemoteApplicationCapabilities capabilities) {
        ApplicationVersion applinksVersion = capabilities.getApplinksVersion();
        return applinksVersion == null || applinksVersion.getMajor() >= 5;
    }

    private static boolean isApplinksPre40(RemoteApplicationCapabilities capabilities) {
        ApplicationVersion applinksVersion = capabilities.getApplinksVersion();
        return applinksVersion != null && applinksVersion.getMajor() < 4;
    }

    private static Iterable<Class<? extends AuthenticationProvider>> getProviders(OAuthConfig authLevel) {
        ImmutableList.Builder providers = ImmutableList.builder();
        if (authLevel.isEnabled()) {
            providers.add(OAuthAuthenticationProvider.class);
        }
        if (authLevel.isTwoLoEnabled()) {
            providers.add(TwoLeggedOAuthAuthenticationProvider.class);
        }
        if (authLevel.isTwoLoImpersonationEnabled()) {
            providers.add(TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
        }
        return providers.build();
    }

    private static RestConsumer getRestConsumer(OAuthConfig config) {
        RestConsumer consumer = new RestConsumer();
        consumer.put("twoLOAllowed", (Object)config.isTwoLoEnabled());
        consumer.put("twoLOImpersonationAllowed", (Object)config.isTwoLoImpersonationEnabled());
        return consumer;
    }

    private final class RequestFactoryAdapter {
        private final Object requestFactory;

        RequestFactoryAdapter(RequestFactory<?> requestFactory) {
            this.requestFactory = requestFactory;
        }

        RequestFactoryAdapter(ApplicationLinkRequestFactory requestFactory) {
            this.requestFactory = requestFactory;
        }

        public Request<?, ?> createRequest(Request.MethodType methodType, String url) throws CredentialsRequiredException {
            if (this.requestFactory instanceof RequestFactory) {
                return ((RequestFactory)this.requestFactory).createRequest(methodType, url);
            }
            return ((ApplicationLinkRequestFactory)this.requestFactory).createRequest(methodType, url);
        }
    }

    private static final class LoggingReturningResponseHandler
    implements ReturningResponseHandler<Response, String> {
        static final LoggingReturningResponseHandler INSTANCE = new LoggingReturningResponseHandler();

        private LoggingReturningResponseHandler() {
        }

        public String handle(Response response) throws ResponseException {
            String body = response.getResponseBodyAsString();
            Response.Status status = Response.Status.fromStatusCode((int)response.getStatusCode());
            if (status == null || status.getFamily() != Response.Status.Family.SUCCESSFUL) {
                log.warn("Unexpected response status: {}, body:\n\n{}", (Object)response.getStatusCode(), (Object)body);
                throw new ResponseStatusException("Unexpected response status: " + response.getStatusCode(), response);
            }
            return body;
        }
    }
}

