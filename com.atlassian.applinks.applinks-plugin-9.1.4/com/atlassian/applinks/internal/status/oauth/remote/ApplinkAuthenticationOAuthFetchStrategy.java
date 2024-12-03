/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.status.oauth.remote;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.net.ResponseContentException;
import com.atlassian.applinks.internal.common.net.ResponsePreconditions;
import com.atlassian.applinks.internal.common.status.oauth.OAuthConfig;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.RestVersion;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.applinks.internal.rest.client.RestRequestBuilder;
import com.atlassian.applinks.internal.rest.model.auth.compatibility.RestApplicationLinkAuthentication;
import com.atlassian.applinks.internal.rest.model.auth.compatibility.RestAuthenticationProvider;
import com.atlassian.applinks.internal.status.error.ApplinkErrorType;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import com.atlassian.applinks.internal.status.oauth.OAuthConfigs;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthConnectionVerifier;
import com.atlassian.applinks.internal.status.oauth.remote.OAuthStatusFetchStrategy;
import com.atlassian.applinks.internal.status.remote.ApplinkStatusAccessException;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

abstract class ApplinkAuthenticationOAuthFetchStrategy
implements OAuthStatusFetchStrategy {
    private static final String INSUFFICIENT_PERMISSION_MESSAGE = "Admin permission required to access remote application link authentication";
    private final Class<? extends AuthenticationProvider> authenticationProvider;
    private final OAuthConnectionVerifier oAuthConnectionVerifier;

    protected ApplinkAuthenticationOAuthFetchStrategy(@Nonnull Class<? extends AuthenticationProvider> authenticationProvider, @Nonnull OAuthConnectionVerifier oAuthConnectionVerifier) {
        this.authenticationProvider = Objects.requireNonNull(authenticationProvider, "authenticationProvider");
        this.oAuthConnectionVerifier = Objects.requireNonNull(oAuthConnectionVerifier, "oAuthConnectionVerifier");
    }

    @Override
    @Nullable
    public ApplinkOAuthStatus fetch(@Nonnull ApplicationId localId, @Nonnull ApplicationLink applink) throws ApplinkStatusException, ResponseException {
        try {
            AuthorisationUriAwareRequest request = new RestRequestBuilder(applink).authentication(this.authenticationProvider).url(this.getAuthenticationUrl(localId)).build();
            return (ApplinkOAuthStatus)request.execute(this.createRequestHandler(request, applink));
        }
        catch (CredentialsRequiredException e) {
            throw new ApplinkStatusAccessException(ApplinkErrorType.LOCAL_AUTH_TOKEN_REQUIRED, (AuthorisationURIGenerator)e, "Local OAuth token not established", e);
        }
    }

    private ApplinkAuthenticationOAuthStatusHandler createRequestHandler(AuthorisationUriAwareRequest request, ApplicationLink applink) {
        if (TwoLeggedOAuthWithImpersonationAuthenticationProvider.class.equals(this.authenticationProvider)) {
            return new ApplinkAuthentication2LoiOAuthStatusHandler(request.getAuthorisationUriGenerator(), applink, this.oAuthConnectionVerifier);
        }
        return new ApplinkAuthenticationOAuthStatusHandler(request.getAuthorisationUriGenerator());
    }

    protected abstract RestUrlBuilder getAuthenticationUrl(ApplicationId var1);

    static final class ApplinkAuthentication2LoiOAuthStatusHandler
    extends ApplinkAuthenticationOAuthStatusHandler {
        private final ApplicationLink applink;
        private final OAuthConnectionVerifier oAuthConnectionVerifier;

        ApplinkAuthentication2LoiOAuthStatusHandler(AuthorisationURIGenerator uriGenerator, ApplicationLink applink, OAuthConnectionVerifier oAuthConnectionVerifier) {
            super(uriGenerator);
            this.applink = applink;
            this.oAuthConnectionVerifier = oAuthConnectionVerifier;
        }

        @Override
        public ApplinkOAuthStatus handle(Response response) throws ResponseException {
            if (ApplinksOAuth.isAuthLevelDisabled(response)) {
                this.oAuthConnectionVerifier.verifyOAuthConnection(this.applink);
                return ApplinkOAuthStatus.DEFAULT;
            }
            return super.handle(response);
        }
    }

    private static class ApplinkAuthenticationOAuthStatusHandler
    implements ApplicationLinkResponseHandler<ApplinkOAuthStatus> {
        private static final Function<RestAuthenticationProvider, String> TO_PROVIDER_NAME = new Function<RestAuthenticationProvider, String>(){

            public String apply(RestAuthenticationProvider provider) {
                return provider.getProvider();
            }
        };
        private final AuthorisationURIGenerator uriGenerator;

        ApplinkAuthenticationOAuthStatusHandler(AuthorisationURIGenerator uriGenerator) {
            this.uriGenerator = uriGenerator;
        }

        public ApplinkOAuthStatus handle(Response response) throws ResponseException {
            if (response.getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
                return null;
            }
            if (response.getStatusCode() == Response.Status.FORBIDDEN.getStatusCode() || response.getStatusCode() == Response.Status.UNAUTHORIZED.getStatusCode()) {
                throw new ApplinkStatusAccessException(ApplinkErrorType.INSUFFICIENT_REMOTE_PERMISSION, this.uriGenerator, ApplinkAuthenticationOAuthFetchStrategy.INSUFFICIENT_PERMISSION_MESSAGE);
            }
            ResponsePreconditions.checkStatusOk(response);
            try {
                RestApplicationLinkAuthentication authenticationEntity = (RestApplicationLinkAuthentication)response.getEntity(RestApplicationLinkAuthentication.class);
                return new ApplinkOAuthStatus(this.getIncomingConfig(authenticationEntity), this.getOutgoingConfig(authenticationEntity));
            }
            catch (Exception e) {
                throw new ResponseContentException(response, e);
            }
        }

        public ApplinkOAuthStatus credentialsRequired(Response response) throws ResponseException {
            throw new ApplinkStatusAccessException(ApplinkErrorType.REMOTE_AUTH_TOKEN_REQUIRED, this.uriGenerator, "OAuth trust not established");
        }

        private OAuthConfig getIncomingConfig(RestApplicationLinkAuthentication restAuthentication) {
            if (Iterables.isEmpty(restAuthentication.getConsumers())) {
                return OAuthConfig.createDisabledConfig();
            }
            return (OAuthConfig)OAuthConfig.ORDER_BY_LEVEL.max(Iterables.transform(restAuthentication.getConsumers(), OAuthConfigs.FROM_REST_CONSUMER));
        }

        private OAuthConfig getOutgoingConfig(RestApplicationLinkAuthentication restAuthentication) {
            if (Iterables.isEmpty(restAuthentication.getConfiguredAuthenticationProviders())) {
                return OAuthConfig.createDisabledConfig();
            }
            ImmutableSet providers = ImmutableSet.copyOf((Iterable)Iterables.transform(restAuthentication.getConfiguredAuthenticationProviders(), TO_PROVIDER_NAME));
            boolean is3LoConfigured = this.isConfigured((Set<String>)providers, OAuthAuthenticationProvider.class);
            boolean is2LoConfigured = this.isConfigured((Set<String>)providers, TwoLeggedOAuthAuthenticationProvider.class);
            boolean is2LoIConfigured = this.isConfigured((Set<String>)providers, TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
            return OAuthConfig.fromConfig(is3LoConfigured, is2LoConfigured, is2LoIConfigured);
        }

        private boolean isConfigured(Set<String> providers, Class<? extends AuthenticationProvider> provider) {
            return providers.contains(provider.getCanonicalName());
        }
    }

    static final class For4x
    extends ApplinkAuthenticationOAuthFetchStrategy {
        For4x(@Nonnull Class<? extends AuthenticationProvider> authenticationProvider, @Nonnull OAuthConnectionVerifier oAuthConnectionVerifier) {
            super(authenticationProvider, oAuthConnectionVerifier);
        }

        @Override
        protected RestUrlBuilder getAuthenticationUrl(ApplicationId localId) {
            return new RestUrlBuilder().version(RestVersion.V2).addPath("applicationlink").addApplicationId(localId).addPath("authentication");
        }
    }

    static final class For5x
    extends ApplinkAuthenticationOAuthFetchStrategy {
        static final String APPLINKS_OAUTH_REST_MODULE = "applinks-oauth";

        For5x(@Nonnull Class<? extends AuthenticationProvider> authenticationProvider, @Nonnull OAuthConnectionVerifier oAuthConnectionVerifier) {
            super(authenticationProvider, oAuthConnectionVerifier);
        }

        @Override
        protected RestUrlBuilder getAuthenticationUrl(ApplicationId localId) {
            return new RestUrlBuilder().module(APPLINKS_OAUTH_REST_MODULE).addPath("applicationlink").addApplicationId(localId).addPath("authentication");
        }
    }
}

